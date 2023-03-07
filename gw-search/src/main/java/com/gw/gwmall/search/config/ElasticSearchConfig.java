package com.gw.gwmall.search.config;

/**
 * @author guanwu
 */

import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("127.0.0.1", 9200, "http"));
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials("elastic", "123456"));
        builder.setHttpClientConfigCallback( clientBuilder -> clientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        return new RestHighLevelClient( builder);
    }
}
