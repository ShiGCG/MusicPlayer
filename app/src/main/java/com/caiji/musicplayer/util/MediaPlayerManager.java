package com.caiji.musicplayer.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.caiji.musicplayer.model.MusicData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MediaPlayerManager {

    public static final int MODE_SINGLE_LOOP = 1;   //单曲循环
    public static final int MODE_LIST_LOOP = 2;     //列表循环
    public static final int MODE_RANDOM = 3;        //随机播放


    private int mode;
    private final List<MusicData> songs = new ArrayList<>();
    private int index = -1;
    private boolean prepared;
    private boolean notFirstPlay = false;
    private boolean isPlaying = false;
    private final Context context;

    @SuppressLint("StaticFieldLeak")
    private static MediaPlayerManager manager;
    private MediaPlayer mediaPlayer;
    private PlayStateListener listener;
    private final AudioFocusManager audioFocusManager;


    public interface PlayStateListener {
        void onStart();

        void onPause();

        void onPreparing();

        void onPrepared();

        void onError();

        void onCompletion();
    }

    public MediaPlayerManager(Context context) {
        this.context = context;
        audioFocusManager = new AudioFocusManager(context);
    }

    public static MediaPlayerManager getInstance(Context context) {
        manager = new MediaPlayerManager(context);
        return manager;
    }

    public static MediaPlayerManager getManager() {
        return manager;
    }

    //创建 初始化 传入歌单
    public void create(@NonNull List<MusicData> list) {
        songs.clear();
        songs.addAll(list);
        index = 0;
        mediaPlayer = new MediaPlayer();
        setDataSource(index);
        init();
    }

    //开始播放 需要准备好和没有播放
    public void start() {
        if (prepared && !isPlaying) {
            audioFocusManager.requestFocus();
        }
    }

    //暂停 需要准备好和在播放
    public void pause() {
        if (prepared && isPlaying) {
            audioFocusManager.releaseFocus();
        }
    }

    //根据播放状态调用相应方法
    public void startOrPause() {
        if (isPlaying)
            pause();
        else
            start();
    }

    //设置播放源
    private void setDataSource(int position) {
        mediaPlayer.reset(); //必须重置
        prepared = false;
        isPlaying = false;
        pause();
        if (listener != null) {
            listener.onPreparing();
            listener.onPause();
        }
        try {
            mediaPlayer.setDataSource(songs.get(position).getPlayUrl());
            mediaPlayer.prepareAsync();//异步准备
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //播放下一首
    public void playNext(int mode) {
        pause();
        if (songs.size() > 1)
            //根据播放模式计算index的值
            switch (mode) {
                case MODE_SINGLE_LOOP:
                    break;
                case MODE_LIST_LOOP:
                    if (index == songs.size() - 1) {
                        index = 0;
                    } else index++;
                    break;
                case MODE_RANDOM:
                    index = new Random().nextInt(songs.size() - 1);
                    break;
            }
        notFirstPlay = true;
        setDataSource(index);
    }

    //播放上一首
    public void playPrevious() {
        pause();
        if (songs.size() > 1)
            //根据播放模式计算index的值
            switch (mode) {
                case MODE_SINGLE_LOOP:
                    break;
                case MODE_LIST_LOOP:
                    if (index == 0)
                        index = songs.size() - 1;
                    else index--;
                    break;
                case MODE_RANDOM:
                    index = new Random().nextInt(songs.size() - 1);
                    break;
            }
        notFirstPlay = true;
        setDataSource(index);
    }

    //跳转到指定的播放位置 需要准备好
    public void seekTo(int position) {
        if (prepared) {
            mediaPlayer.seekTo(position);
        }
    }

    //添加一首歌 并且会从这首歌开始播放
    public void addSong(MusicData musicData) {
        if (!songs.contains(musicData)) {
            pause();
            index++;
            notFirstPlay = true;
            songs.add(index, musicData);
            setDataSource(index);
        }
    }

    //添加所有歌曲 会清空当前歌单
    public void addAllSongs(List<MusicData> list) {
        pause();
        songs.clear();
        notFirstPlay = true;
        songs.addAll(list);
        index = 0;
        setDataSource(index);
    }

    //删除某一首歌
    public void removeSong(int position) {
        if (position == index) {
            //如果删除当前播放的歌曲，顺序播放下一首歌
            pause();
            playNext(MODE_LIST_LOOP);
            songs.remove(position);
            index--;
            return;
        }
        if (position < index) {
            //先删除的歌位置靠上 index--
            index--;
            songs.remove(position);
            return;
        }
        songs.remove(position);
    }

    //播放指定歌曲 传入参数 播放歌曲的位置
    public void setPlaySongByPosition(int position) {
        if (position != index) {
            pause();
            index = position;
            setDataSource(position);
        }
    }

    //获取所有歌曲
    public List<MusicData> getAllSongs() {
        return songs;
    }

    //获取当前播放的歌曲
    public MusicData getCurrentMusicData() {
        Log.d("mediaPlayerManager", songs.get(index).toString());
        return songs.get(index);
    }

    //获取歌曲总时长 需要准备好 返回值为ms
    public int getDuration() {
        if (prepared) {
            return mediaPlayer.getDuration();
        } else return 0;
    }

    //获取当前播放位置，需要正在播放
    public int getCurrentPosition() {
        if (isPlaying) {
            return mediaPlayer.getCurrentPosition();
        } else return 0;
    }

    //获取是否是首次播放
    public boolean isNotFirstPlay() {
        return notFirstPlay;
    }

    //设置播放模式
    public void setPlayMode(int mode) {
        if (mode == MODE_LIST_LOOP || mode == MODE_SINGLE_LOOP || mode == MODE_RANDOM) {
            this.mode = mode;
        }
    }

    //获取当前的播放模式
    public int getPlayMode() {
        return mode;
    }

    //释放资源
    public void release() {
        if (mediaPlayer != null) {
            if (isPlaying)
                audioFocusManager.releaseFocus();
            mediaPlayer.release();
        }
        mediaPlayer = null;
    }

    //设置监听器
    public void setPlayStateListener(PlayStateListener listener) {
        this.listener = listener;
    }

    private void init() {
        //初始化
        //设置音频睡醒
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        //默认播放模式为列表循环
        mode = MODE_LIST_LOOP;
        //设置监听器
        mediaPlayer.setOnPreparedListener(mediaPlayer -> {
            prepared = true;
            if (listener != null) {
                listener.onPrepared();
            }
        });
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            if (listener != null) {
                listener.onCompletion();
            }
        });
        mediaPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
            isPlaying = false;
            prepared = false;
            if (listener != null) {
                listener.onError();
                listener.onPause();
            }
            //返回为true则不会继续执行 completion中的代码
            //如果false则会继续执行
            return true;
        });

        audioFocusManager.setOnAudioFocusChangeListener(focusChange -> {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS://永久失去音频焦点，不会再获取到焦点
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://暂时失去音频焦点
                    //暂停播放
                    isPlaying = false;
                    mediaPlayer.pause();
                    if (listener != null)
                        listener.onPause();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK://与其他应用共焦点
                    Toast.makeText(context, "与其他应用共享音频焦点", Toast.LENGTH_SHORT).show();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN://获取到焦点
                    //开始播放
                    isPlaying = true;
                    mediaPlayer.start();
                    if (listener != null) {
                        listener.onStart();
                    }
                    break;
            }
        });
    }
}
