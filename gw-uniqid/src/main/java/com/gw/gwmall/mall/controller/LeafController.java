package com.gw.gwmall.mall.controller;

import com.gw.gwmall.leafcore.common.Result;
import com.gw.gwmall.leafcore.common.Status;
import com.gw.gwmall.mall.exception.LeafServerException;
import com.gw.gwmall.mall.exception.NoKeyException;
import com.gw.gwmall.mall.service.SegmentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LeafController {
    private Logger logger = LoggerFactory.getLogger(LeafController.class);

    @Autowired
    private SegmentService segmentService;

    @RequestMapping(value = "/api/segment/get/{key}")
    public String getSegmentId(@PathVariable("key") String key) {
        return get(key, segmentService.getId(key));
    }

    @RequestMapping(value = "/api/segment/getlist/{key}")
    public List<String> getSegmentIdList(@PathVariable("key") String key,@RequestParam int keyNumber) {
        if (keyNumber == 0 || keyNumber > 5000) keyNumber = 5000;
        return getList(key, segmentService.getIds(key,keyNumber));
    }

    private String get(@PathVariable("key") String key, Result id) {
        Result result;
        if (key == null || key.isEmpty()) {
            throw new NoKeyException();
        }
        result = id;
        if (result.getStatus().equals(Status.EXCEPTION)) {
            throw new LeafServerException(result.toString());
        }
        return String.valueOf(result.getId());
    }

    private List<String> getList(@PathVariable("key") String key, List<Result> idList) {
        if (key == null || key.isEmpty()) {
            throw new NoKeyException();
        }
        if(idList.isEmpty()){
            throw new LeafServerException("获得id列表失败！");
        }
        List<String> result = new ArrayList<>();
        for(Result id : idList){
            if (id.getStatus().equals(Status.EXCEPTION)) {
                logger.error("获得id异常：",new LeafServerException(id.toString()));
            }else{
                result.add(String.valueOf(id.getId()));
            }
        }
        return result;
    }
}
