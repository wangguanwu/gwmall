package com.gw.gwmall.ordercurrent.controller;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.ordercurrent.domain.RocksDBVo;
import com.gw.gwmall.ordercurrent.util.RocksDBUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.rocksdb.RocksDBException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "RocksDB")
@RestController
@RequestMapping(value = "/rocksdb")
public class RocksDBController {

    @ApiOperation("列族，创建（如果不存在）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cfName", value = "列族", required = true),
    })
    @PostMapping("/cf")
    public CommonResult<String> cfAdd(String cfName) throws RocksDBException {
        RocksDBUtil.cfAddIfNotExist(cfName);
        return CommonResult.success(cfName);
    }

    @ApiOperation("列族，删除（如果存在）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cfName", value = "列族", required = true),
    })
    @DeleteMapping("/cf")
    public CommonResult<String> cfDelete(String cfName) throws RocksDBException {
        RocksDBUtil.cfDeleteIfExist(cfName);
        return CommonResult.success(cfName);
    }

    @ApiOperation("列族名（查询所有）")
    @GetMapping("/cf-all")
    public CommonResult<Set<String>> cfAll() {
        Set<String> cfNames = RocksDBUtil.columnFamilyHandleMap.keySet();
        CommonResult<Set<String>> response = CommonResult.success(cfNames);
        return response;
    }

    @ApiOperation("增")
    @PostMapping("/put")
    public CommonResult<RocksDBVo> put(@RequestBody RocksDBVo rocksDBVo) throws RocksDBException {
        RocksDBUtil.put(rocksDBVo.getCfName(), rocksDBVo.getKey(), rocksDBVo.getValue());
        return CommonResult.success(rocksDBVo);
    }

    @ApiOperation("增（批量）")
    @PostMapping("/batch-put")
    public CommonResult<List<RocksDBVo>> batchPut(@RequestBody List<RocksDBVo> rocksDBVos) throws RocksDBException {
        Map<String, String> map = new HashMap<>();
        for (RocksDBVo rocksDBVo : rocksDBVos) {
            map.put(rocksDBVo.getKey(), rocksDBVo.getValue());
        }
        RocksDBUtil.batchPut(rocksDBVos.get(0).getCfName(), map);
        CommonResult<List<RocksDBVo>> response = CommonResult.success(rocksDBVos);
        return response;
    }

    @ApiOperation("删")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cfName", value = "列族", required = true),
            @ApiImplicitParam(name = "key", value = "键", required = true),
    })
    @DeleteMapping("/delete")
    public CommonResult<RocksDBVo> delete(String cfName, String key) throws RocksDBException {
        String value = RocksDBUtil.get(cfName, key);
        RocksDBUtil.delete(cfName, key);
        RocksDBVo rocksDBVo = new RocksDBVo();
        rocksDBVo.setCfName(cfName);
        rocksDBVo.setKey(key);
        rocksDBVo.setValue(value);
        return CommonResult.success(rocksDBVo);
    }

    @ApiOperation("查")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cfName", value = "列族", required = true),
            @ApiImplicitParam(name = "key", value = "键", required = true),
    })
    @GetMapping("/get")
    public CommonResult<RocksDBVo> get(String cfName, String key) throws RocksDBException {
        String value = RocksDBUtil.get(cfName, key);
        RocksDBVo rocksDBVo = new RocksDBVo();
        rocksDBVo.setCfName(cfName);
        rocksDBVo.setKey(key);
        rocksDBVo.setValue(value);
        return CommonResult.success(rocksDBVo);
    }

    @ApiOperation("查（多个键值对）")
    @PostMapping("/multiGetAsList")
    public CommonResult<List<RocksDBVo>> multiGetAsList(@RequestBody List<RocksDBVo> rocksDBVos) throws RocksDBException {
        List<RocksDBVo> list = new ArrayList<>();
        String cfName = rocksDBVos.get(0).getCfName();
        List<String> keys = new ArrayList<>(rocksDBVos.size());
        for (RocksDBVo rocksDBVo : rocksDBVos) {
            keys.add(rocksDBVo.getKey());
        }
        Map<String, String> map = RocksDBUtil.multiGetAsMap(cfName, keys);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            RocksDBVo rocksDBVo = new RocksDBVo();
            rocksDBVo.setCfName(cfName);
            rocksDBVo.setKey(entry.getKey());
            rocksDBVo.setValue(entry.getValue());
            list.add(rocksDBVo);
        }
        CommonResult<List<RocksDBVo>> response = CommonResult.success(list);
        return response;
    }

    @ApiOperation("查所有键值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cfName", value = "列族", required = true),
    })
    @GetMapping("/get-all")
    public CommonResult<List<RocksDBVo>> getAll(String cfName) throws RocksDBException {
        List<RocksDBVo> rocksDBVos = new ArrayList<>();
        Map<String, String> all = RocksDBUtil.getAll(cfName);
        for (Map.Entry<String, String> entry : all.entrySet()) {
            RocksDBVo rocksDBVo = new RocksDBVo();
            rocksDBVo.setCfName(cfName);
            rocksDBVo.setKey(entry.getKey());
            rocksDBVo.setValue(entry.getValue());
            rocksDBVos.add(rocksDBVo);
        }
        CommonResult<List<RocksDBVo>> response = CommonResult.success(rocksDBVos);
        return response;
    }

    @ApiOperation("分片查（键）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cfName", value = "列族", required = true),
    })
    @GetMapping("/get-keys")
    public CommonResult<List<String>> getKeysFrom(String cfName) throws RocksDBException {
        List<String> data = new ArrayList<>();
        List<String> keys;
        String lastKey = null;
        while (true) {
            keys = RocksDBUtil.getKeysFrom(cfName, lastKey);
            if (keys.isEmpty()) {
                break;
            }
            lastKey = keys.get(keys.size() - 1);
            data.addAll(keys);
            keys.clear();
        }
        CommonResult<List<String>> response = CommonResult.success(data);
        return response;
    }

    @ApiOperation("查（所有键）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cfName", value = "列族", required = true),
    })
    @GetMapping("/get-all-key")
    public CommonResult<List<String>> getAllKey(String cfName) throws RocksDBException {
        List<String> allKey = RocksDBUtil.getAllKey(cfName);
        CommonResult<List<String>> response = CommonResult.success(allKey);
        return response;
    }

    @ApiOperation("查总条数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cfName", value = "列族", required = true),
    })
    @GetMapping("/get-count")
    public CommonResult<Integer> getCount(String cfName) throws RocksDBException {
        int count = RocksDBUtil.getCount(cfName);
        return CommonResult.success(count);
    }

}
