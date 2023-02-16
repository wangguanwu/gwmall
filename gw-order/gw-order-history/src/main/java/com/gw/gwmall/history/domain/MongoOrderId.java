package com.gw.gwmall.history.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("table-order-id")
@Data
public class MongoOrderId {

    private String tableName;

    private Long maxOrderId;

}
