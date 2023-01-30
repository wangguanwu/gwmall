package com.gw.gwmall.constant;

/**
* @desc: 类的描述:会员服务常量类
*/
public class MDA {

    /**
     * 会员服务第三方客户端(这个客户端在认证服务器配置好的oauth_client_details)
     */
    public static final String CLIENT_ID = "member-service";

    /**
     * 会员服务第三方客户端密码(这个客户端在认证服务器配置好的oauth_client_details)
     */
    public static final String CLIENT_SECRET = "123456";

    /**
     * 认证服务器登陆地址
     */
    public static final String OAUTH_LOGIN_URL = "http://gw-authcenter/oauth/token";


    public static final String USER_NAME = "username";

    public static final String PASS = "password";

    public static final String GRANT_TYPE = "grant_type";

    public static final String SCOPE = "scope";

    public static final String SCOPE_AUTH = "read";
}
