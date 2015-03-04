package com.darfoo.backend.model.statistics.clickcount;

import com.darfoo.backend.model.cota.CSVTitle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by zjh on 15-3-2.
 */

//统计资源的点击热度
@Entity
@Table(name = "resourceclickcount")
public class ResourceClickCount extends CommonClickCount implements Serializable {
    @Column(name = "type", nullable = false)
    @CSVTitle(title = "资源类型")
    public String type;

    @Column(name = "resourceid", nullable = false)
    @CSVTitle(title = "资源标识")
    public Integer resourceid;
}
