package com.springapp.mvc.resource;

import com.darfoo.backend.dao.CRUDEvent;
import com.darfoo.backend.dao.cota.CommonDao;
import com.darfoo.backend.dao.resource.ImageDao;
import com.darfoo.backend.model.resource.Image;
import com.darfoo.backend.model.UpdateCheckResponse;
import com.darfoo.backend.model.resource.Video;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/springmvc-hibernate.xml")
public class ImageDaoTests {
    @Autowired
    ImageDao imageDao;
    @Autowired
    CommonDao commonDao;

    @Test
    public void insertImage() {
        Image image = new Image();
        image.setImage_key("CCCC");
        imageDao.insertSingleImage(image);
    }

    @Test
    public void getAllImage() {
        List<Image> l_image = commonDao.getAllResource(Image.class);
        for (Image image : l_image) {
            System.out.println(image.getId() + " " + image.getImage_key());
        }
    }

    /**
     * 根据id更新image的image_key
     * 先做check
     * 再做update
     * *
     */
    @Test
    public void updateImage() {
        Integer id = 2;
        String newImageKey = "aasa1";
        UpdateCheckResponse response = imageDao.updateImageCheck(id, newImageKey);
        if (response.updateIsReady()) {
            HashMap<String, Object> updateMap = new HashMap<String, Object>();
            updateMap.put("image_key", newImageKey);

            System.out.println(CRUDEvent.getResponse(commonDao.updateResourceFieldsById(Image.class, id, updateMap)));
        } else {
            System.out.println("没有对应id的image或者插入imagekey重复");
        }

    }

    /**
     * 删除image(将有外键的关系对应的字段设为null)
     * *
     */
    @Test
    public void deleteImage() {
        Integer id = 115;
        int res = commonDao.deleteResourceById(Image.class, id);
        System.out.println(CRUDEvent.getResponse(res));
    }
}