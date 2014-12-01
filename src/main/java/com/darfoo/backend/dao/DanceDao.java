package com.darfoo.backend.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.darfoo.backend.model.DanceGroup;
import com.darfoo.backend.model.DanceGroupImage;

@Component
@SuppressWarnings("unchecked")
public class DanceDao {
	@Autowired
	private SessionFactory sf;
	
	public void insertSingleDanceGroup(DanceGroup group){
		DanceGroupImage image = group.getImage();
		try{
			Session session = sf.getCurrentSession();
			Criteria c = session.createCriteria(DanceGroupImage.class).add(Restrictions.eq("image_key", image.getImage_key()));
			c.setReadOnly(true);
			List<DanceGroupImage> l_image = c.list();
			if(l_image.size() > 0){
				//图片库中包含此图片信息，用持久化对象代替原来的image
				group.setImage(l_image.get(0));
			}
			session.save(group);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

    /**
     * 根据name判断该舞队是否已经存在表里
     * @param name 待判断的舞队的name
     * @return 表中已经存在该name对应的舞队信息,返回true;反之，返回一个false
     */
    public boolean isDanceGroupExists(String name){
        boolean isExist = true;
        try{
            Session session = sf.getCurrentSession();
            String sql = "select * from dancegroup where name=:name";
            DanceGroup danceGroup = (DanceGroup)session.createSQLQuery(sql).addEntity(DanceGroup.class).setString("name", name).uniqueResult();
            isExist = (danceGroup==null)?false:true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return isExist;
    }

	/**
	 * 获取舞队信息
	 * @param count 需要返回的舞队数量
	 * @return 返回舞队List
	 * **/
	public List<DanceGroup> getDanceGroups(int count){
		List<DanceGroup> l_dance = new ArrayList<DanceGroup>();;
		try{
			Session session = sf.getCurrentSession();
			Criteria c = session.createCriteria(DanceGroup.class);
			c.setReadOnly(true);
			c.setMaxResults(count);
			l_dance = c.list();
		}catch(Exception e){
			e.printStackTrace();
		}
		return l_dance;
	}
	/**
	 * 根据id删除dancegroup
	 * **/
	public int deleteDanceGroupById(Integer id){
		int res = 0;
		try{
			Session session = sf.getCurrentSession();
			String sql = "delete from dancegroup where id=:id";
			res = session.createSQLQuery(sql).setInteger("id", id).executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
}
