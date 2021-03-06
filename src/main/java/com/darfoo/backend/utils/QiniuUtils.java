package com.darfoo.backend.utils;

import com.darfoo.backend.dao.cota.CommonDao;
import com.darfoo.backend.model.resource.dance.DanceMusic;
import com.darfoo.backend.model.resource.dance.DanceVideo;
import com.darfoo.backend.model.resource.opera.OperaVideo;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.resumableio.ResumeableIoApi;
import com.qiniu.api.rs.GetPolicy;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.RSClient;
import com.qiniu.api.rs.URLUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.BucketManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zjh on 14-11-26.
 */
public class QiniuUtils {
    @Autowired
    CommonDao commonDao;
    private String bucketName;
    private String mimeType;
    //因为客户端暂时不支持302重定向所以只能蛋疼的分两个域名了,m3u8切片文件因为本身只用来播放所以就用加速后的域名
    private String downloaddomain;
    private String playdomain;
    private BucketManager bucketManager;
    private Auth auth;
    private OperationManager operater;

    public QiniuUtils() {
        Config.ACCESS_KEY = "bnMvAStYBsL5AjYM3UXbpGalrectRZZF88Y6fZ-X";
        Config.SECRET_KEY = "eMZK5q9HI1EXe7KzNtsyKJZJPHEfh96XcHvDigyG";
        this.bucketName = "zjdxlab410yy";
        this.mimeType = null;
        this.downloaddomain = "7qnarb.com1.z0.glb.clouddn.com";
        this.playdomain = "speedup.darfoo.com";
        this.auth = Auth.create(Config.ACCESS_KEY, Config.SECRET_KEY);
        this.bucketManager = new BucketManager(this.auth);
        this.operater = new OperationManager(this.auth);
    }

    public String getToken() {
        PutPolicy policy = new PutPolicy(bucketName);
        Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
        try {
            String upToken = policy.token(mac);
            return upToken;
        } catch (AuthException e) {
            e.printStackTrace();
            return "error";
        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }
    }

    //key就是七牛云上的文件名字
    //根据type来判断资源链接是否需要加密 暂时图片资源链接不需要加密
    public String getQiniuResourceUrl(String key, QiniuResourceEnum type) {
        Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
        try {
            //domain在空间设置里可以看到，每一个bucket都对应有一个域名
            //所谓的key其实就是上传的文件名字
            String baseUrl = URLUtils.makeBaseUrl(downloaddomain, key);
            //缩略图与切片资源都是用点播加速的域名
            if (type == QiniuResourceEnum.M3U8 || type == QiniuResourceEnum.RAWSMALL) {
                baseUrl = URLUtils.makeBaseUrl(playdomain, key);
            }
            GetPolicy getPolicy = new GetPolicy();
            //过期时间为一周
            getPolicy.expires = 7 * 24 * 3600;
            if (type == QiniuResourceEnum.ENCRYPT) {
                System.out.println(baseUrl);
                String downloadUrl = getPolicy.makeRequest(baseUrl, mac);
                return CryptUtils.encryptQiniuUrl(downloadUrl);
            } else if (type == QiniuResourceEnum.RAWNORMAL) {
                System.out.println(baseUrl);
                return getPolicy.makeRequest(baseUrl, mac);
            } else if (type == QiniuResourceEnum.RAWSMALL) {
                //缩小图片
                //首页推荐的图片和后台查看的视频就不要缩小了
                String baseUrlSmall = String.format("%s?imageView2/2/w/230/h/126", baseUrl);
                System.out.println(baseUrlSmall);
                return getPolicy.makeRequest(baseUrlSmall, mac);
            } else if (type == QiniuResourceEnum.M3U8) {
                Long expiretime = 7 * 24 * 3600L;
                baseUrl = baseUrl + "?pm3u8/0/expires/" + expiretime;
                return CryptUtils.encryptQiniuUrl(this.auth.privateDownloadUrl(baseUrl));
            } else {
                System.out.println("wired");
                return "";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String escape(String url) throws EncoderException {
        URLCodec urlEncoder = new URLCodec("UTF-8");
        return urlEncoder.encode(url).replace("+", "%20");
    }

    public String makeBaseUrl(String domain, String key) throws EncoderException {
        return "http://" + domain + "/" + escape(key);
    }

    public String getM3U8DownloadUrl(String domain, String key) {
        try {
            String baseUrl = makeBaseUrl(domain, key);
            Long expiretime = 7 * 24 * 3600L;
            baseUrl = baseUrl + "?pm3u8/0/expires/" + expiretime;
            return this.auth.privateDownloadUrl(baseUrl);
        } catch (EncoderException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    //对大资源进行分片加快客户端的加载
    public void resourceOperation(String key) {
        String notifyURL = "http://115.29.174.222:9001/qiniucallback";
        boolean force = true;
        String pipeline = "";

        StringMap params = new StringMap().putNotEmpty("notifyURL", notifyURL)
                .putWhen("force", 1, force).putNotEmpty("pipeline", pipeline);

        String fops = "avthumb/m3u8/ab/400k/r/24";

        try {
            // 针对指定空间的文件触发 pfop 操作
            String id = operater.pfop(this.bucketName, key, fops, params);
            System.out.println(id);
            // 可通过下列地址查看处理状态信息。
            // 实际项目中设置 notifyURL，接受通知。通知内容和处理完成后的查看信息一致。
            //String url = "http://api.qiniu.com/status/get/prefop?id=" + id;
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时简单状态信息
            System.out.println(r.toString());
            try {
                // 响应的文本信息
                System.out.println(r.bodyString());
            } catch (QiniuException e1) {
                //ignore
            }
        }
    }

    public void copyFile(String originbucket, String originkey, String targetbucket, String targetkey) {
        try {
            this.bucketManager.copy(originbucket, originkey, targetbucket, targetkey);
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(String bucket, String key) {
        try {
            this.bucketManager.delete(bucket, key);
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    public void speedupResources() {
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("speedup_key", "");
        List<String> resourcekeys = new ArrayList<String>();
        List dancevideos = commonDao.getResourcesByFields(DanceVideo.class, conditions);
        List dancemusics = commonDao.getResourcesByFields(DanceMusic.class, conditions);
        List operavideos = commonDao.getResourcesByFields(OperaVideo.class, conditions);

        for (Object danceVideo : dancevideos) {
            resourcekeys.add((String) commonDao.getResourceAttr(DanceVideo.class, danceVideo, "video_key"));
        }

        for (Object danceMusic : dancemusics) {
            resourcekeys.add((String) commonDao.getResourceAttr(DanceMusic.class, danceMusic, "music_key"));
        }

        for (Object operaVideo : operavideos) {
            resourcekeys.add((String) commonDao.getResourceAttr(OperaVideo.class, operaVideo, "video_key"));
        }

        for (String key : resourcekeys) {
            System.out.println(key);
            resourceOperation(key);
        }
    }

    public void renameFile(String oldkey, String newkey) {
        try {
            bucketManager.rename(this.bucketName, oldkey, newkey);
        } catch (QiniuException e) {
            e.printStackTrace();
        }
    }

    //尝试使用断点续传和并行分块上传
    public String uploadResourceStream(String fileLocation, String fileName) {
        System.out.println("start to upload resource to qiniu server");
        Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
        PutPolicy putPolicy = new PutPolicy(this.bucketName);

        try {
            String upToken = putPolicy.token(mac);
            String key = fileName;
            FileInputStream fis = new FileInputStream(new File(fileLocation));
            PutRet ret = ResumeableIoApi.put(fis, upToken, key, this.mimeType);
            return ret.getStatusCode() + "";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public void deleteResource(String key) {
        Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
        RSClient client = new RSClient(mac);
        client.delete(bucketName, key);
    }
}
