package com.darfoo.backend.model.statistics.clicktime;

import com.darfoo.backend.model.cota.annotations.CSVTitle;
import com.darfoo.backend.model.cota.annotations.ModelAttrSuper;

/**
 * Created by zjh on 15-3-3.
 */

public class CommonClickTime {
    @ModelAttrSuper
    @CSVTitle(title = "mac地址")
    public String mac;

    @ModelAttrSuper
    @CSVTitle(title = "ip地址")
    public String hostip;

    @ModelAttrSuper
    @CSVTitle(title = "唯一标识")
    public String uuid;

    @ModelAttrSuper
    @CSVTitle(title = "时间戳")
    public Long timestamp;

    @ModelAttrSuper
    @CSVTitle(title = "日期")
    public String date;

    @ModelAttrSuper
    @CSVTitle(title = "资源名称")
    public String title;
}
