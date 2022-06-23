package com.caiji.musicplayer.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;

public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {

    private AudioManager mAudioManager;
    private AudioFocusRequest mAudioFocusRequest;
    private AudioAttributes mAudioAttributes;       //音频属性

    private OnAudioFocusChangeListener mOnAudioFocusChangeListener;


    public AudioFocusManager(Context context) {
        //获取系统服务
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        //将focusChange通过自定义的接口传递出去
        if (mOnAudioFocusChangeListener != null) {
            mOnAudioFocusChangeListener.onAudioFocusChange(focusChange);
        }
    }

    public void requestFocus() {
        //请求音频焦点
        if (mAudioFocusRequest == null) {
            if (mAudioAttributes == null) {
                mAudioAttributes = new AudioAttributes.Builder()
                        //用途 媒体
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        //内容类型 音乐
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
            }
            mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(mAudioAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(this)
                    .build();
        }
        //发起请求，获取结果码
        int result = mAudioManager.requestAudioFocus(mAudioFocusRequest);
        if (mOnAudioFocusChangeListener != null)
            mOnAudioFocusChangeListener.onAudioFocusChange(result);
    }

    public void releaseFocus() {
        //释放音频焦点
        mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest);
        if (mOnAudioFocusChangeListener != null)
            mOnAudioFocusChangeListener.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS);
    }

    public interface OnAudioFocusChangeListener {
        void onAudioFocusChange(int focusChange);
    }

    public void setOnAudioFocusChangeListener(OnAudioFocusChangeListener l) {
        this.mOnAudioFocusChangeListener = l;
    }
}
