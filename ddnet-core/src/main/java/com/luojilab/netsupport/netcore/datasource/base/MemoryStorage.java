package com.luojilab.netsupport.netcore.datasource.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.luojilab.netsupport.netcore.datasource.sync.BaseMemorySyncStrategy;

import java.util.List;

/**
 * Created by liushuo on 16/3/19.
 * 内存池对外访问接口
 */
public interface MemoryStorage {

    //缓存非列表数据到内存
    <T> void cacheObjectDataInMemory(@NonNull Class<T> dataClass,@NonNull String id,@NonNull Object data);

    //缓存列表数据到内存
    <T> void cacheArrayDataInMemory(@NonNull Class<T> dataClass,@NonNull List<T> data);

    //根据id查找数据
    @Nullable
    <T> T getObjectDataCached(@NonNull Class<T> dataClass,@NonNull final String id);

    //获取指定类型的所有数据
    @Nullable
    <T> List<T> getArrayDataCached(@NonNull final Class<T> dataClass);

    //配置内存池的同步策略
    @NonNull
    MemoryStorage configureSyncStrategy(@Nullable BaseMemorySyncStrategy syncStrategy);


}
