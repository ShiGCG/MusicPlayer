package com.caiji.musicplayer.util;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private static final Retrofit retrofit = new Retrofit.Builder()
            //设置基址
            .baseUrl("https://music.163.com")
            //添加转换器
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static Retrofit getRetrofit() {
        return retrofit;
    }

}
