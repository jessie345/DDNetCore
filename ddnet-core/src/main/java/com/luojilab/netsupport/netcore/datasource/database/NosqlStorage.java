/**
 * Copyright (c) 2014 Guanghe.tv
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/
package com.luojilab.netsupport.netcore.datasource.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.common.base.Preconditions;
import com.luojilab.netsupport.netcore.datasource.base.BaseLocalStorage;
import com.luojilab.netsupport.netcore.utils.CoreUtils;
import com.luojilab.netsupport.utils.NetLogger;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Collections;
import java.util.List;

/**
 * Created by Android Studio
 * User: killnono(陈凯)
 * Date: 16/5/9
 * Version: 1.0
 */
public class NosqlStorage implements BaseLocalStorage {

    private static final String TAG = "NosqlStorage";

    private DBHelper mHelper;
    private static NosqlStorage sInstance;

    private NosqlStorage(Context context) {
        mHelper = DBHelper.getInstance(context);
    }

    @NonNull
    public static NosqlStorage getInstance(@NonNull Context context) {
        Preconditions.checkNotNull(context);

        synchronized (NosqlStorage.class) {
            if (sInstance == null) {
                sInstance = new NosqlStorage(context);
            }
        }
        return sInstance;
    }


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

            JsonPrimitive jp = data.getAsJsonPrimitive(TableDef.TableNosql.Column.COLUMN_ID);

            String id = "";
            if (jp != null) {
                id = jp.getAsString();
            } else {
                id = Long.toString(System.currentTimeMillis());
                NetLogger.w(NetLogger.TAG, "需要保存的数据没有id，自动将当前时间作为id，用户只能根据type 查询到此条数据:" + data.toString());
            }

            ContentValues values = new ContentValues();
            values.put(TableDef.TableNosql.Column.COLUMN_ID, id);
            values.put(TableDef.TableNosql.Column.COLUMN_CONTENT_TYPE, dataType);
            values.put(TableDef.TableNosql.Column.COLUMN_CONTENT, data.toString());

            try {
                db.replaceOrThrow(TableDef.TableNosql.DB_TABLE, null, values);
            } catch (Exception e) {
                NetLogger.e(TAG, e, null);
            }
        }
    }

    @NonNull
    @Override
    public List<JsonObject> queryItemsByIds(@NonNull String dataType, @NonNull Object... ids) {
        Preconditions.checkNotNull(dataType);
        Preconditions.checkNotNull(ids);

        if (ids.length == 0) return Collections.emptyList();

        //select * from table where doctype =? and id in (?,...);
        StringBuilder sql = new StringBuilder("select * from %1$s where %2$s=? and %3$s");
        Pair<String, String[]> pair = CoreUtils.buildSqlInSegment(ids);
        sql.append(pair.first);
        String[] args = pair.second;

        if (args.length == 0) return Collections.emptyList();

        String[] arr = new String[args.length + 1];
        arr[0] = dataType;
        System.arraycopy(args, 0, arr, 1, args.length);

        Cursor cursor = null;
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            cursor = db.rawQuery(String.format(sql.toString(), TableDef.TableNosql.DB_TABLE, TableDef.TableNosql.Column.COLUMN_CONTENT_TYPE, TableDef.TableNosql.Column.COLUMN_ID), arr);
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
            cursor = db.query(TableDef.TableNosql.DB_TABLE, null, TableDef.TableNosql.Column.COLUMN_CONTENT_TYPE + "=?", new String[]{dataType}, null, null, null);
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

        //delete from table where doctype =? and id in (?,...);
        StringBuilder sql = new StringBuilder("delete from %1$s where %2$s=? and %3$s");
        Pair<String, String[]> pair = CoreUtils.buildSqlInSegment(ids);
        sql.append(pair.first);
        String[] args = pair.second;

        if (args.length == 0) return;

        String[] arr = new String[args.length + 1];
        arr[0] = dataType;
        System.arraycopy(args, 0, arr, 1, args.length);

        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.execSQL(String.format(sql.toString(), TableDef.TableNosql.DB_TABLE, TableDef.TableNosql.Column.COLUMN_CONTENT_TYPE, TableDef.TableNosql.Column.COLUMN_ID), arr);

        } catch (Exception e) {
            NetLogger.e(e, null);
        }

    }

    @Override
    public void clearItemsOfType(@NonNull String dataType) {
        Preconditions.checkNotNull(dataType);

        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.delete(TableDef.TableNosql.DB_TABLE, TableDef.TableNosql.Column.COLUMN_CONTENT_TYPE + "=?", new String[]{dataType});
        } catch (Exception e) {
            NetLogger.e(e, null);
        }
    }

    @Override
    public void clearDataBase() {
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.delete(TableDef.TableNosql.DB_TABLE, null, null);
        } catch (Exception e) {
            NetLogger.e(e, null);
        }
    }
}
