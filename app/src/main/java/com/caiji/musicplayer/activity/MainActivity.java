package com.caiji.musicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.caiji.musicplayer.R;
import com.caiji.musicplayer.api.API;
import com.caiji.musicplayer.model.MusicData;
import com.caiji.musicplayer.model.MusicDetail;
import com.caiji.musicplayer.service.DownloadFileService;
import com.caiji.musicplayer.util.MediaPlayerManager;
import com.caiji.musicplayer.util.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    private static final int CODE_REQUEST_PERMISSION = 1;

    private Toolbar tb_main;                    //工具栏
    private ImageView iv_mp3_pic;               //专辑图片
    private TextView tx_mp3_song_name;          //歌曲名字
    private TextView tx_mp3_artist;             //歌手名字
    private SeekBar sk_mp3;                     //拖动条
    private TextView tx_mp3_current_time;       //当前播放进度显示
    private TextView tx_mp3_total_time;         //总时间显示
    private ImageButton ib_mp3_loop_mode;       //循环模式按钮
    private ImageButton ib_mp3_previous;        //下一首按钮
    private ImageButton ib_mp3_start_pause;     //开始或暂停按钮
    private ImageButton ib_mp3_next;            //下一首按钮
    private ImageButton ib_mp3_songs_list;      //歌曲列表按钮

    private List<MusicData> songs;          //歌曲列表
    private MediaPlayerManager manager;     //管理者
    private ObjectAnimator animator;        //动画
    private Intent serviceIntent;
    private ServiceConnection serviceConnection;
    private DownloadFileService.DownloadBinder downloadBinder;

    private boolean isPlaying = false;      //是否正在播放
    private boolean goThread = true;        //是否循环
    private boolean isAnimation = true;     //是否需要开启动画
    private long lastActionTime = 0;        //上次按下返回的时间
    private int playMode = MediaPlayerManager.MODE_LIST_LOOP;//循环模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVIew();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_REQUEST_PERMISSION);
        } else init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                //设置所有按钮都不可用
                sk_mp3.setEnabled(false);
                tb_main.setEnabled(false);
                ib_mp3_next.setEnabled(false);
                ib_mp3_previous.setEnabled(false);
                ib_mp3_loop_mode.setEnabled(false);
                ib_mp3_songs_list.setEnabled(false);
                ib_mp3_start_pause.setEnabled(false);
            } else init();
        }
    }

    private void init() {
        manager = MediaPlayerManager.getInstance(this);
        //启动活动并绑定
        serviceIntent = new Intent(this, DownloadFileService.class);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                downloadBinder = (DownloadFileService.DownloadBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        initData();
        viewConfig();
    }


    private void initVIew() {
        tb_main = findViewById(R.id.tb_main);
        iv_mp3_pic = findViewById(R.id.iv_mp3_pic);
        tx_mp3_song_name = findViewById(R.id.tx_mp3_song_name);
        tx_mp3_artist = findViewById(R.id.tx_mp3_artist);
        sk_mp3 = findViewById(R.id.sk_mp3);
        tx_mp3_current_time = findViewById(R.id.tx_mp3_current_time);
        tx_mp3_total_time = findViewById(R.id.tx_mp3_total_time);
        ib_mp3_loop_mode = findViewById(R.id.ib_mp3_loop_mode);
        ib_mp3_previous = findViewById(R.id.ib_mp3_previous);
        ib_mp3_start_pause = findViewById(R.id.ib_mp3_start_pause);
        ib_mp3_next = findViewById(R.id.ib_mp3_next);
        ib_mp3_songs_list = findViewById(R.id.ib_mp3_songs_list);

        animator = ObjectAnimator.ofFloat(iv_mp3_pic, "rotation", 0.0f, 360.0f);
        animator.setDuration(16 * 1000);
        animator.setRepeatCount(Animation.INFINITE);        //无限循环
        animator.setRepeatMode(ObjectAnimator.RESTART);     //循环模式
        animator.setInterpolator(new LinearInterpolator()); //匀速
    }

    private void initData() {
        //默认歌曲
        songs = new ArrayList<>();
        MusicData data1 = new MusicData();
        data1.setId("30612793");
        data1.setArtist("G.E.M.邓紫棋");
        data1.setPicUrl("http://p1.music.126.net/kVwk6b8Qdya8oDyGDcyAVA==/1364493930777368.jpg");
        data1.setName("多远都要在一起");
        songs.add(data1);
    }

    //设置监听器等
    private void viewConfig() {
        setSupportActionBar(tb_main);
        manager.setPlayMode(playMode);
        manager.setPlayStateListener(new MediaPlayerManager.PlayStateListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onStart() {
                //开始播放
                isPlaying = true;
                if (isAnimation)
                    animator.resume();
                ib_mp3_start_pause.setBackground(getDrawable(R.drawable.pause));
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onPause() {
                //暂停
                isPlaying = false;
                animator.pause();
                ib_mp3_start_pause.setBackground(getDrawable(R.drawable.start));
            }

            @Override
            public void onPreparing() {
                //准备中
                setDisplayDataWithoutPrepared();
            }

            @Override
            public void onPrepared() {
                //准备好
                setDisplayDataWithPrepared();
                if (manager.isNotFirstPlay())
                    manager.start();
            }

            @Override
            public void onError() {
                //出错
                Toast.makeText(MainActivity.this, "播放失败，可能是VIP歌曲", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompletion() {
                //播放完毕
                manager.playNext(playMode);
            }
        });
        manager.create(songs);

        sk_mp3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //显示拖动的位置
                tx_mp3_current_time.setText(getMinAndSec(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //触摸到，暂停播放
                if (isPlaying)
                    manager.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //松开，开始播放
                if (!isPlaying) {
                    manager.start();
                    manager.seekTo(seekBar.getProgress());
                }
            }
        });

        new Thread(() -> {
            while (goThread) {
                if (isPlaying) {
                    //把当前的播放进度设置到seekBar上
                    sk_mp3.setProgress(manager.getCurrentPosition());
                }
                //歇0.5s
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        ib_mp3_start_pause.setOnClickListener(this);
        ib_mp3_next.setOnClickListener(this);
        ib_mp3_previous.setOnClickListener(this);
        ib_mp3_loop_mode.setOnClickListener(this);
        ib_mp3_songs_list.setOnClickListener(this);
    }

    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.ib_mp3_start_pause:
                //开始或暂停按钮
                manager.startOrPause();
                break;
            case R.id.ib_mp3_next:
                //下一首按钮
                manager.playNext(playMode);
                break;
            case R.id.ib_mp3_previous:
                //上一首按钮
                manager.playPrevious();
                break;
            case R.id.ib_mp3_loop_mode:
                //播放模式按钮
                //现根据当前模式设置点击后的图片和变更后的模式
                //最后在将playMode和播放器的mode同步
                switch (playMode) {
                    case MediaPlayerManager.MODE_LIST_LOOP:
                        manager.setPlayMode(MediaPlayerManager.MODE_SINGLE_LOOP);
                        ib_mp3_loop_mode.setBackground(getDrawable(R.drawable.signle_loop));
                        break;
                    case MediaPlayerManager.MODE_SINGLE_LOOP:
                        manager.setPlayMode(MediaPlayerManager.MODE_RANDOM);
                        ib_mp3_loop_mode.setBackground(getDrawable(R.drawable.random));
                        break;
                    case MediaPlayerManager.MODE_RANDOM:
                        manager.setPlayMode(MediaPlayerManager.MODE_LIST_LOOP);
                        ib_mp3_loop_mode.setBackground(getDrawable(R.drawable.list_loop));
                        break;
                }
                playMode = manager.getPlayMode();
                break;
            case R.id.ib_mp3_songs_list:
                //歌单列表，打开新的活动
                startActivity(new Intent(this, MusicListActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //记载选项菜单
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_search:
                //搜索按钮
                //打开新的活动
                startActivity(new Intent(MainActivity.this, SearchResultActivity.class));
                break;
            case R.id.menu_main_download:
                //下载按钮
                //开启下载服务
                //download(manager.getCurrentMusicData().getId()); //打开浏览器
                startDownloadFileService(manager.getCurrentMusicData());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //暂停动画
        //需要动画=false
        animator.pause();
        isAnimation = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //如果正在播放就开始动画
        isAnimation = true;
        if (isPlaying)
            animator.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        goThread = false;
        manager.release();
        unbindService(serviceConnection);
        stopService(serviceIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //双击返回才退出活动
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - lastActionTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                lastActionTime = System.currentTimeMillis();
            } else {
                this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setDisplayDataWithoutPrepared() {
        //设置一些不需要播放器准备好就可以显示的
        //专题图片、歌手、歌曲名
        //调用start（）使动画重新开始
        tx_mp3_song_name.setText(manager.getCurrentMusicData().getName());
        tx_mp3_artist.setText(manager.getCurrentMusicData().getArtist());
        Glide.with(this)
                .load(manager.getCurrentMusicData().getPicUrl())
                .circleCrop()
                .into(iv_mp3_pic);
        tx_mp3_total_time.setText(getMinAndSec(0));
        tx_mp3_current_time.setText(getMinAndSec(0));
        sk_mp3.setProgress(0);
        animator.start();
    }

    private void setDisplayDataWithPrepared() {
        //设置需要播放器准备好才可以显示的内容
        //歌曲时长
        tx_mp3_total_time.setText(getMinAndSec(manager.getDuration()));
        sk_mp3.setMax(manager.getDuration());
    }


    private void startDownloadFileService(MusicData data) {
        //开启下载服务
        String id = data.getId();   //歌曲id
        String fileName = data.getName() + "-" + data.getArtist();  //用歌曲名和歌手拼接成文件名
        API api = RetrofitManager.getRetrofit().create(API.class);
        Call<MusicDetail> call = api.getMusicDetail(id, "[" + id + "]", 3200000);
        call.enqueue(new Callback<MusicDetail>() {
            @Override
            public void onResponse(@NonNull Call<MusicDetail> call, @NonNull Response<MusicDetail> response) {
                if (response.code() == HttpsURLConnection.HTTP_OK && response.body() != null) {
                    //如果code==200且响应体不为空
                    //获取到下载链接
                    //传入下载链接和文件名字
                    String downloadUrl = response.body().data.get(0).url;
                    if (downloadUrl != null)
                        downloadBinder.start(downloadUrl, fileName);
                    else Toast.makeText(MainActivity.this, "没有获取到下载链接", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(MainActivity.this, "内容为空", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<MusicDetail> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "查询失败，再试试？", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void download(String id) {
        //获取到下载链接后打开浏览器
        Toast.makeText(this, "正在获取，可能会打开浏览器", Toast.LENGTH_SHORT).show();
        API api = RetrofitManager.getRetrofit().create(API.class);
        Call<MusicDetail> call = api.getMusicDetail(id, "[" + id + "]", 3200000);
        call.enqueue(new Callback<MusicDetail>() {
            @Override
            public void onResponse(@NonNull Call<MusicDetail> call, @NonNull Response<MusicDetail> response) {
                if (response.code() == HttpsURLConnection.HTTP_OK) {
                    if (response.body() != null && response.body().data.get(0).url != null) {
                        String url = response.body().data.get(0).url;
                        Log.e("MusicPlayerActivity", url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } else
                        Toast.makeText(MainActivity.this, "结果为空", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MusicDetail> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "查询失败，再试试？", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //将int类型的ms值 转换为mm：ss形式的字符串
    public String getMinAndSec(int data) {
        data /= 1000;
        String m = "" + data / 60;
        String s = data % 60 < 10 ? ("" + 0 + data % 60) : "" + data % 60;
        return m + ":" + s;
    }
}