package com.caiji.musicplayer.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class NeteaseCouldMusicSearchAPI {

    //AES加密
    public String encrypt(String content, String key) throws Exception {
        String iv = "0102030405060708";
        if (key == null) {
            return null;
        }
        // 判断Key是否为16位
        if (key.length() != 16) {
            return null;
        }
        byte[] raw = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        //使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ips);
        byte[] encrypted = cipher.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String getEncSecKey() {
        return "96082b986b9f636e80c4de5868d9798cd4f5008d09d19c39c21817d36b3df397" +
                "19a9c6d367e249eedba216ce536e839265edc6e1cc5486db3f9545e5c560f32" +
                "9476cf9bb962a3ef63c4ae48c08df1aac1244f056aa1a356becc10bd475bd95b" +
                "80442d17515070f50b7730d43c9db00a151a0d530786d336767df354ab9189e50";
    }

    public String getParams(String s, int limit, int offset) {
        //拼接要加密的字符串
        String tempStr = "{\"hlpretag\":\"<span class=\\\"s-fc7\\\">\",\"hlposttag\":\"</span>\",\"s\":\""
                + s + "\",\"type\":\"1\",\"offset\":\"" + offset
                + "\",\"total\":\"true\",\"limit\":\"" + limit + "\",\"csrf_token\":\"\"}";
        try {
            //第一次加密的key固定
            //第二次加密的key和EncSecKey一一对应
            //返回前encode一下
            String first = encrypt(tempStr, "0CoJUm6Qyw8W8jud");
            return URLEncoder.encode(encrypt(first, "9cxqkYv1WsSmRWZ1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUrl(String s, int offset) {
        //拼接成查询字符串
        return "/weapi/cloudsearch/get/web?csrf_token="
                + "&params=" + getParams(s, 20, offset)
                + "&encSecKey=" + getEncSecKey();
    }
}
