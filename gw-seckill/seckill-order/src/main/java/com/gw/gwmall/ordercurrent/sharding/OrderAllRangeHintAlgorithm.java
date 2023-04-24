package com.gw.gwmall.ordercurrent.sharding;


import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

import java.util.Collection;

/**
 * @description: 分库分表后的兜底路由策略，全库表路由。
 **/

public class OrderAllRangeHintAlgorithm implements HintShardingAlgorithm {
    @Override
    public Collection<String> doSharding(Collection availableTargetNames, HintShardingValue shardingValue) {
        return availableTargetNames;
    }
}
