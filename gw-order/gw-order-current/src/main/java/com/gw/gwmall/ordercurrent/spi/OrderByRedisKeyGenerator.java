package com.gw.gwmall.ordercurrent.spi;

import com.gw.gwmall.GwMallOrderCurrApplication;
import com.gw.gwmall.rediscomm.util.RedisOpsExtUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @description: 全局唯一主键生成器【分库分表专用】
 **/
public class OrderByRedisKeyGenerator implements ShardingKeyGenerator {

    @Getter
    private final String type = "CUSTOM";

    @Getter
    @Setter
    private Properties properties = new Properties();

    /**
     * 生成18位订单编号:8位日期+3位平台ID+7位以上自增id
     * 需在分表策略当中配置如下属性
     * spring.shardingsphere.sharding.tables.oms_order.key-generator.column=id
     * spring.shardingsphere.sharding.tables.oms_order.key-generator.type=CUSTOM
     * spring.shardingsphere.sharding.tables.oms_order.key-generator.props.worker.id=123
     * spring.shardingsphere.sharding.tables.oms_order.key-generator.props.redis.prefix=order:id:prefix:
     */
    @Override
    public Comparable<?> generateKey() {
        StringBuilder sb = new StringBuilder();
        RedisOpsExtUtil redisOpsUtil = GwMallOrderCurrApplication.getBean("redisOpsUtil");

        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String key = properties.getProperty("redis.prefix") + date;
        //增长值
        Long increment = null;
        if(redisOpsUtil.hasKey(key)){
            increment = redisOpsUtil.incr(key,1);
        }else{
            increment = redisOpsUtil.incr(key,1);
            redisOpsUtil.expire(key,24, TimeUnit.HOURS);
        }
        sb.append(date);
        sb.append(String.format("%03d", Integer.parseInt(properties.getProperty("worker.id"))));
        String incrementStr = increment.toString();
        if (incrementStr.length() <= 7) {
            sb.append(String.format("%07d", increment));
        } else {
            sb.append(incrementStr);
        }
        return Long.parseLong(sb.toString());
    }

}
