# MusicPlayer
基于网易云音乐api实现的一款简单的音乐播放器
>最低Sdk版本26（API level26，Android 8.0）
>目标Sdk版本32（API level32（Sv2），Android 12）

>所需权限：
>1. 联网权限（播放、下载）
>2. 文件读写权限（保存文件）
## 已经完成的功能
- 搜索歌曲（使用wed端api，最多返回20条目）
- 播放控制（开始、暂停、切歌、模式控制）
- 播放列表（单击开始播放，长按删除）
- 下载歌曲（获取链接，后台服务下载，保存在系统下载目录）
## 项目结构
包含四个包（activity、api、model、service、util）
分别对应 活动、接口、模型、服务、工具
### activity包
> 包含三个活动和一个基本活动
> 1. MainActivity 应用主活动、入口、音乐播放页
> 2. MusicListActivity 播放页表活动，由主活动跳转
> 3. SearchResultActivity 搜索歌曲活动
> 4. BaseActivity 基本活动，上述三个活动都继承自它
### api包
>包含一个接口，一个java类
>1. API 接口，定义需要的方法
>2. NeteaseCouldMusicSearchAPI类，负责实现加密，获取查询链接
### model包
>包含三个类
>1. MusicData，包含歌曲的基本信息（id，专辑图片链接、歌手、歌曲名、播放链接）
>2. MusicDetail，网络请求返回的json字符串转换的bean类，包含下载链接
>3. MusicSearchResult，网络请求返回的json字符串转换的bean类，包含歌曲的详细信息
>MusicDetail和MusicSearchResult的定义用GsonFormat插件完成
### service包
>包含一个服务类
>DownloadFileService，用来开启后台下载任务，构建通知
### util包
>包含四个管理器和一个适配器
>1. AudioFocusManager，管理音频焦点，内含有OnAudioFocusChangeListener接口，监听焦点变化
>2. DownloadFileManager，下载任务管理，内含有DownloadFileListener接口，监听下载状态
>3. MediaPlayerManager，播放管理器，内含有PlayStateListener接口，监听播放状态
>4. RetrofitManager，获取Retrofit
>5. RecyclerViewAdapter，适配器，内含有OnItemViewClickListener接口，监听itemView的点击事件
# 已经使用Gradle对app进行签名
> Run前请更改**build.gradle**中的签名配置
```Groovy
signingConfigs {  
  release {  
  storeFile file("filepath")  
  storePassword 'your pswd'  
  keyAlias 'your key alias'  
  keyPassword 'your pswd'  
  }  
}
```
> 应用可以调试，如果不想，需要删掉**build.gradle**中相应代码
```Groovy
buildTypes {  
  release {  
  debuggable true  //删掉这一行
  signingConfig signingConfigs.release  //签名配置
  minifyEnabled false  
  proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'  
  }  
}
```
# 项目使用到的依赖
>implementation 'androidx.recyclerview:recyclerview:1.2.1'  
>implementation 'com.google.code.gson:gson:2.9.0'  
>implementation 'com.squareup.retrofit2:converter-gson:2.9.0'  
>implementation 'com.squareup.retrofit2:retrofit:2.9.0'  
>implementation 'com.github.bumptech.glide:glide:4.13.2'
# 如果有疑问可以通过QQ联系我
[点击这里]("tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=431227130&website=www.oicqzone.com")或者直接加我 431227130（请注明来意）
