package com.caiji.musicplayer.model;

import java.util.List;

//用来获取下载链接的api转换成的bean类
public class MusicDetail {
    public List<DataDTO> data;
    public Integer code;

    public static class DataDTO {
        public Integer id;      //歌曲id
        public String url;      //下载链接
        public Integer br;
        public Integer size;
        public String md5;
        public Integer code;
        public Integer expi;
        public String type;
        public Integer gain;
        public Integer fee;
        public Object uf;
        public Integer payed;
        public Integer flag;
        public Boolean canExtend;
        public Object freeTrialInfo;
        public String level;
        public String encodeType;
        public DataDTO.FreeTrialPrivilegeDTO freeTrialPrivilege;
        public DataDTO.FreeTimeTrialPrivilegeDTO freeTimeTrialPrivilege;
        public Integer urlSource;

        public static class FreeTrialPrivilegeDTO {
            public Boolean resConsumable;
            public Boolean userConsumable;
            public Object listenType;
        }

        public static class FreeTimeTrialPrivilegeDTO {
            public Boolean resConsumable;
            public Boolean userConsumable;
            public Integer type;
            public Integer remainTime;
        }
    }
}
