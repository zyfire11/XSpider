package com.zy.config;

import wuhao.tools.reader.Property;
import wuhao.tools.reader.PropertyFile;

/**
 * Created by zhouyi on 2016/10/29.
 */
@PropertyFile(value = "proxyConfig",comment = "代理参数")
public class ProxyConfig {
    @Property(comment = "代理域名")
    public static       String proxyHost = "proxy.abuyun.com";

    @Property(comment = "代理端口")
    public static       int    proxyPort = 9010;

    @Property(comment = "代理用户名")
    public static       String proxyUser = "H0456VRN655135BP";

    @Property(comment = "代理密码")
    public static       String proxyPassword = "B5DA4B79346B499C";

    @Property(comment = "预警人员号码")
    public static       String warningPhone = "15261492378";

}
