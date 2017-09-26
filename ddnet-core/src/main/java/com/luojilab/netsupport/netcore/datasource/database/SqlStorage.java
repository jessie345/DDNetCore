package com.luojilab.netsupport.netcore.datasource.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.datasource.base.AdvanceLocalStorage;
import com.luojilab.netsupport.netcore.utils.CoreUtils;
import com.luojilab.netsupport.utils.NetLogger;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;

/**
 * Created by liushuo on 16/3/21.
 */
public class SqlStorage implements AdvanceLocalStorage {

    private DBHelper mHelper;

    private static SqlStorage sInstance;

    private SqlStorage(Context context) {
        mHelper = DBHelper.getInstance(context);
    }

    @NonNull
    public static SqlStorage getInstance(@NonNull Context context) {
        Preconditions.checkNotNull(context);

        synchronized (SqlStorage.class) {
            if (sInstance == null) {
                sInstance = new SqlStorage(context);
            }
        }
        return sInstance;
    }


    /**
     * 使用SqlStorage存储数据时，需要确保指定dataType 对应的表存在
     *
     * @param dataType
     * @param dataArray
     */
    @Override
    public void saveData(@NonNull String dataType, @NonNull JsonObject... dataArray) {
        Preconditions.checkNotNull(dataType);
        Preconditions.checkNotNull(dataArray);

        if (dataArray.length == 0) return;

        SQLiteDatabase db = null;
        try {
            db = mHelper.getWritableDatabase();
        } catch (Exception e) {
            NetLogger.e(e, null);
        }

        if (db == null) return;

        int length = dataArray.length;
        for (int i = 0; i < length && dataArray[i] != null; i++) {
            JsonObject data = dataArray[i];
            if (data == null) continue;

            ContentValues values = CoreUtils.json2ContentValues(data);
            if (values == null) continue;

            try {
                db.insertWithOnConflict(dataType, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                NetLogger.e(NetLogger.TAG, "插入sql数据--type:" + dataType);
            } catch (Exception e) {
                NetLogger.e(e, null);
            }
        }
    }

    @NonNull
    @Override
    public List<JsonObject> queryItemsByIds(@NonNull String dataType, @NonNull Object... ids) {
        return queryItemsByColumn(dataType, BaseColumn.COLUMN_ID, ids);
    }

    @NonNull
    @Override
    public List<JsonObject> queryItemsByColumn(@NonNull String dataType, @NonNull String column, @NonNull Object... values) {
        Preconditions.checkNotNull(dataType);
        Preconditions.checkNotNull(column);
        Preconditions.checkNotNull(values);

        if (values.length == 0) {
            return Collections.emptyList();
        }

        StringBuilder sql = new StringBuilder("select * from %1$s where %2$s");
        Pair<String, String[]> pair = CoreUtils.buildSqlInSegment(values);
        sql.append(pair.first);
        String[] args = pair.second;

        if (args.length == 0) return Collections.emptyList();

        Cursor cursor = null;
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            cursor = db.rawQuery(String.format(sql.toString(), dataType, column), args);
            if (cursor == null) return Collections.emptyList();

            return CoreUtils.parseCursorToJsonList(cursor);

        } catch (Exception e) {
            NetLogger.e(e, null);

            return Collections.emptyList();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    NetLogger.e(e, null);

                }
            }
        }

    }

    @NonNull
    @Override
    public List<JsonObject> queryItemsByType(@NonNull String dataType) {
        Preconditions.checkNotNull(dataType);

        Cursor cursor = null;
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            cursor = db.query(dataType, null, null, null, null, null, null);
            if (cursor == null) return Collections.emptyList();

            return CoreUtils.parseCursorToJsonList(cursor);

        } catch (Exception e) {
            NetLogger.e(e, null);

            return Collections.emptyList();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    NetLogger.e(e, null);
                }
            }
        }
    }

    @Override
    public void deleteItemsById(@NonNull String dataType, @NonNull Object... ids) {
        Preconditions.checkNotNull(dataType);
        Preconditions.checkNotNull(ids);

        if (ids.length == 0) return;

        StringBuilder sql = new StringBuilder("delete from %1$s where %2$s");
        Pair<String, String[]> pair = CoreUtils.buildSqlInSegment(ids);
        sql.append(pair.first);
        String[] args = pair.second;

        if (args.length == 0) return;

        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.execSQL(String.format(sql.toString(), dataType, BaseColumn.COLUMN_ID), args);
        } catch (Exception e) {
            NetLogger.e(e, null);
        }

    }


    @Override
    public void clearDataBase() {
        Class[] classes = TableDef.class.getClasses();
        if (classes == null || classes.length == 0) return;

        SQLiteDatabase db = null;
        try {
            db = mHelper.getWritableDatabase();
        } catch (Exception e) {
            NetLogger.e(e, null);
        }

        if (db == null) return;

        for (Class cls : classes) {
            try {
                String table = cls.getSimpleName();
                db.delete(table, null, null);
            } catch (Exception e) {
                NetLogger.e(e, null);
            }
        }
    }

    @Override
    public void clearItemsOfType(@NonNull String dataType) {
        Preconditions.checkNotNull(dataType);

        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.delete(dataType, null, null);
        } catch (Exception e) {
            NetLogger.e(e, null);
        }

    }

    @Override
    public void resetInternalRowId(@NonNull String dataType) {
        Preconditions.checkNotNull(dataType);

        String sql = String.format("UPDATE sqlite_sequence SET seq = 0 WHERE name='%s'", dataType);
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.execSQL(sql);
        } catch (Exception e) {
            NetLogger.e(e, null);
        }

    }
}
