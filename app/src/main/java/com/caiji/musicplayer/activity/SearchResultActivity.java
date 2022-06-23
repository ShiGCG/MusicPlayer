package com.caiji.musicplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.caiji.musicplayer.R;
import com.caiji.musicplayer.api.API;
import com.caiji.musicplayer.api.NeteaseCouldMusicSearchAPI;
import com.caiji.musicplayer.model.MusicData;
import com.caiji.musicplayer.model.MusicSearchResult;
import com.caiji.musicplayer.util.MediaPlayerManager;
import com.caiji.musicplayer.util.RecyclerViewAdapter;
import com.caiji.musicplayer.util.RetrofitManager;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class SearchResultActivity extends BaseActivity {
    Toolbar tb_search_result;
    Button bt_play_all;
    RecyclerView rc_search_result;

    MediaPlayerManager manager;
    List<MusicData> songs;
    RecyclerViewAdapter adapter;
    NeteaseCouldMusicSearchAPI searchAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        init();
    }

    private void init() {
        manager = MediaPlayerManager.getManager();
        songs = new ArrayList<>();
        searchAPI = new NeteaseCouldMusicSearchAPI();
        adapter = new RecyclerViewAdapter();
        initView();
    }

    private void initView() {
        tb_search_result = findViewById(R.id.tb_search_result);
        bt_play_all = findViewById(R.id.bt_play_all);
        rc_search_result = findViewById(R.id.rc_search_result);
        rc_search_result.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(tb_search_result);

        bt_play_all.setOnClickListener(this);

        adapter.setItemViewClickListener(new RecyclerViewAdapter.OnItemViewClickListener() {
            @Override
            public void onClick(int position) {
                //单击播放，并结束此活动
                manager.addSong(songs.get(position));
                SearchResultActivity.this.finish();
            }

            @Override
            public void onLongClick(int position) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.bt_play_all) {
            if (songs.isEmpty()) {
                Toast.makeText(this, "没有结果", Toast.LENGTH_SHORT).show();
            } else {
                manager.addAllSongs(songs);
                this.finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载menu，加载searchView
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();    //收起键盘
                searchView.setQuery("", false);
                searchView.onActionViewCollapsed(); //收起搜索框
                searchMusic(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void searchMusic(String query) {
        API api = RetrofitManager.getRetrofit().create(API.class);
        Call<MusicSearchResult> searchResult = api.getSearchResult(searchAPI.getUrl(query, 0));
        searchResult.enqueue(new retrofit2.Callback<MusicSearchResult>() {
            @Override
            public void onResponse(@NonNull Call<MusicSearchResult> call, @NonNull Response<MusicSearchResult> response) {
                if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {
                    //如果code==200 且响应体不为空
                    MusicSearchResult result = response.body();
                    songs.clear();
                    for (int i = 0; i < result.result.songs.size(); i++) {
                        //处理结果，将MusicSearchResult类中的有用信息封装成MusicData类
                        MusicData data = new MusicData();
                        data.setId(String.valueOf(result.result.songs.get(i).id));
                        data.setName(result.result.songs.get(i).name);
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j = 0; j < result.result.songs.get(i).ar.size(); j++) {
                            //拼接歌手名字，中间 / 分开
                            stringBuilder.append(result.result.songs.get(i).ar.get(j).name);
                            stringBuilder.append("/");
                        }
                        //删去最后的 / 符号
                        String artist = stringBuilder.substring(0, stringBuilder.length() - 1);
                        data.setArtist(artist);
                        data.setPicUrl(result.result.songs.get(i).al.picUrl);
                        songs.add(data);
                    }
                    //设置列表数据
                    setData(songs);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MusicSearchResult> call, @NonNull Throwable t) {
                Toast.makeText(SearchResultActivity.this, "error\n请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData(List<MusicData> songsList) {
        runOnUiThread(() -> {
            if (songsList.isEmpty()) {
                Toast.makeText(this, "没有结果", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.setData(songsList);
            rc_search_result.setAdapter(adapter);
        });
    }
}
