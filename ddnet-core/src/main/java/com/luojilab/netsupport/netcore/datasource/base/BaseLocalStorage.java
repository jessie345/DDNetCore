package com.luojilab.netsupport.netcore.datasource.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by liushuo on 16/3/19.
 * 基本数据存储服务访问接口，提供数据存储访问的基本能力
 * 对标nosql的存储实现
 */
public interface BaseLocalStorage {

    /**
     * @param dataType 数据类型
     * @param data
     * @return
     */
    void saveData(@NonNull String dataType, @NonNull JsonObject... data);

    @NonNull
    List<JsonObject> queryItemsByIds(@NonNull final String dataType, @NonNull Object... ids);

    @NonNull
    List<JsonObject> queryItemsByType(@NonNull final String dataType);

    void deleteItemsById(@NonNull final String dataType, @NonNull Object... ids);

    void clearItemsOfType(@NonNull String dataType);

    void clearDataBase();


}
