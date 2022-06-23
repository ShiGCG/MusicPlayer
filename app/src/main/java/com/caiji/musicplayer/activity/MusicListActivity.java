package com.caiji.musicplayer.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.caiji.musicplayer.R;
import com.caiji.musicplayer.model.MusicData;
import com.caiji.musicplayer.util.MediaPlayerManager;
import com.caiji.musicplayer.util.RecyclerViewAdapter;

import java.util.List;

public class MusicListActivity extends BaseActivity {
    RecyclerView rc_songs_list;

    MediaPlayerManager manager;
    RecyclerViewAdapter adapter;
    List<MusicData> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);
        init();
    }

    private void init() {
        manager = MediaPlayerManager.getManager();
        songs = manager.getAllSongs();
        adapter = new RecyclerViewAdapter();
        adapter.setData(songs);
        initView();
    }

    private void initView() {
        rc_songs_list = findViewById(R.id.rc_songs_list);
        rc_songs_list.setLayoutManager(new LinearLayoutManager(this));
        rc_songs_list.setAdapter(adapter);
        adapter.setItemViewClickListener(new RecyclerViewAdapter.OnItemViewClickListener() {
            @Override
            public void onClick(int position) {
                //单击播放指定歌曲
                manager.setPlaySongByPosition(position);
            }

            @Override
            public void onLongClick(int position) {
                //长按删除歌曲，如果不止有一首歌
                if (songs.size() > 1) {
                    manager.removeSong(position);
                    adapter.removeItemView(position);
                } else Toast.makeText(MusicListActivity.this, "只剩一个了", Toast.LENGTH_SHORT).show();
            }
        });
    }
}