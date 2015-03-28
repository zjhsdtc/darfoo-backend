package com.springapp.mvc.statistic;

/**
 * Created by zjh on 15-3-2.
 */

import com.darfoo.backend.dao.statistic.StatisticsDao;
import com.darfoo.backend.model.statistics.mongo.CrashLog;
import com.darfoo.backend.model.statistics.mongo.SearchHistory;
import com.darfoo.backend.model.statistics.mongo.clickcount.MenuClickCount;
import com.darfoo.backend.model.statistics.mongo.clickcount.ResourceClickCount;
import com.darfoo.backend.model.statistics.mongo.clickcount.TabClickCount;
import com.darfoo.backend.model.statistics.mongo.clicktime.MenuClickTime;
import com.darfoo.backend.model.statistics.mongo.clicktime.ResourceClickTime;
import com.darfoo.backend.model.statistics.mongo.clicktime.TabClickTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/springmvc-hibernate.xml")
public class StatisticsTests {
    @Autowired
    StatisticsDao statisticsDao;

    @Test
    public void insertOrUpdateResourceClickCount() {
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("mac", "00:ad:05:01:a6:85");
        conditions.put("hostip", "fe80::2ad:5ff:fe01:a685-wlan0");
        conditions.put("uuid", "123");
        conditions.put("type", "video");
        conditions.put("resourceid", 33);

        statisticsDao.insertOrUpdateClickBehavior(ResourceClickCount.class, conditions);
    }

    @Test
    public void insertOrUpdateMenuClickCount() {
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("mac", "00:ad:05:01:a6:85");
        conditions.put("hostip", "fe80::2ad:5ff:fe01:a685-wlan0");
        conditions.put("uuid", "123");
        conditions.put("menuid", 1);

        statisticsDao.insertOrUpdateClickBehavior(MenuClickCount.class, conditions);
    }

    @Test
    public void insertOrUpdateTabClickCount() {
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("mac", "00:ad:05:01:a6:85");
        conditions.put("hostip", "fe80::2ad:5ff:fe01:a685-wlan0");
        conditions.put("uuid", "123");
        conditions.put("tabid", 3);

        statisticsDao.insertOrUpdateClickBehavior(TabClickCount.class, conditions);
    }

    @Test
    public void insertResourceClickTime() {
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("mac", "00:ad:05:01:a6:85");
        conditions.put("hostip", "fe80::2ad:5ff:fe01:a685-wlan0");
        conditions.put("uuid", "123");
        conditions.put("type", "video");
        conditions.put("resourceid", 81);

        statisticsDao.insertTimeBehavior(ResourceClickTime.class, conditions);
    }

    @Test
    public void insertMenuClickTime() {
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("mac", "00:ad:05:01:a6:85");
        conditions.put("hostip", "fe80::2ad:5ff:fe01:a685-wlan0");
        conditions.put("uuid", "123");
        conditions.put("menuid", 1);

        statisticsDao.insertTimeBehavior(MenuClickTime.class, conditions);
    }

    @Test
    public void insertTabClickTime() {
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("mac", "00:ad:05:01:a6:85");
        conditions.put("hostip", "fe80::2ad:5ff:fe01:a685-wlan0");
        conditions.put("uuid", "123");
        conditions.put("tabid", 3);

        statisticsDao.insertTimeBehavior(TabClickTime.class, conditions);
    }

    @Test
    public void insertSearchBehavior() {
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("searchcontent", "呵呵");
        conditions.put("searchtype", "video");

        statisticsDao.insertTimeBehavior(SearchHistory.class, conditions);
    }

    @Test
    public void insertCrashLog() {
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("log", "exception 呵呵");

        statisticsDao.insertTimeBehavior(CrashLog.class, conditions);
    }
}
