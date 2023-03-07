package com.gw.gwmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.gw.gwmall.common.util.JacksonUtils;
import com.gw.gwmall.search.service.EsClientSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author gw
 */
@Service(value="esSearchClientService")
@Slf4j
public class EsClientClientSearchServiceImpl implements EsClientSearchService {

    @Qualifier("restHighLevelClient")
    @Autowired
    private RestHighLevelClient client;

    @Override
    public boolean createIndex(String index) throws IOException {
        //1.创建索引请求
        CreateIndexRequest request = new CreateIndexRequest(index);
        //2.客户端执行请求IndicesClient,请求后获得相应
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    @Override
    public boolean isExist(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    @Override
    public boolean delete(String index) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        return delete.isAcknowledged();
    }

    @Override
    public boolean addDocument(String index, String id, Object object) throws IOException {
        IndexRequest request = new IndexRequest(index);
        //规则 一般的文档形如 put /index/_doc/1
        request.id(id);//如果不设置id的话会自动分配id
        request.timeout("1s");//设置超时时间
        log.info("JSON.toJSONString(object):"+JSON.toJSONString(object));
        //将我们的数据放入请求Json中
        request.source(JSON.toJSONString(object), XContentType.JSON);
        //客户端发送请求,获取相应的结果
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response.getShardInfo().getSuccessful() > 0;
    }

    @Override
    public boolean isdocuexit(String index, String id) throws IOException {
        GetRequest getRequest = new GetRequest(index,id);
        //不获取返回的_source上下文
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        return client.exists(getRequest, RequestOptions.DEFAULT);
    }

    @Override
    public String getDocument(String index, String id) throws IOException {
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        return response.getSourceAsString();
    }

    @Override
    public boolean updateDocument(Object object, String index, String id) throws IOException {
        UpdateRequest request = new UpdateRequest(index, id);
        request.timeout("1s");
        request.doc(JSON.toJSONString(object), XContentType.JSON);
        UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
        return update.getShardInfo().getSuccessful() > 0;
    }

    @Override
    public boolean deleteDocument(String index, String id) throws IOException{
        DeleteRequest request = new DeleteRequest(index,id);
        request.timeout("1s");
        DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
        return deleteResponse.getShardInfo().getSuccessful() > 0;
    }

    @Override
    public boolean addMoreDocument(List<Object> list, String index, String id) throws IOException{
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("1s");
        //批量处理请求
        for (Object o : list) {
            IndexRequest source = new IndexRequest(index)
                    .source(JSON.toJSONString(o), XContentType.JSON);
            bulkRequest.add(source);

        }
        BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        //是否失败 false-没有失败
        BulkItemResponse[] items = responses.getItems();
        for (BulkItemResponse item : items) {
            log.info("上传成功：item => {}", JacksonUtils.toJson(item));
        }
        return !responses.hasFailures();
    }

    @Override
    public List<Map<String,Object>> termQuery(String index, TreeMap<String, Object> content, int size, int from, boolean ishigh) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        //构建查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        log.info(content.firstKey());
        //查询条件
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(content.firstKey(),content.get(content.firstKey()));
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


        //获取多少条数据
        sourceBuilder.size(size);
        //从第几行开始
        sourceBuilder.from(from);
        //是否要将查询的结果中将搜索的关键词高亮
        if (ishigh){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            //设置高亮的属性
            highlightBuilder.field(content.firstKey());
            //也可以自定义高亮的样式,这里我使用的是默认的方式
            sourceBuilder.highlighter(highlightBuilder);
        }
        log.info("term query dsl: {}", sourceBuilder);
        //将查询条件放入需要查询中
        searchRequest.source(sourceBuilder);

        //获取相应的数据
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> result = parseResultFromResponse(searchResponse, content, ishigh);
        log.info("result:{}",result);
        log.info("result: {}",result);
        return result;
    }

    @Override
    public List<Map<String,Object>> matchQuery(String index, TreeMap<String, Object> content, int size, int from, boolean ishigh) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        //构建查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //查询条件
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(content.firstKey(),content.get(content.firstKey()));

        sourceBuilder.query(matchQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


        //获取多少条数据
        sourceBuilder.size(size);
        //从第几行开始
        sourceBuilder.from(from);
        //是否要高亮
        if (ishigh){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            //设置高亮的属性
            highlightBuilder.field(content.firstKey());
            //也可以自定义高亮的样式,这里我使用的是默认的方式
            sourceBuilder.highlighter(highlightBuilder);
        }

        log.info("match query dsl: {}",sourceBuilder);

        //将查询条件放入需要查询中
        searchRequest.source(sourceBuilder);
        //获取相应的数据
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> result = parseResultFromResponse(searchResponse, content, ishigh);
        log.info("result:{}",result);
        return result;
    }

    @Override
    public List<Map<String,Object>> boolMustQuery(String index, TreeMap<String, Object> content, int size, int from, boolean ishigh) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        //构建查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        Set<String> keys = content.keySet();
        for (String key : keys) {
            //将要查询的条件加入
            boolQueryBuilder.must(QueryBuilders.termQuery(key,content.get(key)));
        }
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //获取多少条数据
        sourceBuilder.size(size);
        //从第几行开始
        sourceBuilder.from(from);

        log.info("bool must query:{}", sourceBuilder);
        //将查询条件放入需要查询中
        searchRequest.source(sourceBuilder);
        //获取相应的数据
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        log.info("search response:{}", searchResponse);
        List<Map<String, Object>> result = parseResultFromResponse(searchResponse, content, ishigh);
        log.info("result: {}", result);
        return result;
    }

    private static List<Map<String, Object>> parseResultFromResponse(SearchResponse response,
                                                              TreeMap<String, Object> content,
                                                              boolean isHigh) {
        SearchHit[] hits = response.getHits().getHits();
        List<Map<String,Object>> result = new ArrayList<>();
        for (SearchHit searchHit: hits) {
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            //获取高亮的信息
            //查询的元素数据(没有高亮)
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            log.info("sourceAsMap:{}", JacksonUtils.toJson(sourceAsMap));
            HighlightField property = highlightFields.get(content.firstKey());
            if (isHigh) {
                if (property != null) {
                    Text[] fragments = property.fragments();
                    StringBuilder n_title = new StringBuilder();
                    for (Text text:fragments) {
                        n_title.append(text);
                    }
                    sourceAsMap.put(content.firstKey(), n_title.toString());
                }
            }
            result.add(sourceAsMap);
        }
        return result;
    }
}



