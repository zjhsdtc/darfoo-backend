package com.darfoo.backend.service;

import com.darfoo.backend.dao.statistic.StatisticsDao;
import com.darfoo.backend.model.statistics.CrashLog;
import com.darfoo.backend.model.statistics.SearchHistory;
import com.darfoo.backend.model.statistics.clickcount.ResourceClickCount;
import com.darfoo.backend.model.statistics.clicktime.ResourceClickTime;
import com.darfoo.backend.service.cota.TypeClassMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * Created by zjh on 15-3-3.
 */

@Controller
@RequestMapping("/statistics")
public class StatisticsController {
    @Autowired
    StatisticsDao statisticsDao;

    @RequestMapping(value = "{type}/{id}/m/{mac}/h/{host}/u/{uuid}")
    public
    @ResponseBody
    String statisticsClickBehavior(@PathVariable String type, @PathVariable Integer id, @PathVariable String mac, @PathVariable String host, @PathVariable String uuid) {
        HashMap<String, Object> conditions = new HashMap<String, Object>();

        conditions.put("mac", mac);
        conditions.put("hostip", host);
        conditions.put("uuid", uuid);

        if (type.equals("menu") || type.equals("tab")) {
            conditions.put(String.format("%sid", type), id);

            statisticsDao.insertBehavior(TypeClassMapping.clickTimeStatMap.get(type), conditions);
            statisticsDao.insertOrUpdateClickBehavior(TypeClassMapping.clickCountStatMap.get(type), conditions);
        } else {
            conditions.put("type", type);
            conditions.put("resourceid", id);

            statisticsDao.insertBehavior(ResourceClickTime.class, conditions);
            statisticsDao.insertOrUpdateClickBehavior(ResourceClickCount.class, conditions);
        }
        return "ok";
    }

    @RequestMapping(value = "search/t/{type}")
    public
    @ResponseBody
    String statisticsSearchHistory(@PathVariable String type, HttpServletRequest request) {
        String content = request.getParameter("search");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("searchtype", type);
        conditions.put("searchcontent", content);

        statisticsDao.insertBehavior(SearchHistory.class, conditions);
        return "ok";
    }

    @RequestMapping(value = "crashlog")
    public
    @ResponseBody
    String statisticsCrashLog(HttpServletRequest request) {
        String loginfo = request.getParameter("loginfo");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("log", loginfo);

        statisticsDao.insertBehavior(CrashLog.class, conditions);
        return "ok";
    }
}