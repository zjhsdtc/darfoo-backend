package com.darfoo.backend.cache;

import com.darfoo.backend.caches.client.CommonRedisClient;
import com.darfoo.backend.caches.cota.CacheCollType;
import com.darfoo.backend.caches.cota.CacheProtocol;
import com.darfoo.backend.caches.dao.CacheDao;
import com.darfoo.backend.dao.cota.CategoryDao;
import com.darfoo.backend.dao.cota.CommonDao;
import com.darfoo.backend.model.resource.dance.DanceGroup;
import com.darfoo.backend.model.resource.dance.DanceMusic;
import com.darfoo.backend.model.resource.dance.DanceVideo;
import com.darfoo.backend.service.cota.TypeClassMapping;
import com.darfoo.backend.service.responsemodel.SingleDanceGroup;
import com.darfoo.backend.service.responsemodel.SingleDanceMusic;
import com.darfoo.backend.service.responsemodel.SingleDanceVideo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;
import java.util.List;

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
    CommonDao commonDao;
    @Autowired
    CacheProtocol cacheProtocol;
    @Autowired
    CacheDao cacheDao;
    @Autowired
    CommonRedisClient redisClient;
    @Autowired
    CategoryDao categoryDao;

    @Test
    public void isExistsInCache() {
        System.out.println(redisClient.exists("music-419"));
    }

    @Test
    public void insertSortedSetIntoCache() {
        String type = "music";
        String categories = "0-0-0";
        Class resource = TypeClassMapping.typeClassMap.get(type);
        String cachekey = String.format("%scategory%s", type, categories);

        //List resources = categoryDao.getResourcesByCategories(resource, ServiceUtils.convertList2Array(cacheDao.parseResourceCategories(resource, categories)));
        List resources = null;
        cacheDao.insertResourcesIntoCache(resource, resources, cachekey, type, CacheCollType.SORTEDSET);
    }

    @Test
    public void extractSortedSetFromCache() {
        String type = "music";
        String categories = "0-0-0";
        String cachekey = String.format("%scategory%s", type, categories);

        List result = cacheDao.extractResourcesFromCache(TypeClassMapping.cacheResponseMap.get(type), cachekey, CacheCollType.SORTEDSET, 0L, 10L);

        for (Object object : result) {
            System.out.println(commonDao.getResourceAttr(SingleDanceMusic.class, object, "id"));
        }
    }

    @Test
    public void insertResourceIntoCache() {
        DanceVideo video = (DanceVideo) commonDao.getResourceById(DanceVideo.class, 35);
        cacheDao.insertSingleResource(DanceVideo.class, video, "video");
    }

    @Test
    public void extractResourceFromCache() {
        SingleDanceVideo result = (SingleDanceVideo) cacheDao.getSingleResource(SingleDanceVideo.class, "video-" + 81);
        try {
            for (Field field : SingleDanceVideo.class.getDeclaredFields()) {
                field.setAccessible(true);
                System.out.println(field.getName() + " -> " + field.get(result));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertVideoResourceIntoCache() {
        DanceVideo video = (DanceVideo) commonDao.getResourceById(DanceVideo.class, 35);
        cacheProtocol.insertResourceIntoCache(DanceVideo.class, video, "video");
    }

    @Test
    public void extractVideoResourceFromCache() {
        SingleDanceVideo result = (SingleDanceVideo) cacheProtocol.extractResourceFromCache(SingleDanceVideo.class, "video-" + 81);
        try {
            for (Field field : SingleDanceVideo.class.getDeclaredFields()) {
                field.setAccessible(true);
                System.out.println(field.getName() + " -> " + field.get(result));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void extractTutorialResourceFromCache() {
        SingleDanceVideo result = (SingleDanceVideo) cacheProtocol.extractResourceFromCache(SingleDanceVideo.class, "tutorial-" + 30);
        try {
            for (Field field : SingleDanceVideo.class.getDeclaredFields()) {
                field.setAccessible(true);
                System.out.println(field.getName() + " -> " + field.get(result));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertAuthorResourceIntoCache() {
        DanceGroup author = (DanceGroup) commonDao.getResourceById(DanceGroup.class, 13);
        cacheProtocol.insertResourceIntoCache(DanceGroup.class, author, "author");
    }

    @Test
    public void extractAuthorResourceFromCache() {
        SingleDanceGroup result = (SingleDanceGroup) cacheProtocol.extractResourceFromCache(SingleDanceGroup.class, "author-" + 13);
        try {
            for (Field field : SingleDanceGroup.class.getDeclaredFields()) {
                field.setAccessible(true);
                System.out.println(field.getName() + " -> " + field.get(result));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertMusicResourceIntoCache() {
        DanceMusic music = (DanceMusic) commonDao.getResourceById(DanceMusic.class, 30);
        cacheProtocol.insertResourceIntoCache(DanceMusic.class, music, "music");
    }

    @Test
    public void extractMusicResourceFromCache() {
        SingleDanceMusic result = (SingleDanceMusic) cacheProtocol.extractResourceFromCache(SingleDanceMusic.class, "music-" + 30);
        try {
            for (Field field : SingleDanceMusic.class.getDeclaredFields()) {
                field.setAccessible(true);
                System.out.println(field.getName() + " -> " + field.get(result));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
