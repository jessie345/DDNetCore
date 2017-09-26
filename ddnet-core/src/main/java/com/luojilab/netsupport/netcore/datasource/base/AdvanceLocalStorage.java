package com.luojilab.netsupport.netcore.datasource.base;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by liushuo on 16/4/5.
 * 高级数据存储服务访问接口，提供基本数据存储访问服务和高级查询功能
 */
public interface AdvanceLocalStorage extends BaseLocalStorage {

    @NonNull
    List<JsonObject> queryItemsByColumn(@NonNull String dataType, @NonNull String column, @NonNull Object... values);

    /**
     * row id 由系统维护，自增
     *
     * @param dataType
     */
    void resetInternalRowId(@NonNull String dataType);
}
