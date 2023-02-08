package com.gw.gwmall.ordercurrent.component;//package com.tuling.tulingmall.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description: 支付宝二维码存储与访问路径设置
 **/
@Component
@ConfigurationProperties(prefix = "trade.zhifu.qrcode")
@Data
public class TradePayProp {
    /**
     * 支付宝qrcode存储路径
     */
    private String aliPayPath;
    /**
     * 微信支付qrcode存储路径
     */
    private String weChatPath;
    /**
     * qrcode存储文件路径设置
     */
    private String storePath;
    /**
     * http访问路径设置
     */
    private String httpBasePath;
    /**
     * 支付成功回调页面
     */
    private String paySuccessCallBack;
}
