package com.gw.gwmall.history.config;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableTransactionManagement
public class MongoDbConfig {

    @Bean
    MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory factory){
        //事务操作配置

        TransactionOptions txnOptions = TransactionOptions.builder()
                .readPreference(ReadPreference.primary())
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.MAJORITY
                        //订单日志需要落盘
                        .withJournal(true)
                        .withWTimeout(10, TimeUnit.SECONDS))
                .build();
        return new MongoTransactionManager(factory,txnOptions);
    }

}
