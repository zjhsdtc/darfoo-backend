package com.darfoo.backend.model.resource;

import com.darfoo.backend.caches.cota.CacheInsert;
import com.darfoo.backend.caches.cota.CacheInsertEnum;
import com.darfoo.backend.model.category.TutorialCategory;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zjh on 14-11-16.
 */
@Entity
@Table(name = "tutorial")
public class Tutorial implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @CacheInsert(type = CacheInsertEnum.NORMAL)
    Integer id;

    //单向N-1  在tutorialvideo表中增加一个外键列IMAGE_ID(music的主键)
    @ManyToOne(targetEntity = Image.class)
    @Cascade(value = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "IMAGE_ID", referencedColumnName = "id")
    @CacheInsert(type = CacheInsertEnum.RESOURCE)
    Image image;

    //单向N-1 在tutorialvideo表中增加一个外键列AUTHOR_ID(author的主键)
    @ManyToOne(targetEntity = Author.class)
    @Cascade(value = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "AUTHOR_ID", referencedColumnName = "id")
    @CacheInsert(type = CacheInsertEnum.NORMAL)
    Author author;

    //category
    @ManyToMany(targetEntity = TutorialCategory.class)
    @JoinTable(name = "tutorial_category", joinColumns = {@JoinColumn(name = "video_id", referencedColumnName = "id", nullable = false, columnDefinition = "int(11) not null")},
            inverseJoinColumns = {@JoinColumn(name = "category_id", nullable = false, columnDefinition = "int(11) not null")})
    Set<TutorialCategory> categories = new HashSet<TutorialCategory>();

    @Column(name = "VIDEO_KEY", unique = true, nullable = false, columnDefinition = "varchar(255) not null")
    @CacheInsert(type = CacheInsertEnum.RESOURCE)
    String video_key;

    @Column(name = "TITLE", nullable = false, columnDefinition = "varchar(255) not null")
    @CacheInsert(type = CacheInsertEnum.NORMAL)
    String title;

    @Column(name = "UPDATE_TIMESTAMP", nullable = false, columnDefinition = "bigint(64) not null")
    @CacheInsert(type = CacheInsertEnum.NORMAL)
    Long update_timestamp;

    @ManyToOne(targetEntity = Music.class)
    @JoinColumn(name = "MUSIC_ID", referencedColumnName = "id", updatable = true, nullable = true)
    Music music;

    //点击量
    @Column(name = "HOTTEST", nullable = true, updatable = true, columnDefinition = "bigint(64) default 0")
    Long hottest;

    @Column(name = "RECOMMEND", nullable = true, updatable = true, columnDefinition = "int default 0")
    Integer recommend;

    public Tutorial() {
    }

    public Integer getRecommend() {
        return recommend;
    }

    public void setRecommend(Integer recommend) {
        this.recommend = recommend;
    }

    public Long getHottest() {
        return hottest;
    }

    public void setHottest(Long hottest) {
        this.hottest = hottest;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Set<TutorialCategory> getCategories() {
        return categories;
    }

    public void setCategories(Set<TutorialCategory> categories) {
        this.categories = categories;
    }

    public String getVideo_key() {
        return video_key;
    }

    public void setVideo_key(String video_key) {
        this.video_key = video_key;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUpdate_timestamp() {
        return update_timestamp;
    }

    public void setUpdate_timestamp(Long update_timestamp) {
        this.update_timestamp = update_timestamp;
    }

    public String toString(boolean isShowCategory) {
        StringBuilder sb = new StringBuilder();
        sb.append("title:" + title + "\nvideo_key:" + video_key + "\nupdate_timestamp:" + update_timestamp);
        if (author == null) {
            sb.append("\nauthor:null");
        } else {
            sb.append("\nauthor:" + author.getName() + "  演唱家信息:" + author.getDescription());
            if (author.getImage() == null) {
                sb.append(" 作者图片:" + null);
            } else {
                sb.append(" 作者图片:" + author.getImage().getImage_key());
            }
        }
        if (image == null) {
            sb.append("\n视频图片:null");
        } else {
            sb.append("\n视频图片:" + image.getImage_key());
        }
        if (isShowCategory) {
            if (categories == null) {
                sb.append("种类为空");
            } else {
                for (TutorialCategory category : categories) {
                    sb.append("\n").append("种类:" + category.getTitle() + " 描述:" + category.getDescription());
                }
            }
        }
        return sb.toString();
    }
}
