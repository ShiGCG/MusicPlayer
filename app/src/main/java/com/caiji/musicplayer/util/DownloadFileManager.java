package com.caiji.musicplayer.util;

import android.os.AsyncTask;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.caiji.musicplayer.api.API;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadFileManager extends AsyncTask<String, Integer, Integer> {
    public static final int DOWNLOAD_STATE_SUCCESS = 0;
    public static final int DOWNLOAD_STATE_FAIL = 1;

    private DownloadFileListener downloadFileListener;
    File file = null;
    InputStream inputStream = null;
    RandomAccessFile randomAccessFile = null;
    private String downloadUrl = "";    //下载链接
    private String fileName = "";       //文件名字
    private String path = "";           //文件路径
    private int lastProgress;           //上次的进度
    private int stateCode = 0;          //状态码
    private long downloadLength = 0;    //上次已经下载的长度

    public interface DownloadFileListener {
        void onStart(String path);

        void onError();

        void onSuccess(String path);

        void onProgress(int position, int percentageProgress);
    }

    public DownloadFileManager(@NonNull DownloadFileListener l) {
        this.downloadFileListener = l;
    }

    /*
    后台任务
     */
    @Override
    protected Integer doInBackground(String... strings) {
        downloadUrl = strings[0];
        fileName = strings[1] + ".mp3";
        try {
            //文件路径为 系统下载文件夹
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory, fileName);
            path = file.getAbsolutePath();
            if (file.exists()) {
                //如果文件已经存在，获取已经下载的进度
                downloadLength = file.length();
            }
            API api = RetrofitManager.getRetrofit().create(API.class);
            Call<ResponseBody> responseBodyCall = api.downloadFile(downloadUrl, downloadLength);
            Response<ResponseBody> response = responseBodyCall.execute();
            f:
            if (response.code() == HttpURLConnection.HTTP_OK && response.body() != null) {
                //如果code==200 且响应体不为空
                long contextLength = response.body().contentLength();
                //获取下载内容的大小
                if (contextLength == 0) {
                    //没有内容
                    stateCode = DOWNLOAD_STATE_FAIL;
                    break f;
                }
                if (contextLength == downloadLength) {
                    //已下载的进度等于要下载的进度，返回成功
                    stateCode = DOWNLOAD_STATE_SUCCESS;
                    break f;
                }
                //获取输入流
                inputStream = response.body().byteStream();
                try {
                    downloadFileListener.onStart(path);
                    //断点续传
                    randomAccessFile = new RandomAccessFile(file, "rw");
                    //缓冲区长度1024byte
                    byte[] bytes = new byte[1024];
                    int readLength;
                    while ((readLength = inputStream.read(bytes)) != -1) {
                        //写入文件
                        randomAccessFile.write(bytes, 0, readLength);
                    }
                    stateCode = DOWNLOAD_STATE_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                    stateCode = DOWNLOAD_STATE_FAIL;
                }
            } else stateCode = DOWNLOAD_STATE_FAIL;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (randomAccessFile != null)
                    randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stateCode;
    }

    /*
    进度更新
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0] > lastProgress && values[1] < 100) {
            //第一个参数当前位置，多少kb，第二参数百分比
            downloadFileListener.onProgress(values[0], values[1]);
            lastProgress = values[0];
        }
    }

    /*
    根据下载结果回调
     */
    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer) {
            case DOWNLOAD_STATE_FAIL:
                downloadFileListener.onError();
                break;
            case DOWNLOAD_STATE_SUCCESS:
                downloadFileListener.onSuccess(path);
                break;
        }
    }
}
