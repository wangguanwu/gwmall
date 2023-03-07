package com.gw.gwmall.util;

import com.alibaba.otter.canal.protocol.CanalEntry;

/**
 * @author guanwu
 * @created on 2023-03-02 19:48:41
 **/
public class CanalUtil {

    public static boolean isTransactionalLog(CanalEntry.Entry entry) {
        return entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
                || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND;
    }

}
