package com.gw.mall.controller;

import com.gw.leafcore.segment.SegmentIDGenImpl;
import com.gw.leafcore.segment.model.LeafAlloc;
import com.gw.leafcore.segment.model.SegmentBuffer;
import com.gw.mall.model.SegmentBufferView;
import com.gw.mall.service.SegmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LeafMonitorController {
    private Logger logger = LoggerFactory.getLogger(LeafMonitorController.class);

    @Autowired
    private SegmentService segmentService;

    @RequestMapping(value = "cache")
    public Map<String, SegmentBufferView> getCache() {
        Map<String, SegmentBufferView> data = new HashMap<>();
        SegmentIDGenImpl segmentIDGen = segmentService.getIdGen();
        if (segmentIDGen == null) {
            throw new IllegalArgumentException("You should config leaf.segment.enable=true first");
        }
        Map<String, SegmentBuffer> cache = segmentIDGen.getCache();
        for (Map.Entry<String, SegmentBuffer> entry : cache.entrySet()) {
            SegmentBufferView sv = new SegmentBufferView();
            SegmentBuffer buffer = entry.getValue();
            sv.setInitOk(buffer.isInitOk());
            sv.setKey(buffer.getKey());
            sv.setPos(buffer.getCurrentPos());
            sv.setNextReady(buffer.isNextReady());
            sv.setMax0(buffer.getSegments()[0].getMax());
            sv.setValue0(buffer.getSegments()[0].getValue().get());
            sv.setStep0(buffer.getSegments()[0].getStep());

            sv.setMax1(buffer.getSegments()[1].getMax());
            sv.setValue1(buffer.getSegments()[1].getValue().get());
            sv.setStep1(buffer.getSegments()[1].getStep());

            data.put(entry.getKey(), sv);

        }
        logger.info("Cache info {}", data);
        //model.addAttribute("data", data);
        return data;
    }

    @RequestMapping(value = "db")
    public List<LeafAlloc> getDb() {
        SegmentIDGenImpl segmentIDGen = segmentService.getIdGen();
        if (segmentIDGen == null) {
            throw new IllegalArgumentException("You should config leaf.segment.enable=true first");
        }
        List<LeafAlloc> items = segmentIDGen.getAllLeafAllocs();
        logger.info("DB info {}", items);
        return items;
    }
}
