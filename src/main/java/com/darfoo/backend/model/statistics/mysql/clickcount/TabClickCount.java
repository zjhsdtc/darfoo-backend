package com.darfoo.backend.model.statistics.mysql.clickcount;

import com.darfoo.backend.model.cota.CSVTitle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by zjh on 15-3-3.
 */

//底部菜单的点击热度
@Entity
@Table(name = "tabclickcount")
public class TabClickCount extends CommonClickCount implements Serializable {
    @Column(name = "tabid", nullable = false)
    @CSVTitle(title = "底部菜单标识")
    public Integer tabid;
}
