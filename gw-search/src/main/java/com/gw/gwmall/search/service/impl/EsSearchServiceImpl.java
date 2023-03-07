package com.gw.gwmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.gw.gwmall.search.service.EsSearchService;
import com.gw.gwmall.search.domain.EsProduct;
import com.gw.gwmall.search.utils.SearchConstant;
import com.gw.gwmall.search.vo.ESRequestParam;
import com.gw.gwmall.search.vo.ESResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gw
 */
@Service(value="esSearchService")
@Slf4j
public class EsSearchServiceImpl implements EsSearchService {

    @Qualifier("restHighLevelClient")
    @Autowired
    private RestHighLevelClient client;


    /**************************商城搜索*****************************/
    @Override
    public ESResponseResult search(ESRequestParam param) {

        try {
            //1、构建检索对象-封装请求相关参数信息
            SearchRequest searchRequest = startBuildRequestParam(param);

            //2、进行检索操作
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println("response:"+response);
            //3、分析响应数据，封装成指定的格式
            ESResponseResult responseResult = startBuildResponseResult(response, param);
            return responseResult;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 封装请求参数信息
     * 关键字查询、根据属性、分类、品牌、价格区间、是否有库存等进行过滤、分页、高亮、以及聚合统计品牌分类属性
     *
     */
    private SearchRequest startBuildRequestParam(ESRequestParam param) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        /**
         * 关键字查询、根据属性、分类、品牌、价格区间、是否有库存等进行过滤、分页、高亮、以及聚合统计品牌分类属性
         */
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //1、查询关键字
        if (!StringUtils.isEmpty(param.getKeyword())) {
            //单字段查询
            //boolQueryBuilder.must(QueryBuilders.matchQuery("name", param.getKeyword()));
            //多字段查询
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(param.getKeyword(),"name","keywords","subTitle"));
        }
        //2、根据类目ID进行过滤
        if (null != param.getCategoryId()) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryId", param.getCategoryId()));
        }

        //3、根据品牌ID进行过滤
        if (null != param.getBrandId() && param.getBrandId().size() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }

        //4、根据属性进行相关过滤
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {

            param.getAttrs().forEach(item -> {
                //attrs=1_白色&2_4核
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

                //attrs=1_64G
                String[] s = item.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");//这个属性检索用的值
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));

                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            });

        }

        //5、是否有库存
        if (null != param.getHasStock()) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }


        //6、根据价格过滤
        if (!StringUtils.isEmpty(param.getPrice())) {
            //价格的输入形式为：10_100（起始价格和最终价格）或_100（不指定起始价格）或10_（不限制最终价格）
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
            String[] price = param.getPrice().split("_");
            if (price.length == 2) {
                    //price: _5000
                    if (param.getPrice().startsWith("_")) {
                        rangeQueryBuilder.lte(price[1]);
                    }
                else{
                        //price: 1_5000
                        rangeQueryBuilder.gte(price[0]).lte(price[1]);
                }

            } else if (price.length == 1) {
                 //price: 1_
                if (param.getPrice().endsWith("_")) {
                    rangeQueryBuilder.gte(price[0]);
                }
                //price: _5000
                if (param.getPrice().startsWith("_")) {
                    rangeQueryBuilder.lte(price[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        //封装所有查询条件
        searchSourceBuilder.query(boolQueryBuilder);


        /**
         * 实现排序、高亮、分页操作
         */

        //排序
        //页面传入的参数值形式 sort=price_asc/desc
        if (!StringUtils.isEmpty(param.getSort())) {
            String sort = param.getSort();
            String[] sortFileds = sort.split("_");

            if(!StringUtils.isEmpty(sortFileds[0])){
                SortOrder sortOrder = "asc".equalsIgnoreCase(sortFileds[1]) ? SortOrder.ASC : SortOrder.DESC;
                searchSourceBuilder.sort(sortFileds[0], sortOrder);
            }
        }

        //分页查询
        searchSourceBuilder.from((param.getPageNum() - 1) * SearchConstant.PAGE_SIZE);
        searchSourceBuilder.size(SearchConstant.PAGE_SIZE);

        //高亮显示
        if (!StringUtils.isEmpty(param.getKeyword())) {

            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("name");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");

            searchSourceBuilder.highlighter(highlightBuilder);
        }


        /**
         * 对品牌、分类信息、属性信息进行聚合分析
         */
        //1. 按照品牌进行聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);


        //1.1 品牌的子聚合-品牌名聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        //1.2 品牌的子聚合-品牌图片聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));

        searchSourceBuilder.aggregation(brand_agg);

        //2. 按照分类信息进行聚合
        TermsAggregationBuilder category_agg = AggregationBuilders.terms("category_agg");
        category_agg.field("categoryId").size(50);

        category_agg.subAggregation(AggregationBuilders.terms("category_name_agg").field("categoryName").size(1));

        searchSourceBuilder.aggregation(category_agg);

        //2. 按照属性信息进行聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        //2.1 按照属性ID进行聚合
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attr_agg.subAggregation(attr_id_agg);
        //2.1.1 在每个属性ID下，按照属性名进行聚合
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        //2.1.1 在每个属性ID下，按照属性值进行聚合
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        searchSourceBuilder.aggregation(attr_agg);

        System.out.println("构建的DSL语句 {}:"+ searchSourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest(new String[]{SearchConstant.INDEX_NAME}, searchSourceBuilder);

        return searchRequest;
    }



    /**
     * 封装查询到的结果信息
     * 关键字查询、根据属性、分类、品牌、价格区间、是否有库存等进行过滤、分页、高亮、以及聚合统计品牌分类属性
     */
    private ESResponseResult startBuildResponseResult(SearchResponse response, ESRequestParam param) {

       ESResponseResult result = new ESResponseResult();

        //1、获取查询到的商品信息
        SearchHits hits = response.getHits();

        List<EsProduct> esModels = new ArrayList<>();
        //2、遍历所有商品信息
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                EsProduct esModel = JSON.parseObject(sourceAsString, EsProduct.class);

                    //2.1 判断是否按关键字检索，若是就显示高亮，否则不显示
                if (!StringUtils.isEmpty(param.getKeyword())) {
                    //2.2 拿到高亮信息显示标题
                    HighlightField name = hit.getHighlightFields().get("name");
                    //2.3 判断name中是否含有查询的关键字(因为是多字段查询，因此可能不包含指定的关键字，假设不包含则显示原始name字段的信息)
                    String nameValue = name!=null ? name.getFragments()[0].string() : esModel.getName();
                    esModel.setName(nameValue);
                }
                esModels.add(esModel);
            }
        }
        result.setProducts(esModels);

        //3、当前商品涉及到的所有品牌信息，小米手机和小米电脑都属于小米品牌，过滤重复品牌信息
        List<ESResponseResult.BrandVo> brandVos = new ArrayList<>();
        //获取到品牌的聚合
        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            ESResponseResult.BrandVo brandVo = new ESResponseResult.BrandVo();

            //获取品牌的id
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);

            //获取品牌的名字
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);

            //获取品牌的LOGO
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            System.out.println("brandId:"+brandId+"brandName:"+brandName+"brandImg");
            brandVos.add(brandVo);
        }
        System.out.println("brandVos.size:"+brandVos.size());
        result.setBrands(brandVos);


        //4、当前商品相关的所有类目信息
        //获取到分类的聚合
        List<ESResponseResult.categoryVo> categoryVos = new ArrayList<>();

        ParsedLongTerms categoryAgg = response.getAggregations().get("category_agg");


        for (Terms.Bucket bucket : categoryAgg.getBuckets()) {
            ESResponseResult.categoryVo categoryVo = new ESResponseResult.categoryVo();
            //获取分类id
            String keyAsString = bucket.getKeyAsString();
            categoryVo.setCategoryId(Long.parseLong(keyAsString));

            //获取分类名
            ParsedStringTerms categoryNameAgg = bucket.getAggregations().get("category_name_agg");
            String categoryName = categoryNameAgg.getBuckets().get(0).getKeyAsString();
            categoryVo.setCategoryName(categoryName);
            categoryVos.add(categoryVo);
        }

        result.setCategorys(categoryVos);



        //5、获取商品相关的所有属性信息
        List<ESResponseResult.AttrVo> attrVos = new ArrayList<>();
        //获取属性信息的聚合
        ParsedNested attrsAgg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            ESResponseResult.AttrVo attrVo = new ESResponseResult.AttrVo();
            //获取属性ID值
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            //获取属性的名字
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);

            //获取属性的值
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            System.out.println("===1==="+attrValueAgg.getBuckets());

            for (Terms.Bucket b : attrValueAgg.getBuckets()) {
                 String bb = b.getKeyAsString();
                System.out.println("bb:"+bb);
            }

            List<String> attrValues = attrValueAgg.getBuckets().stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);
            System.out.println("===2==="+attrValues);
            attrVos.add(attrVo);
        }

        result.setAttrs(attrVos);

        //6、进行分页操作
        result.setPageNum(param.getPageNum());
        //获取总记录数
        long total = hits.getTotalHits().value;
        result.setTotal(total);

        //计算总页码
        int totalPages = (int) total % SearchConstant.PAGE_SIZE == 0 ?
                (int) total / SearchConstant.PAGE_SIZE : ((int) total / SearchConstant.PAGE_SIZE + 1);
        result.setTotalPages(totalPages);

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        return result;
        }

    }



