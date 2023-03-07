package com.gw.gwmall.search.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author gw
 */
public interface EsClientSearchService {

    /**
     * 创建索引
     * @param index  索引的名称
     * @return
     */
    boolean createIndex(String index) throws IOException;

    /**
     * 获取索引(一个索引相当于一个数据库,你只能判断索引是否存在)
     * @param index 索引名
     * @return
     * @throws IOException
     */
    boolean isExist(String index) throws IOException;

    /**
     * 删除索引
     * @param index
     * @return
     */
    boolean delete(String index) throws IOException;

    /**
     * 添加文档
     * @param index 需要添加文档的索引名称
     * @param object 需要添加文档的内容(这里传入的是对象)
     * @return
     */
    boolean addDocument(String index, String id, Object object) throws IOException;

    /**
     * 获取文档,判断是否存在 /index/doc/id
     * @param index
     * @param id
     * @return
     */
    boolean isdocuexit(String index, String id) throws IOException;

    /**
     * 获取文档信息
     * @param index
     * @param id
     * @return 返回的是形如{"age":12,"name":"zhan"}的字符串
     * @throws IOException
     */
    String getDocument(String index, String id) throws IOException;

    /**
     * 更新文档信息
     * @throws IOException
     */
    boolean updateDocument(Object object, String index, String id) throws IOException;

    /**
     * 删除文档信息
     * @throws IOException
     */
    boolean deleteDocument(String index, String id) throws IOException;

    /**
     * 批量插入文档信息
     * @throws IOException
     */
    boolean addMoreDocument(List<Object> list, String index, String id) throws IOException;

    /**
     * 精确查询 (例如有张三,张四,张五,要查询张三的话就只能得到一个结果即张三)
     * @param index
     * @param content 需要查询的名称
     * @param size 每一页需要获取多少条数据
     * @param from 从第几个数据开始
     * @return 返回SearchHit[]的数据中的每一个元素都是一个对象 形如{{name=张三, age=12},{name=张三, age=13}}
     */
    List<Map<String,Object>> termQuery(String index, TreeMap<String, Object> content, int size, int from, boolean ishigh) throws IOException;

    /**
     * 使用分词器解析进行查询 (假如要查询的是张三,则会使用ik分词器进行解析,分成张和三),代码结构与上面的基本一样
     * @param index
     * @param content key：属性 value:查询的信息
     * @param size
     * @param from
     * @return 返回的结果就不仅仅包含张三,也包含了姓张的用户的信息了
     */
    List<Map<String,Object>> matchQuery(String index, TreeMap<String, Object> content, int size, int from, boolean ishigh) throws IOException;

    /**
     * 多条件match查询 并且是查的是并集(形如where name = 'zhan' and age = 12)
     * @param index
     * @param content key：属性 value:查询的信息
     * @param size
     * @param from
     * @throws IOException
     */
    List<Map<String,Object>> boolMustQuery(String index, TreeMap<String, Object> content, int size, int from, boolean ishigh) throws IOException;


}


