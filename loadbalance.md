## 2014/12/9

### goal: nginx deals with statics whereas tomcats for dynamics;

服务器上已经有一个tomcat,位于/usr/local/darfoo/apache-tomcat-7.0.47,它的http connect端口为8080 shutdown端口为8005 AJP端口为8009 (conf/server.xml)
现在/usr/local/darfoo再配置两个用于负载均衡的tomcat,版本同第一个,分别叫tomcatbak1,tomcatbak2(manager-gui user:darfoo password:darfoodarfoo)
tomcatbak1 http connect端口为8081 shutdown端口为8006 AJP端口为8010  
tomcatbak2 http connect端口为8082 shutdown端口为8007 AJP端口为8011
现将darfoobackend应用放在/usr/local/darfoo/webapps下面,在tomcatbak1,tomcatbak2的conf/Catalina/localhost下面加了配置文件darfoobackend.xml,用于找到该应用路径。
这样通过112.134.68.27:8081/及112.134.68.27:8082/ 也可以访问该应用。

```
<?xml version="1.0" encoding="utf-8"?>
<Context path="" docBase="/usr/local/darfoo/webapps/darfoobackend" reloadable="true" privileges="true">
</Context>
```

-------------------------------------------------------------------------

### 之后便是要在nginx下面增加负载均衡

* 本地调试:

虚拟机192.168.1.33(CentOs.TOMCAT),配置了两个tomcat，位于/usr/local/tomcat1与/usr/local/tomcat2,端口配置分别为http8000,shutdown8005,AJP8009,enginejvmRoute=tomcat1和
http8001,shutdown8006,AJP8010,enginejvmRoute=tomcat2。将darfoobackend(00e7f3)打成war包放到webapps下面，修改/WEB-INF/pages/login.jsp中的"后台登陆",
分别改为后台登陆@tomcat1,后台登陆@tomcat2（简单区分）

* nginx中在http{}中添加

```
upstream backend_server {
        server localhost:8080 weight=1;
        server localhost:8081 weight=1;
        #server   localhost:8080 weight=1 max_fails=2 fail_timeout=30s;
        #server   localhost:8081 weight=1 max_fails=2 fail_timeout=30s;
        ip_hash; 
}
```

* nginx中在server{}中添加

```
location / {
                #root html/default;
                #index index.html index.htm;
                proxy_pass http://backend_server;
           }
```

启动nginx及两个tomcat,访问http://192.168.1.33/darfoobackend/rest/login	并刷新，可以看到原来"后台登陆"的位置会出现"后台登陆@tomcat1"与"后台登陆@tomcat2"的交替.
之后主要要修改nginx配置文件的参数。

### error

我在本地测试多个tomcat时并没有发现这个问题，但是报另一个异常

* errorA

```
2014-12-20 20:03:30.145 INFO  com.mchange.v2.uid.UidUtils.generateVmId:75行 
- Failed to get local InetAddress for VMID. This is unlikely to matter. At all. We'll add some extra randomness
java.net.UnknownHostException: centos.tomcat: centos.tomcat: 未知的名称或服务(非主要~解决方法:/etc/hosts中为127.0.0.1增添一个domain  centos.tomcat即可)
```

* errorB(只有关闭tomcat时会出现)

```
严重: The web application [/darfoobackend] registered the JDBC driver [com.mysql.jdbc.Driver] but failed to unregister it when the web application was stopped. To prevent a memory leak, the JDBC Driver has been forcibly unregistered.
十二月 20, 2014 8:02:14 下午 org.apache.catalina.loader.WebappClassLoader clearReferencesThreads
```

tomcat自己会取消注册jdbc驱动，理论上对程序无影响，[问题来源](http://www.cnblogs.com/yezhenhan/archive/2011/08/12/2136391.html)
以上两个问题是待解决的。

当前需要解决的:多个tomcat之间session共享(nginx配置或者memcache)
1. nginx配置： 在上述配置中将ip_hash使能，这样nginx会根据ip为请求分配一个固定的后端(不过这玩意儿特别不好，http://www.oschina.net/question/12_621)
2. memcache:待测试
3. redis:todo

----------------------------------------------------------------------------

（吉卉发现）在darfoo服务器上，当另外两个tomcat启动后，原来的tomcat在部署应用时会报一个空指针错误，关闭另外两个tomcat就不会报错。

这个异常并不影响系统的正常使用TAT，所以得继续查找问题的原因。

----------------------------------------------------------------------------

现在在darfoo服务器上配置了nginx+3个tomcat
nginx的配置文件主要是加了upstream配置，具体在/opt/nginx/conf/nginx.conf
tomcat主要改动在于darfoobackend应用文件的位置，已经从原来的webapps目录中转移到外部的/usr/local/darfoo/webapps/darfoobackend,
然后在3个tomcat的conf/Catalina/localhost下面加了配置文件darfoobackend.xml,用于找到该应用的外部路径。
（吉卉需要修改下之前的自动部署的脚本。把war包传到/usr/local/darfoo/webapps/下面，再解压缩为darfoobackend）
tomcat的http connector端口分别为8080 8081 8082


