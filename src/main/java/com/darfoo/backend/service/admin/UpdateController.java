package com.darfoo.backend.service.admin;

import com.darfoo.backend.dao.CRUDEvent;
import com.darfoo.backend.dao.VideoDao;
import com.darfoo.backend.model.UpdateCheckResponse;
import com.darfoo.backend.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zjh on 14-12-4.
 */

@Controller
public class UpdateController {
    @Autowired
    VideoDao videoDao;

    @RequestMapping(value = "/admin/video/update", method = RequestMethod.POST)
    public @ResponseBody String updateVideo(HttpServletRequest request, HttpSession session){
        String videoTitle = request.getParameter("title");
        String authorName = request.getParameter("authorname");
        String imageKey = request.getParameter("imagekey");
        String videoSpeed = request.getParameter("videospeed");
        String videoDifficult = request.getParameter("videodifficult");
        String videoStyle = request.getParameter("videostyle");
        String videoLetter = request.getParameter("videoletter").toUpperCase();
        System.out.println("requests: " + videoTitle + " " + authorName + " " + imageKey + " " + videoSpeed + " " + videoDifficult + " " + videoStyle + " " + videoLetter);

        boolean isSingleLetter = ServiceUtils.isSingleCharacter(videoLetter);
        if (isSingleLetter){
            System.out.println("是单个大写字母");
        }else{
            System.out.println("不是单个大写字母");
            return 505+"";
        }

        Integer vid = Integer.parseInt(request.getParameter("id"));
        UpdateCheckResponse response = videoDao.updateVideoCheck(vid, authorName, imageKey); //先检查图片和作者姓名是否已经存在
        System.out.println(response.updateIsReady()); //若response.updateIsReady()为false,可以根据response成员变量具体的值来获悉是哪个值需要先插入数据库
        Set<String> categoryTitles = new HashSet<String>();
        categoryTitles.add(videoSpeed);
        categoryTitles.add(videoDifficult);
        categoryTitles.add(videoStyle);
        categoryTitles.add(videoLetter.toUpperCase());
        if(response.updateIsReady()){
            //updateIsReady为true表示可以进行更新操作
            System.out.println(CRUDEvent.getResponse(videoDao.updateVideo(vid, authorName, imageKey, categoryTitles, System.currentTimeMillis())));
        }else{
            System.out.println("请根据reponse中的成员变量值来设计具体逻辑");
        }

        return 200+"";
    }
}
