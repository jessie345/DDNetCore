package com.luojilab.netsupport.netcore.datasource.sync;

import android.support.annotation.NonNull;

import com.luojilab.netsupport.netcore.datasource.base.BaseLocalStorage;
import com.luojilab.netsupport.netcore.datasource.DALFactory;

/**
 * Created by liushuo on 16/3/30.
 */
public class NoSqlSyncStrategy extends BaseMemorySyncStrategy {
    public static final BaseMemorySyncStrategy mStrategy = new NoSqlSyncStrategy();

    private NoSqlSyncStrategy() {
    }

    @NonNull
    @Override
    protected BaseLocalStorage getBaseLocalStorage() {
        return DALFactory.getBaseStorage();
    }
}
