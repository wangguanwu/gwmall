package com.gw.gwmall.search.controller;

/**
* @author guanwu
*/


import com.gw.gwmall.search.domain.User;
import com.gw.gwmall.search.service.EsClientSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@Slf4j
public class EsClientTestController {

    @Autowired
    private EsClientSearchService esClientSearchService;

    //创建索引库
    @GetMapping("/createIndex")
    public boolean createIndex()  throws IOException{
        log.info("创建索引====");
        return esClientSearchService.createIndex("es_test_index");
    }

    //判断索引库是否存在
    @GetMapping("/isExistIndex")
    public boolean isExitIndex() throws IOException {
        return esClientSearchService.isExist("es_test_index");
    }

    //删除索引库
    @GetMapping("/deleteIndex")
    public boolean deleteIndex() throws IOException {
        return esClientSearchService.delete("es_test_index");
    }

    //添加文档
    @GetMapping("/addDocument")
    public boolean addDocument() throws IOException {
        return  esClientSearchService.addDocument("es_test_index", "1", new User("jack",20,"杰克船长"));
    }

     //判断文档是否存在
    @GetMapping("/isDocumentExit")
    public boolean isDocumentExit(String index, String id) throws IOException {
        return esClientSearchService.isdocuexit("es_test_index", "1");
    }

    //根据id获取文档
    @GetMapping("/getDocument")
    public String getDoucumment(String index, String id) throws IOException {
        return esClientSearchService.getDocument("es_test_index", "1");
    }

    //更新文档信息
    @GetMapping("/updateDocument")
    public boolean updateDocument(Object object, String index, String id) throws IOException {
           return esClientSearchService.updateDocument(new User("白起",18,"白起老师"),"es_test_index", "1");
    }

    //删除文档
    @GetMapping("/deleteDocument")
    public boolean deleteDocument(String index, String id) throws IOException{
        return esClientSearchService.deleteDocument("es_test_index", "1");
    }

    //批量添加
    @GetMapping("/addMoreDocument")
    public boolean addMoreDocument() throws IOException{

        User u1 = new User("jack",20,"杰克船长");
        User u2 = new User("rose",21,"肉丝船长");
        User u3 = new User("tom",22,"汤姆船长");
        List<Object> list = new ArrayList<>();
        list.add(u1);
        list.add(u2);
        list.add(u3);
        return esClientSearchService.addMoreDocument(list,"es_test_index", "1");
    }

    @GetMapping("/termQuery")
    public List<Map<String,Object>> termQuery() throws IOException {

        TreeMap<String, Object> map = new TreeMap<>();
        map.put("name", "jack");
       return esClientSearchService.termQuery("es_test_index",map , 2, 0, true);
    }

    @GetMapping("/matchQuery")
    public List<Map<String,Object>> matchQuery() throws IOException {
        TreeMap<String, Object> map = new TreeMap<>();
        map.put("name", "rose");

        return esClientSearchService.matchQuery("es_test_index",map , 2, 0, true);
    }

    @GetMapping("/boolMustQuery")
    public List<Map<String,Object>> boolmustQuery() throws IOException {

        TreeMap<String, Object> map = new TreeMap<>();
        map.put("name", "jack");
        map.put("age",20 );
        return esClientSearchService.boolMustQuery("es_test_index",map , 2, 0,true);
    }
}
