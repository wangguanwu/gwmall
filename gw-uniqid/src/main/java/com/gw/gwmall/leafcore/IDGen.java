package com.gw.gwmall.leafcore;


import com.gw.gwmall.leafcore.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
