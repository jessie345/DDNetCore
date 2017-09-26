package com.luojilab.netsupport.netcore.datasource;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.datasource.base.MemoryStorage;
import com.luojilab.netsupport.netcore.datasource.sync.BaseMemorySyncStrategy;
import com.luojilab.netsupport.utils.NetLogger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by liushuo on 16/3/19.
 * 注意:内存池返回的数据可能是null，调用者需要注意 异常判断
 */
public class MemoryDataPool implements MemoryStorage {
    private static final int CACHE_SIZE = 10;

    //缓存某种类型的数据<dataType(dataType or table name),<id,数据>>
    private final Map<Class, Map<String, Object>> mObjectDatas = newLruMap("object datas", CACHE_SIZE);

    private final Map<Class, List<?>> mArrayDatas = newLruMap("array datas", CACHE_SIZE);

    private ThreadLocal<BaseMemorySyncStrategy> mMemorySyncStrategy = new ThreadLocal<>();

    private static MemoryDataPool mInstance;

    private MemoryDataPool() {
    }

    @NonNull
    public static MemoryDataPool getInstance() {
        synchronized (MemoryDataPool.class) {
            if (mInstance == null) {
                mInstance = new MemoryDataPool();
            }
            mInstance.configureSyncStrategy(null);
        }
        return mInstance;
    }

    @NonNull
    @Override
    public MemoryDataPool configureSyncStrategy(@Nullable BaseMemorySyncStrategy syncStrategy) {
        mMemorySyncStrategy.set(syncStrategy);
        return this;
    }


    @Override
    public <T> void cacheObjectDataInMemory(@NonNull Class<T> dataClass, @NonNull String id, @NonNull Object data) {
        Preconditions.checkNotNull(dataClass);
        Preconditions.checkNotNull(data);
        Preconditions.checkArgument(!TextUtils.isEmpty(id));

        synchronized (mObjectDatas) {
            Map<String, Object> datasOfType = mObjectDatas.get(dataClass);

            if (datasOfType == null) {
                datasOfType = newLruMap("element count of type :" + dataClass.getSimpleName(), CACHE_SIZE);
                mObjectDatas.put(dataClass, datasOfType);
            }
            datasOfType.put(id, data);
        }
    }

    @Override
    public <T> void cacheArrayDataInMemory(@NonNull Class<T> dataClass, @NonNull List<T> data) {
        Preconditions.checkNotNull(dataClass);
        Preconditions.checkNotNull(data);

        synchronized (mArrayDatas) {
            mArrayDatas.put(dataClass, data);
        }
    }

    /**
     * 内存有数据，使用内存数据，内存无数据，读取本地数据并存储到内存
     *
     * @param dataClass
     * @param id
     * @return
     */
    @Nullable
    @Override
    public <T> T getObjectDataCached(@NonNull Class<T> dataClass, @NonNull String id) {
        Preconditions.checkNotNull(dataClass);
        Preconditions.checkArgument(!TextUtils.isEmpty(id));

        if (mObjectDatas.containsKey(dataClass)) {
            Map<String, Object> datasOfType = mObjectDatas.get(dataClass);

            if (datasOfType != null && datasOfType.containsKey(id)) {
                return (T) datasOfType.get(id);
            }
        }

        if (mMemorySyncStrategy.get() != null) {
            //内存无数据，取本地
            T data = mMemorySyncStrategy.get().syncObject(dataClass, id);
            if (data == null) return null;

            cacheObjectDataInMemory(dataClass, id, data);
            return data;
        }

        return null;
    }

    @Nullable
    @Override
    public <T> List<T> getArrayDataCached(@NonNull Class<T> dataClass) {
        Preconditions.checkNotNull(dataClass);

        if (mArrayDatas.containsKey(dataClass)) {
            NetLogger.d(NetLogger.TAG, "内存数据返回,dataType:" + dataClass.getSimpleName());

            return (List<T>) mArrayDatas.get(dataClass);

        } else if (mMemorySyncStrategy.get() != null) {
            List<T> beans = mMemorySyncStrategy.get().syncArray(dataClass);
            if (beans == null) return null;

            cacheArrayDataInMemory(dataClass, beans);
            NetLogger.d(NetLogger.TAG, "本地数据返回,dataType:" + dataClass.getSimpleName());

            return beans;
        }

        return null;
    }

    @NonNull
    private static Map newLruMap(final String des, final int size) {
        return new LinkedHashMap(size, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Entry eldest) {
                NetLogger.d(NetLogger.TAG, String.format(Locale.CHINA, "%1$s cache size:%2$d", des, size()));
                return size() > size;
            }
        };
    }

}
