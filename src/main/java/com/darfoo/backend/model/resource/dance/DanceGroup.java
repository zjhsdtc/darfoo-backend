package com.darfoo.backend.model.resource.dance;

import com.darfoo.backend.caches.cota.CacheInsert;
import com.darfoo.backend.caches.cota.CacheInsertEnum;
import com.darfoo.backend.model.cota.*;
import com.darfoo.backend.model.resource.Image;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by zjh on 14-11-16.
 */

//舞队
@Entity
@Table(name = "dancegroup")
@ModelOperation(insertMethod = "insertDanceGroup", updateMethod = "updateDanceGroup")
public class DanceGroup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //引用下面名为mysql的主键生成方式
    @CacheInsert(type = CacheInsertEnum.NORMAL)
    Integer id;

    @Column(name = "NAME", nullable = false, columnDefinition = "varchar(255) not null")
    @CacheInsert(type = CacheInsertEnum.NORMAL)
    @ModelInsert
    @ModelUpdate
    String name;

    @Column(name = "DESCRIPTION", nullable = false, columnDefinition = "varchar(255) not null")
    @CacheInsert(type = CacheInsertEnum.NORMAL)
    @ModelInsert
    @ModelUpdate
    String description;

    //暂时弄成单向对应关系
    @OneToOne(targetEntity = Image.class, fetch = FetchType.EAGER)
    @Cascade(value = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.SAVE_UPDATE, CascadeType.DELETE})
    @JoinColumn(name = "IMAGE_ID", referencedColumnName = "id", updatable = true)
    @CacheInsert(type = CacheInsertEnum.RESOURCE)
    Image image;

    //点击量
    @Column(name = "HOTTEST", nullable = true, updatable = true, columnDefinition = "bigint(64) default 0")
    Long hottest = 0L;

    @Transient
    @ModelInsert
    @ModelUpload(type = ModelUploadEnum.SMALL)
    @ModelUpdate
    String imagekey;

    @Column(name = "TYPE", nullable = false, updatable = true, columnDefinition = "int default 0")
    DanceGroupType type = DanceGroupType.STAR;

    @Column(name = "HOT", nullable = false, updatable = true, columnDefinition = "int default 0")
    DanceGroupHot hot = DanceGroupHot.NOTHOT;

    public DanceGroup() {

    }

    public Long getHottest() {
        return hottest;
    }

    public void setHottest(Long hottest) {
        this.hottest = hottest;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public DanceGroupType getType() {
        return type;
    }

    public void setType(DanceGroupType type) {
        this.type = type;
    }

    public DanceGroupHot getHot() {
        return hot;
    }

    public void setHot(DanceGroupHot hot) {
        this.hot = hot;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getId() + " " + this.getName() + " " + this.getDescription() + " " + this.getImage().getImage_key());
        return sb.toString();
    }
}
