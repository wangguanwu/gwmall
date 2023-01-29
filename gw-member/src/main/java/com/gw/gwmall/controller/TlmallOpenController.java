package com.gw.gwmall.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.gw.gwmall.model.UmsMember;
import com.gw.gwmall.service.UmsMemberService;
import com.tuling.tulingmall.model.UmsMember;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @desc 响应电商服务开放平台的请求。
 */
@RestController
public class TlmallOpenController {

    @Resource
    private UmsMemberService umsMemberService;

    @RequestMapping(value = "/open/service",method = {RequestMethod.POST})
    public Object loadUserByUsername(@RequestBody String requestJson){
        Map<String,Object> response = new HashMap<>();
        try {
            JSONObject requestObj = JSONObject.parseObject(requestJson);
            String username = requestObj.getString("username");

            response.put("username",username);
            UmsMember umsMember = umsMemberService.getByUsername(username);

            if(null != umsMember){
                response.put("umsMember",umsMember);
            }else{
                response.put("umsMember","查无此人");
            }
        } catch(JSONException e){
            response.put("error","请求报文格式错误");
        }catch (Exception e) {
            response.put("error","查询出错，请检查服务或重新查询");
        }
        return response;
    }
}
