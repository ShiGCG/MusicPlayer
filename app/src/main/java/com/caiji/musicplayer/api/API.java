package com.caiji.musicplayer.api;

import com.caiji.musicplayer.model.MusicDetail;
import com.caiji.musicplayer.model.MusicSearchResult;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface API {

    //废了不好使
    @FormUrlEncoded
    @POST("/weapi/cloudsearch/get/web")
    Call<ResponseBody> getSearchResult(@HeaderMap Map<String, Object> headers,  //请求头
                                       @FieldMap Map<String, Object> fields,    //请求表单
                                       @QueryMap Map<String, Object> query);    //查询参数

    //这个可以获取搜索结果
    @POST
    Call<MusicSearchResult> getSearchResult(@Url String url);

    @GET("/api/song/enhance/player/url")
    Call<MusicDetail> getMusicDetail(@Query("id") String id, @Query("ids") String ids, @Query("br") int br);

    @GET
    Call<ResponseBody> downloadFile(@Url String url, @Header("RANGE") long position);
}
