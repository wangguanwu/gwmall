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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.util.List;

@Configuration
public class ElasticSearchConfig {

    @Value("${spring.data.elasticsearch.rest.uris:127.0.0.1:9300}")
    private List<String> uris;

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        HttpHost[] hosts = new HttpHost[uris.size()];
        for (int i = 0; i < uris.size(); i++) {
            String url = uris.get(i);
            String ip = url.split(":")[0];
            int port = Integer.parseInt(url.split(":")[1]);
            hosts[i] = new HttpHost(ip, port, "http");
        }
        RestClientBuilder builder = RestClient.builder(
                hosts);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY,
//                new UsernamePasswordCredentials("elastic", "123456"));
        builder.setHttpClientConfigCallback( clientBuilder -> clientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        return new RestHighLevelClient( builder);
    }

    @Bean
    ElasticsearchRestTemplate elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(restHighLevelClient());
    }
}
