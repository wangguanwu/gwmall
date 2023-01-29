package com.gw.gwmall.mall.service;

import java.util.UUID;

public class UUIDService {

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            String rawUUID = UUID.randomUUID().toString();
            System.out.println(rawUUID);
            //去除"-"
            String uuid = rawUUID.replaceAll("-", "");
            System.out.println(uuid);
        }
    }

}
