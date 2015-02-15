package com.springapp.mvc;

import com.darfoo.backend.caches.CacheProtocol;
import com.darfoo.backend.dao.VideoDao;
import com.darfoo.backend.model.Video;
import com.darfoo.backend.service.responsemodel.CacheSingleVideo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by zjh on 15-2-14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "file:src/main/webapp/WEB-INF/pre-deal.xml",
        "file:src/main/webapp/WEB-INF/redis-context.xml",
        "file:src/main/webapp/WEB-INF/springmvc-hibernate.xml"
})
public class RedisProtocolTests {
    @Autowired
    VideoDao videoDao;
    @Autowired
    CacheProtocol cacheProtocol;

    @Test
    public void insertResourceIntoCache() {
        Video video = videoDao.getVideoByVideoId(35);
        cacheProtocol.insertResourceIntoCache(Video.class, video, "video");
    }

    @Test
    public void extractResourceFromCache() {
        CacheSingleVideo result = (CacheSingleVideo) cacheProtocol.extractResourceFromCache(Video.class, CacheSingleVideo.class, 35, "video");
        try {
            for (Field field : CacheSingleVideo.class.getDeclaredFields()) {
                field.setAccessible(true);
                System.out.println(field.getName() + " -> " + field.get(result));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
