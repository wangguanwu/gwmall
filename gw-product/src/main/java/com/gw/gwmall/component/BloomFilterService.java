package com.gw.gwmall.component;

/**
 * @author guanwu
 * @created on 2023-04-21 19:54:04
 **/
public interface BloomFilterService {

     <T> void addByBloomFilter(String key, T value);

     <T> boolean includeByBloomFilter(String key, T value);
}
