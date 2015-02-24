package com.darfoo.backend.model.cota;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zjh on 15-2-23.
 */

//标识出资源中需要从界面view层获得并插入到数据库的字段名称
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModelInsert {
}
