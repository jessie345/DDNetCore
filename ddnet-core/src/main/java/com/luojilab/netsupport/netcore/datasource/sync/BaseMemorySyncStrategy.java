package com.luojilab.netsupport.netcore.datasource.sync;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.datasource.base.BaseLocalStorage;
import com.luojilab.netsupport.netcore.utils.CoreUtils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liushuo on 16/3/30.
 * 内存池的本地数据同步方案
 */
public abstract class BaseMemorySyncStrategy {

    @Nullable
    public <T> List<T> syncArray(@NonNull Class<T> dataClass) {
        Preconditions.checkNotNull(dataClass);

        BaseLocalStorage bls = getBaseLocalStorage();
        List<JsonObject> data = bls.queryItemsByType(dataClass.getSimpleName());
        return makeBeanList(dataClass, data);
    }

    @Nullable
    public <T> T syncObject(@NonNull Class<T> dataClass, @NonNull String id) {
        Preconditions.checkNotNull(dataClass);
        Preconditions.checkNotNull(id);

        BaseLocalStorage bls = getBaseLocalStorage();
        List<JsonObject> dataList = bls.queryItemsByIds(dataClass.getSimpleName(), id);

        if (dataList.size() > 0) {
            JsonObject data = dataList.get(0);
            return CoreUtils.json2Bean(data, dataClass);
        }

        return null;
    }

    @NonNull
    private <T> List<T> makeBeanList(@NonNull Class<T> dataClass, @Nullable List<JsonObject> data) {
        Preconditions.checkNotNull(dataClass);

        if (data == null || data.size() == 0) return Collections.emptyList();

        List<T> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JsonObject obj = data.get(i);
            if (obj == null) continue;

            T element = CoreUtils.json2Bean(obj, dataClass);
            if (element == null) continue;

            list.add(element);
        }

        return list;

    }

    @NonNull
    protected abstract BaseLocalStorage getBaseLocalStorage();
}
