package com.caiji.musicplayer.model;

import java.util.List;

//获取搜索结果的api转换成的bean类
public class MusicSearchResult {
    public Boolean needLogin;
    public ResultDTO result;
    public Integer code;

    public static class ResultDTO {
        public Object searchQcReminder;
        public List<SongsDTO> songs;
        public Integer songCount;

        public static class SongsDTO {
            public String name;             //歌曲名
            public Integer id;              //歌曲id
            public Integer pst;
            public Integer t;
            public List<ArDTO> ar;          //歌手
            public List<String> alia;
            public Double pop;
            public Integer st;
            public Object rt;
            public Integer fee;
            public Integer v;
            public Object crbt;
            public String cf;
            public ResultDTO.SongsDTO.AlDTO al;//专辑信息
            public Integer dt;
            public ResultDTO.SongsDTO.HDTO h;
            public ResultDTO.SongsDTO.MDTO m;
            public ResultDTO.SongsDTO.LDTO l;
            public ResultDTO.SongsDTO.SqDTO sq;
            public Object hr;
            public Object a;
            public String cd;
            public Integer no;
            public Object rtUrl;
            public Integer ftype;
            public List<?> rtUrls;
            public Integer djId;
            public Integer copyright;
            public Integer sId;
            public Long mark;
            public Integer originCoverType;
            public Object originSongSimpleData;
            public Object tagPicList;
            public Boolean resourceState;
            public Integer version;
            public Object songJumpInfo;
            public Object entertainmentTags;
            public Integer single;
            public Object noCopyrightRcmd;
            public Integer rtype;
            public Object rurl;
            public Integer mst;
            public Integer cp;
            public Integer mv;
            public Long publishTime;
            public ResultDTO.SongsDTO.PrivilegeDTO privilege;
            public List<String> tns;

            public static class AlDTO {
                public Integer id;
                public String name;
                public String picUrl;           //专辑图
                public List<?> tns;
                public String picStr;
                public Long pic;
            }

            public static class HDTO {
                public Integer br;
                public Integer fid;
                public Integer size;
                public Double vd;
                public Integer sr;
            }

            public static class MDTO {
                public Integer br;
                public Integer fid;
                public Integer size;
                public Double vd;
                public Integer sr;
            }

            public static class LDTO {
                public Integer br;
                public Integer fid;
                public Integer size;
                public Double vd;
                public Integer sr;
            }

            public static class SqDTO {
                public Integer br;
                public Integer fid;
                public Integer size;
                public Double vd;
                public Integer sr;
            }

            public static class PrivilegeDTO {
                public Integer id;
                public Integer fee;
                public Integer payed;
                public Integer st;
                public Integer pl;
                public Integer dl;
                public Integer sp;
                public Integer cp;
                public Integer subp;
                public Boolean cs;
                public Integer maxbr;
                public Integer fl;
                public Boolean toast;
                public Integer flag;
                public Boolean preSell;
                public Integer playMaxbr;
                public Integer downloadMaxbr;
                public String maxBrLevel;
                public String playMaxBrLevel;
                public String downloadMaxBrLevel;
                public String plLevel;
                public String dlLevel;
                public String flLevel;
                public Object rscl;
                public ResultDTO.SongsDTO.PrivilegeDTO.FreeTrialPrivilegeDTO freeTrialPrivilege;
                public List<ChargeInfoListDTO> chargeInfoList;

                public static class FreeTrialPrivilegeDTO {
                    public Boolean resConsumable;
                    public Boolean userConsumable;
                    public Object listenType;
                }

                public static class ChargeInfoListDTO {
                    public Integer rate;
                    public Object chargeUrl;
                    public Object chargeMessage;
                    public Integer chargeType;
                }
            }

            public static class ArDTO {
                public Integer id;
                public String name;
                public List<?> tns;
                public List<String> alias;
                public List<String> alia;
            }
        }
    }
}
