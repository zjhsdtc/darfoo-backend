package com.darfoo.backend.cache;

import com.darfoo.backend.caches.dao.CacheUtils;
import com.darfoo.backend.dao.cota.CommonDao;
import com.darfoo.backend.model.resource.dance.DanceGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zjh on 15-4-1.
 */

//测试所有和缓存层交互的service的正确性
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "file:src/main/webapp/WEB-INF/pre-deal.xml",
        "file:src/main/webapp/WEB-INF/redis-context.xml",
        "file:src/main/webapp/WEB-INF/springmvc-hibernate.xml",
        "file:src/main/webapp/WEB-INF/util-context.xml"
})
public class CacheServiceTests {
    @Autowired
    CacheUtils cacheUtils;
    @Autowired
    CommonDao commonDao;

    public void logResources(List resources) {
        for (Object object : resources) {
            System.out.println(object);
        }
        System.out.println("resources total size -> " + resources.size());
    }

    @Test
    public void cacheSingleResourceById() {
        HashMap<String, Integer> typeidpair = new HashMap<String, Integer>();
        typeidpair.put("dancevideo", 1073);
        typeidpair.put("dancemusic", 436);
        typeidpair.put("dancegroup", 116);
        for (String type : typeidpair.keySet()) {
            System.out.println(cacheUtils.cacheSingleResource(type, typeidpair.get(type)));
        }
    }

    @Test
    public void cacheSingleResourceByTitle() {
        HashMap<String, String> typeidpair = new HashMap<String, String>();
        typeidpair.put("thirdpartapp", "芒果tv");
        for (String type : typeidpair.keySet()) {
            System.out.println(cacheUtils.cacheSingleResource(type, typeidpair.get(type)));
        }
    }

    @Test
    public void cacheRecommendResources() {
        String type = "dancevideo";
        logResources(cacheUtils.cacheRecommendResources(type));
    }

    @Test
    public void cacheIndexResources() {
        String type = "dancegroup";
        logResources(cacheUtils.cacheIndexResources(type));
    }

    @Test
    public void cacheNewestResources() {
        String[] types = {"dancevideo", "dancemusic"};
        for (String type : types) {
            logResources(cacheUtils.cacheNewestResources(type));
        }
    }

    @Test
    public void cacheAllResources() {
        String[] types = {"operavideo"};
        //String[] types = {"operaseries"};
        for (String type : types) {
            logResources(cacheUtils.cacheAllResources(type));
        }
    }

    @Test
    public void cacheNewestResourcesByPage() {
        String[] types = {"dancevideo", "dancemusic"};
        Integer page = 1;
        for (String type : types) {
            logResources(cacheUtils.cacheNewestResources(type, page));
        }
    }

    @Test
    public void cacheResourcesByCategory() {
        HashMap<String, String> typecategorypair = new HashMap<String, String>();
        typecategorypair.put("dancevideo", "0");
        typecategorypair.put("dancemusic", "A");
        for (String type : typecategorypair.keySet()) {
            logResources(cacheUtils.cacheResourcesByCategory(type, typecategorypair.get(type)));
        }
    }

    @Test
    public void cacheResourcesByCategoryByPage() {
        HashMap<String, String> typecategorypair = new HashMap<String, String>();
        typecategorypair.put("dancevideo", "0");
        typecategorypair.put("dancemusic", "A");
        Integer page = 1;
        for (String type : typecategorypair.keySet()) {
            logResources(cacheUtils.cacheResourcesByCategory(type, typecategorypair.get(type), page));
        }
    }

    @Test
    public void cacheResourcesByInnertype() {
        HashMap<String, String[]> typeinnertypepair = new HashMap<String, String[]>();
        typeinnertypepair.put("dancevideo", new String[]{"TUTORIAL", "NORMAL"});
        typeinnertypepair.put("dancegroup", new String[]{"STAR", "NORMAL"});
        for (String type : typeinnertypepair.keySet()) {
            String[] innertypes = typeinnertypepair.get(type);
            for (String innertype : innertypes) {
                logResources(cacheUtils.cacheResourcesByInnertype(type, innertype));
            }
        }
    }

    @Test
    public void cacheResourcesByInnertypeByPage() {
        HashMap<String, String[]> typeinnertypepair = new HashMap<String, String[]>();
        typeinnertypepair.put("dancevideo", new String[]{"TUTORIAL", "NORMAL"});
        typeinnertypepair.put("dancegroup", new String[]{"STAR", "NORMAL"});
        Integer page = 1;
        for (String type : typeinnertypepair.keySet()) {
            String[] innertypes = typeinnertypepair.get(type);
            for (String innertype : innertypes) {
                logResources(cacheUtils.cacheResourcesByInnertype(type, innertype, page));
            }
        }
    }

    @Test
    public void cacheHotResources() {
        String[] types = {"dancegroup", "dancemusic"};
        for (String type : types) {
            logResources(cacheUtils.cacheHotResources(type));
        }
    }

    @Test
    public void cacheHotResourcesByPage() {
        String[] types = {"dancegroup", "dancemusic"};
        Integer page = 1;
        for (String type : types) {
            logResources(cacheUtils.cacheHotResources(type, page));
        }
    }

    @Test
    public void cacheDanceMusicForDanceVideo() {
        Integer[] ids = {1073, 1075};
        for (Integer id : ids) {
            System.out.println(cacheUtils.cacheDanceMusicForDanceVideo(id));
        }
    }

    @Test
    public void cacheVideosForDanceGroup() {
        Integer dancegroupid = 109;
        logResources(cacheUtils.cacheDanceGroupVideos(dancegroupid));
    }

    @Test
    public void cacheVideosForOperaSeries() {
        Integer operaseriesid = 3;
        logResources(cacheUtils.cacheOperaSeriesVideos(operaseriesid));
    }

    @Test
    public void cacheVideosForDanceGroupByPage() {
        Integer dancegroupid = 109;
        Integer page = 1;
        logResources(cacheUtils.cacheDanceGroupVideos(dancegroupid, page));
    }

    @Test
    public void cacheResourcesBySearch() {
        HashMap<String, String> typesearch = new HashMap<String, String>();
        typesearch.put("dancevideo", "三亚");
        typesearch.put("dancemusic", "那时候");
        typesearch.put("operavideo", "哈哈");
        for (String type : typesearch.keySet()) {
            logResources(cacheUtils.cacheResourcesBySearch(type, typesearch.get(type)));
        }
    }

    @Test
    public void cacheResourcesBySearchByPage() {
        HashMap<String, String> typesearch = new HashMap<String, String>();
        typesearch.put("dancevideo", "三亚");
        typesearch.put("dancemusic", "那时候");
        Integer page = 1;
        for (String type : typesearch.keySet()) {
            logResources(cacheUtils.cacheResourcesBySearch(type, typesearch.get(type), page));
        }
    }

    @Test
    public void cacheSidebarResources() {
        HashMap<String, Integer> typeidpair = new HashMap<String, Integer>();
        typeidpair.put("dancevideo", 1073);
        typeidpair.put("dancemusic", 436);
        for (String type : typeidpair.keySet()) {
            logResources(cacheUtils.cacheSidebarResources(type, typeidpair.get(type)));
        }
    }

    @Test
    public void cacheResourcesWithPriority() {
        HashMap<String, String> typeinnertypepair = new HashMap<String, String>();
        typeinnertypepair.put("dancegroup", "STAR");
        for (String type : typeinnertypepair.keySet()) {
            logResources(cacheUtils.cacheResourcesByInnertype(type, typeinnertypepair.get(type)));
        }
    }

    @Test
    public void cachePriorityDanceGroup() {
        logResources(commonDao.getResourcesWithPriority(DanceGroup.class));
    }

    public void inner(Integer... ids) {
        System.out.println(ids[0]);
        System.out.println(ids[1]);
    }

    public void outer(Integer... ids) {
        inner(ids);
    }

    @Test
    public void testNestedUlimitParams() {
        outer(3, 3);
    }
}
