package com.darfoo.backend.service.admin;

import com.darfoo.backend.dao.cota.CommonDao;
import com.darfoo.backend.model.cota.annotations.ModelInsert;
import com.darfoo.backend.model.cota.annotations.ModelUpload;
import com.darfoo.backend.model.cota.enums.DanceVideoType;
import com.darfoo.backend.model.cota.enums.ModelUploadEnum;
import com.darfoo.backend.model.resource.dance.DanceGroup;
import com.darfoo.backend.model.resource.dance.DanceMusic;
import com.darfoo.backend.model.resource.dance.DanceVideo;
import com.darfoo.backend.service.category.DanceVideoCates;
import com.darfoo.backend.service.cota.TypeClassMapping;
import com.darfoo.backend.utils.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;


/**
 * Created by zjh on 14-11-27.
 * 用于上传伴奏，视频和教学视频
 */

@Controller
public class UploadController {
    @Autowired
    CommonDao commonDao;

    public int commonInsertResource(Class resource, HttpServletRequest request, HttpSession session) {
        HashMap<String, String> insertcontents = new HashMap<String, String>();

        for (Field field : resource.getDeclaredFields()) {
            if (field.isAnnotationPresent(ModelInsert.class)) {
                String insertkey = field.getName().toLowerCase();
                insertcontents.put(insertkey, request.getParameter(insertkey));
            }
        }

        if (resource == DanceVideo.class) {
            String[] categories = request.getParameterValues("categories");
            if (categories == null) {
                insertcontents.put("category", "");
            } else {
                for (String category : categories) {
                    insertcontents.put(String.format("category%s", category), DanceVideoCates.danceVideoCategoryMap.get(category));
                }
            }
        }

        if (resource == DanceVideo.class || resource == DanceGroup.class) {
            insertcontents.put("type", request.getParameter("innertype").toLowerCase());

            String imagekey = String.format("%s-imagekey-%s.%s", resource.getSimpleName().toLowerCase(), System.currentTimeMillis(), request.getParameter("imagetype"));
            insertcontents.put("imagekey", imagekey);
            session.setAttribute("imagekey", imagekey);
        }

        if (resource == DanceMusic.class) {
            insertcontents.put("category", request.getParameter("musicletter").toUpperCase());
        }

        HashMap<String, Integer> result = commonDao.insertResource(resource, insertcontents);
        int statuscode = result.get("statuscode");
        int insertid = result.get("insertid");

        System.out.println("status code is -> " + statuscode);

        if (resource == DanceVideo.class) {
            String videokey = String.format("%s-%s-%d.%s", insertcontents.get("title"), resource.getSimpleName().toLowerCase(), insertid, insertcontents.get("videotype"));
            session.setAttribute("videokey", videokey);
        }

        if (resource == DanceMusic.class) {
            String musickey = String.format("%s-%s-%d.%s", insertcontents.get("title"), resource.getSimpleName().toLowerCase(), insertid, "mp3");
            session.setAttribute("musickey", musickey);
        }
        return statuscode;
    }

    public String commonUploadResource(Class resource, HashMap<String, CommonsMultipartFile> uploadresources, HttpSession session) {
        for (Field field : resource.getDeclaredFields()) {
            if (field.isAnnotationPresent(ModelUpload.class)) {
                field.setAccessible(true);
                Annotation annotation = field.getAnnotation(ModelUpload.class);
                ModelUpload modelUpload = (ModelUpload) annotation;

                final CommonsMultipartFile file = uploadresources.get(field.getName());
                final String key = session.getAttribute(field.getName()).toString();
                final ModelUploadEnum type = modelUpload.type();

                ServiceUtils.operateQiniuResourceAsync(file, key, type, ServiceUtils.QiniuOperationType.UPLOAD);
            }
        }
        return "success";
    }

    @RequestMapping(value = "/resources/new/{type}", method = RequestMethod.GET)
    public String uploadResourceWithType(@PathVariable String type, ModelMap modelMap) {
        if (type.equals("dancegroup")) {
            modelMap.put("typenames", TypeClassMapping.danceGroupTypeNameMap);
        }
        if (type.equals("dancevideo")) {
            modelMap.put("typenames", TypeClassMapping.danceVideoTypeNameMap);
        }
        if (type.equals("dancemusic")) {
            return "upload/uploaddancemusic";
        }
        modelMap.put("operation", "upload");
        modelMap.put("type", type);
        return "resource/resourcetype";
    }

    @RequestMapping(value = "/resources/new/{type}/{innertype}", method = RequestMethod.GET)
    public String uploadResource(@PathVariable String type, @PathVariable String innertype, ModelMap modelMap) {
        if (type.equals("dancevideo")) {
            DanceVideoType danceVideoType = DanceVideoType.valueOf(innertype.toUpperCase());
            HashMap<String, Object> conditions = new HashMap<String, Object>();
            conditions.put("type", TypeClassMapping.danceVideoTypeDanceGroupTypeMap.get(danceVideoType));
            modelMap.addAttribute("authors", commonDao.getResourcesByFields(DanceGroup.class, conditions));
            modelMap.addAttribute("type", type);
            modelMap.addAttribute("innertype", innertype);
        } else if (type.equals("dancegroup")) {
            modelMap.addAttribute("type", type);
            modelMap.addAttribute("innertype", innertype);
        }

        return String.format("upload/upload%s", type);
    }

    @RequestMapping(value = "/resources/{type}/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Integer createResource(@PathVariable String type, HttpServletRequest request, HttpSession session) {
        return commonInsertResource(TypeClassMapping.typeClassMap.get(type), request, session);
    }

    @RequestMapping(value = "/resources/{type}/resource/new", method = RequestMethod.GET)
    public String uploadMediaResource(@PathVariable String type) {
        return String.format("upload/upload%sresource", type);
    }

    @RequestMapping(value = "/resources/dancevideo/resource/create", method = RequestMethod.POST)
    public String createMediaResource(@RequestParam("videoresource") CommonsMultipartFile videoresource, @RequestParam("imageresource") CommonsMultipartFile imageresource, HttpSession session) {
        HashMap<String, CommonsMultipartFile> uploadresources = new HashMap<String, CommonsMultipartFile>();
        uploadresources.put("videokey", videoresource);
        uploadresources.put("imagekey", imageresource);
        return commonUploadResource(DanceVideo.class, uploadresources, session);
    }

    @RequestMapping(value = "/resources/dancemusic/resource/create", method = RequestMethod.POST)
    public String createMusicResourceNoPic(@RequestParam("musicresource") CommonsMultipartFile musicresource, HttpSession session) {
        HashMap<String, CommonsMultipartFile> uploadresources = new HashMap<String, CommonsMultipartFile>();
        uploadresources.put("musickey", musicresource);
        return commonUploadResource(DanceMusic.class, uploadresources, session);
    }

    @RequestMapping(value = "/resources/dancegroup/resource/create", method = RequestMethod.POST)
    public String createAuthorResource(@RequestParam("imageresource") CommonsMultipartFile imageresource, HttpSession session) {
        HashMap<String, CommonsMultipartFile> uploadresources = new HashMap<String, CommonsMultipartFile>();
        uploadresources.put("imagekey", imageresource);
        return commonUploadResource(DanceGroup.class, uploadresources, session);
    }
}