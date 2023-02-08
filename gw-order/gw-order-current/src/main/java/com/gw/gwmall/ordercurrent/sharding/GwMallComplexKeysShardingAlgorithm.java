package com.gw.gwmall.ordercurrent.sharding;

import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author guanwu
 **/

@Slf4j
public class GwMallComplexKeysShardingAlgorithm implements ComplexKeysShardingAlgorithm<String> {

    /* 订单编号列名 */
    private static final String COLUMN_ORDER_ID_KEY = "id";
    /* 客户id列名*/
    private static final String COLUMN_CUSTOMER_ID_KEY = "member_id";

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<String> shardingValue) {

        //处理 =、in 查询
        if (shardingValue.getColumnNameAndShardingValuesMap() != null ) {
            Collection<String> orderList = shardingValue.getColumnNameAndShardingValuesMap().get(COLUMN_ORDER_ID_KEY);

            Collection<String> userId = shardingValue.getColumnNameAndShardingValuesMap().get(COLUMN_CUSTOMER_ID_KEY);

            Collection<String> idList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty((orderList))) {
                idList.addAll(batchConvertIdToString(orderList));
            }
            if (CollectionUtils.isNotEmpty(userId)) {
                idList.addAll(batchConvertIdToString(userId));
            }

            return idList.stream()
                    .map(e -> e.substring(e.length() - 2))
                    .distinct()
                    .map(Integer::parseInt)
                    .map(idSuffix -> idSuffix % availableTargetNames.size())
                    .map(String::valueOf)
                     //映射为表名
                    .map(suffix -> availableTargetNames.stream().filter(t -> t.endsWith(suffix)).findFirst().orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        //处理between之类的查询,只能支持orderId字段，而且只能支持部分表
        Map<String, Range<String>> columnNameAndRangeValuesMap = shardingValue.getColumnNameAndRangeValuesMap();
        if (columnNameAndRangeValuesMap == null || columnNameAndRangeValuesMap.isEmpty()) {
            return availableTargetNames;
        }

        Range<String> range = columnNameAndRangeValuesMap.get(COLUMN_ORDER_ID_KEY);

        log.info("[OmsOrderComplexShardingAlgorithm] 分片值[{}]", shardingValue);
        if (null == range) {
            return availableTargetNames;
        }

        if (range.lowerEndpoint() == null || range.upperEndpoint() == null) {
            return availableTargetNames;
        }
        Long lower = Long.valueOf(range.lowerEndpoint());
        Long upper = Long.valueOf(range.upperEndpoint());
        if (upper - lower > availableTargetNames.size()) {
            return new ArrayList<>(availableTargetNames);
        } else {
            Collection<String> res = new HashSet<>();
            String lowStr = lower.toString();
            String upperStr = upper.toString();
            String lowSuffix = lowStr.substring(lowStr.length() - 2);
            String upperSuffix = upperStr.substring(upperStr.length() - 2);
            int l = Integer.parseInt(lowSuffix) % availableTargetNames.size();
            int h = Integer.parseInt(upperSuffix) % availableTargetNames.size();
            for(int i = l; i <=h; i++) {
                res.add("oms_order_" + i);
            }
            return new ArrayList<>(res);
        }
    }


    private Collection<String> batchConvertIdToString (Collection<?> idList) {
        if (CollectionUtils.isNotEmpty(idList)) {
            return Collections.emptyList();
        }
        return idList.stream().map(this::convertIdToString)
                .collect(Collectors.toList());
    }


    private String convertIdToString(Object id) {
        String idStr = String.valueOf(id);
        if (idStr.length() < 2) {
            idStr = "0" + idStr;
        }
        return idStr;
    }
}
