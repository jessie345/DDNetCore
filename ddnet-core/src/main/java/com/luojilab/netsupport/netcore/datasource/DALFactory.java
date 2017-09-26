package com.luojilab.netsupport.netcore.datasource;

import android.content.Context;
import android.support.annotation.NonNull;

import com.luojilab.netsupport.netcore.datasource.base.AdvanceLocalStorage;
import com.luojilab.netsupport.netcore.datasource.base.BaseLocalStorage;
import com.luojilab.netsupport.netcore.datasource.base.MemoryStorage;
import com.luojilab.netsupport.netcore.datasource.database.NosqlStorage;
import com.luojilab.netsupport.netcore.datasource.database.SqlStorage;
import com.luojilab.netsupport.netcore.datasource.retrofit.RetrofitClient;
import com.luojilab.netsupport.utils.NetCoreInitializer;

/**
 * Created by liushuo on 16/4/5.
 * data abstract layer factory(数据源层访问工厂),用于获取对应的访问实例,具体实现细节，参考相应的具体实现
 * 使用之前必须执行init初始化操作
 */
public class DALFactory {

    private static Context sContext;

    /**
     * 获取内存池实例，内存池可以配置数据同步方案（内存访问失效需要指定是否同步本地数据及同步方式）
     *
     * @return
     */
    @NonNull
    public static MemoryStorage getMemoryStorage() {

        return MemoryDataPool.getInstance();
    }

    /**
     * 获取基本数据存储服务，nosql实现，提供基本数据查询方式（基于_id|全表查询）
     *
     * @return
     */
    @NonNull
    public static BaseLocalStorage getBaseStorage() {
        checkContext();

        return NosqlStorage.getInstance(sContext);

    }

    /**
     * 获取高级数据存储服务，sql实现，提供数据查询方式的灵活性（基于_id|全表查询|自定义column查询）
     *
     * @return
     */
    @NonNull
    public static AdvanceLocalStorage getAdvanceStorage() {
        checkContext();

        return SqlStorage.getInstance(sContext);
    }

    @NonNull
    public static <T> T getApiService(Class<T> cls) {
        return RetrofitClient.getApiService(cls);
    }

    private static void checkContext() {
        if (sContext == null) {
            sContext = NetCoreInitializer.getInstance().getAppContext();
        }
    }
}
