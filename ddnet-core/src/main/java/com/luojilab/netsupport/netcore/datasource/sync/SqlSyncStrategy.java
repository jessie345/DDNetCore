package com.luojilab.netsupport.netcore.datasource.sync;

import android.support.annotation.NonNull;

import com.luojilab.netsupport.netcore.datasource.DALFactory;
import com.luojilab.netsupport.netcore.datasource.base.BaseLocalStorage;

/**
 * Created by liushuo on 16/3/30.
 */
public class SqlSyncStrategy extends BaseMemorySyncStrategy {
    public static final BaseMemorySyncStrategy mStrategy = new SqlSyncStrategy();

    private SqlSyncStrategy() {
    }

    @NonNull
    @Override
    protected BaseLocalStorage getBaseLocalStorage() {
        return DALFactory.getAdvanceStorage();
    }
}
