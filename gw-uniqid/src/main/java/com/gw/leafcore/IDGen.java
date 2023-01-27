package com.gw.leafcore;


import com.gw.leafcore.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
