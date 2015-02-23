package com.darfoo.backend.service.admin;

import com.darfoo.backend.dao.cota.AccompanyDao;
import com.darfoo.backend.dao.cota.CommonDao;
import com.darfoo.backend.dao.resource.*;
import com.darfoo.backend.model.category.MusicCategory;
import com.darfoo.backend.model.category.TutorialCategory;
import com.darfoo.backend.model.category.VideoCategory;
import com.darfoo.backend.model.cota.ModelInsert;
import com.darfoo.backend.model.resource.*;
import com.darfoo.backend.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by zjh on 14-11-27.
 * 用于上传伴奏，视频和教学视频
 */

@Controller
public class UploadController {
    @Autowired
    AuthorDao authorDao;
    @Autowired
    VideoDao videoDao;
    @Autowired
    TutorialDao tutorialDao;
    @Autowired
    ImageDao imageDao;
    @Autowired
    MusicDao musicDao;
    @Autowired
    CommonDao commonDao;
    @Autowired
    AccompanyDao accompanyDao;

    public HashMap<String, Integer> insertSingleMusic(String musictitle, String authorname, String imagekey, String musicbeat, String musicstyle, String musicletter) {
        HashMap<String, Integer> resultMap = new HashMap<String, Integer>();

        boolean isSingleLetter = ServiceUtils.isSingleCharacter(musicletter);
        if (isSingleLetter) {
            System.out.println("是单个大写字母");
        } else {
            System.out.println("不是单个大写字母");
            resultMap.put("statuscode", 505);
            resultMap.put("insertid", -1);
            return resultMap;
        }

        //伴奏title可以重名,但是不可能出现authorname和title都一样的情况,也就是一个作者名字对应的伴奏中不会出现重名的情况

        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("title", musictitle);
        conditions.put("author_name", authorname);

        Music queryMusic = (Music) commonDao.getResourceByFields(Music.class, conditions);
        if (queryMusic == null) {
            System.out.println("伴奏名字和作者名字组合不存在，可以进行插入");
        } else {
            //System.out.println(queryMusic.toString());
            System.out.println("伴奏名字和作者名字组合已存在，不可以进行插入了，是否需要修改");
            resultMap.put("statuscode", 503);
            resultMap.put("insertid", -1);
            return resultMap;
        }

        Music music = new Music();

        MusicCategory beat = new MusicCategory();
        MusicCategory style = new MusicCategory();
        MusicCategory letter = new MusicCategory();
        beat.setTitle(musicbeat);
        style.setTitle(musicstyle);
        letter.setTitle(musicletter);
        Set<MusicCategory> s_mCategory = music.getCategories();
        s_mCategory.add(beat);
        s_mCategory.add(style);
        s_mCategory.add(letter);
        music.setTitle(musictitle);
        music.setMusic_key(musictitle);
        music.setAuthorName(authorname);
        music.setUpdate_timestamp(System.currentTimeMillis());
        int insertStatus = musicDao.insertSingleMusic(music);
        if (insertStatus == -1) {
            System.out.println("插入伴奏失败");
        } else {
            System.out.println("插入伴奏成功，伴奏id是" + insertStatus);
        }

        HashMap<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put("music_key", musictitle + "-" + insertStatus);
        commonDao.updateResourceFieldsById(Music.class, insertStatus, updateMap);

        resultMap.put("statuscode", 200);
        resultMap.put("insertid", insertStatus);
        return resultMap;
    }

    public int insertSingleAuthor(String authorname, String description, String imagekey) {
        if (authorDao.isExistAuthor(authorname)) {
            System.out.println("作者已存在");
            return 501;
        } else {
            System.out.println("无该author记录，可以创建");
        }

        if (imagekey.equals("")) {
            return 508;
        }

        HashMap<String, Object> imageConditions = new HashMap<String, Object>();
        imageConditions.put("image_key", imagekey);

        Image image = (Image) commonDao.getResourceByFields(Image.class, imageConditions);
        if (image == null) {
            System.out.println("图片不存在，可以进行插入");
            image = new Image();
            image.setImage_key(imagekey);
            imageDao.insertSingleImage(image);
        } else {
            System.out.println("图片已存在，不可以进行插入了，是否需要修改");
            return 503;
        }

        Author author = new Author();
        author.setName(authorname);
        author.setDescription(description);
        author.setImage(image);
        authorDao.insertAuthor(author);

        return 200;
    }

    public int commonInsertResource(Class resource, HttpServletRequest request, HttpSession session) {
        HashMap<String, String> insertcontents = new HashMap<String, String>();

        for (Field field : resource.getDeclaredFields()) {
            if (field.isAnnotationPresent(ModelInsert.class)) {
                String insertkey = field.getName().toLowerCase();
                insertcontents.put(insertkey, request.getParameter(insertkey));
            }
        }

        if (resource == Video.class) {
            insertcontents.put("category1", request.getParameter("videospeed"));
            insertcontents.put("category2", request.getParameter("videodifficult"));
            insertcontents.put("category3", request.getParameter("videostyle"));
            insertcontents.put("category4", request.getParameter("videoletter").toUpperCase());
        }

        if (resource == Tutorial.class) {
            insertcontents.put("category1", request.getParameter("videospeed"));
            insertcontents.put("category2", request.getParameter("videodifficult"));
            insertcontents.put("category3", request.getParameter("videostyle"));
        }

        HashMap<String, Integer> result = commonDao.insertResource(resource, insertcontents);
        int statuscode = result.get("statuscode");
        int insertid = result.get("insertid");

        System.out.println("status code is -> " + statuscode);

        if (resource == Video.class || resource == Tutorial.class) {
            session.setAttribute("videokey", insertcontents.get("title") + "-" + insertid + "." + insertcontents.get("videotype"));
            session.setAttribute("imagekey", insertcontents.get("imagekey"));

        }

        return statuscode;
    }

    @RequestMapping(value = "/resources/{type}/new", method = RequestMethod.GET)
    public String uploadResource(@PathVariable String type, ModelMap modelMap, HttpSession session) {
        session.setAttribute("resource", type);
        modelMap.addAttribute("resource", type);
        modelMap.addAttribute("authors", commonDao.getAllResource(Author.class));
        return "upload" + type;
    }

    @RequestMapping(value = "/resources/{type}/create", method = RequestMethod.POST)
    public @ResponseBody Integer reateResource(@PathVariable String type, HttpServletRequest request, HttpSession session) {
        if (type.equals("video")) {
            return commonInsertResource(Video.class, request, session);
        } else if (type.equals("tutorial")) {
            return commonInsertResource(Tutorial.class, request, session);
        } else {
            return 404;
        }
    }

    @RequestMapping(value = "/resources/{type}resource/new", method = RequestMethod.GET)
    public String uploadMediaResource(@PathVariable String type) {
        return String.format("upload%sresource", type);
    }

    @RequestMapping(value = "/resources/{type}resource/create")
    public String createMediaResource(@RequestParam("videoresource") CommonsMultipartFile videoresource, @RequestParam("imageresource") CommonsMultipartFile imageresource, @PathVariable String type, HttpSession session) {
        String videokey = (String) session.getAttribute("videokey");
        String imagekey = (String) session.getAttribute("imagekey");

        System.out.println(videokey + " " + imagekey);

        String videoStatusCode = "";
        String imageStatusCode = "";

        try {
            videoStatusCode = ServiceUtils.uploadLargeResource(videoresource, videokey);
            imageStatusCode = ServiceUtils.uploadSmallResource(imageresource, imagekey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (videoStatusCode.equals("200") && imageStatusCode.equals("200")) {
            return "success";
        } else {
            return "fail";
        }
    }

    /*music part*/
    @RequestMapping(value = "/resources/music/new", method = RequestMethod.GET)
    public String uploadMusic(ModelMap modelMap, HttpSession session) {
        session.setAttribute("resource", "music");
        modelMap.addAttribute("resource", "music");
        return "uploadmusic";
    }

    @RequestMapping(value = "/resources/music/create", method = RequestMethod.POST)
    public
    @ResponseBody
    String createMusic(HttpServletRequest request, HttpSession session) {
        String musicTitle = request.getParameter("title");
        String authorName = request.getParameter("authorname");
        //String imagekey = request.getParameter("imagekey");
        String musicBeat = request.getParameter("musicbeat");
        String musicStyle = request.getParameter("musicstyle");
        String musicLetter = request.getParameter("musicletter").toUpperCase();
        Long update_timestamp = System.currentTimeMillis() / 1000;
        //System.out.println("requests: " + musicTitle + " " + authorName + " " + imagekey + " " + musicBeat + " " + musicStyle + " " + musicLetter + " " + update_timestamp);
        System.out.println("requests: " + musicTitle + " " + authorName + " " + " " + musicBeat + " " + musicStyle + " " + musicLetter + " " + update_timestamp);

        //HashMap<String, Integer> resultMap = this.insertSingleMusic(musicTitle, authorName, imagekey, musicBeat, musicStyle, musicLetter);
        HashMap<String, Integer> resultMap = this.insertSingleMusic(musicTitle, authorName, "", musicBeat, musicStyle, musicLetter);
        int statusCode = resultMap.get("statuscode");
        System.out.println("status code is: " + statusCode);
        if (statusCode != 200) {
            return statusCode + "";
        } else {
            int insertid = resultMap.get("insertid");
            session.setAttribute("musicTitle", musicTitle + "-" + insertid + ".mp3");
            //session.setAttribute("musicImage", imagekey);
            return statusCode + "";
        }
    }

    @RequestMapping(value = "/resources/musicresource/new", method = RequestMethod.GET)
    public String uploadMusicResource() {
        return "uploadmusicresource";
    }

    @RequestMapping("/resources/musicresource/create")
    public String createMusicResource(@RequestParam("musicresource") CommonsMultipartFile musicresource, @RequestParam("imageresource") CommonsMultipartFile imageresource, HttpSession session) {
        //upload
        String musicTitle = (String) session.getAttribute("musicTitle");
        String imageKey = (String) session.getAttribute("musicImage");

        String videoResourceName = musicresource.getOriginalFilename();
        String imageResourceName = imageresource.getOriginalFilename();

        System.out.println(videoResourceName + " " + imageResourceName);

        String musicStatusCode = "";
        String imageStatusCode = "";

        try {
            musicStatusCode = ServiceUtils.uploadLargeResource(musicresource, musicTitle);
            imageStatusCode = ServiceUtils.uploadSmallResource(imageresource, imageKey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (musicStatusCode.equals("200") && imageStatusCode.equals("200")) {
            return "success";
        } else {
            return "fail";
        }
    }

    @RequestMapping("/resources/musicresourcenopic/create")
    public String createMusicResourceNoPic(@RequestParam("musicresource") CommonsMultipartFile musicresource, HttpSession session) {
        //upload
        String musicTitle = (String) session.getAttribute("musicTitle");

        String videoResourceName = musicresource.getOriginalFilename();

        System.out.println("musicresourcename -> " + videoResourceName);

        String musicStatusCode = "";

        try {
            musicStatusCode = ServiceUtils.uploadLargeResource(musicresource, musicTitle);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (musicStatusCode.equals("200")) {
            return "success";
        } else {
            return "fail";
        }
    }
    /*end of music part*/

    /*author part*/
    //作者信息，不管是视频作者还是伴奏作者
    @RequestMapping(value = "/resources/author/new", method = RequestMethod.GET)
    public String uploadAuthor(ModelMap modelMap, HttpSession session) {
        session.setAttribute("resource", "author");
        modelMap.addAttribute("resource", "author");
        return "uploadauthor";
    }

    @RequestMapping(value = "/resources/author/create", method = RequestMethod.POST)
    public
    @ResponseBody
    String createAuthor(HttpServletRequest request, HttpSession session) {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String imagekey = request.getParameter("imagekey");
        System.out.println("requests: " + name + " " + description);

        session.setAttribute("authorImage", imagekey);

        int statusCode = this.insertSingleAuthor(name, description, imagekey);
        return statusCode + "";
    }

    @RequestMapping(value = "/resources/authorresource/new", method = RequestMethod.GET)
    public String uploadAuthorResource() {
        return "uploadauthorresource";
    }

    @RequestMapping("/resources/authorresource/create")
    public String createAuthorResource(@RequestParam("imageresource") CommonsMultipartFile imageresource, HttpSession session) {
        //upload
        String imagekey = (String) session.getAttribute("authorImage");
        System.out.println("imagekey in session: " + imagekey);

        String imageResourceName = imageresource.getOriginalFilename();

        System.out.println(imageResourceName);

        String imageStatusCode = "";

        try {
            imageStatusCode = ServiceUtils.uploadSmallResource(imageresource, imagekey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (imageStatusCode.equals("200")) {
            return "success";
        } else {
            return "fail";
        }
    }
    /*end of author part*/
}
