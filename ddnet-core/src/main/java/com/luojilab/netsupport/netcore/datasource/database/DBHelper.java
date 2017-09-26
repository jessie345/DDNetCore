package com.luojilab.netsupport.netcore.datasource.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.luojilab.netsupport.utils.NetLogger;

/**
 * Created by liushuo on 16/1/12.
 */
class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "compat_nosql.db";
    private static final int DB_VERSION = 2;

    private static DBHelper mDBHelper;

    public static DBHelper getInstance(Context context) {
        synchronized (DBHelper.class) {
            if (mDBHelper == null) {
                mDBHelper = new DBHelper(context);
            }
            return mDBHelper;
        }
    }


    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, 1);
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //创建模拟的nosql数据表
        createCompatibleNosqlTable(db);

        //创建其他的关系数据表
        createTestTable(db);
    }


    private void createCompatibleNosqlTable(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TableDef.TableNosql.DB_TABLE);
            db.execSQL("CREATE TABLE " + TableDef.TableNosql.DB_TABLE + "("
                    + TableDef.TableNosql.Column.COLUMN_ID + " TEXT, "
                    + TableDef.TableNosql.Column.COLUMN_CONTENT_TYPE + " TEXT, "
                    + TableDef.TableNosql.Column.COLUMN_CONTENT + " TEXT, "
                    + "PRIMARY KEY(" + TableDef.TableNosql.Column.COLUMN_ID + "," + TableDef.TableNosql.Column.COLUMN_CONTENT_TYPE + ")"
                    + "); ");
        } catch (SQLException ex) {
            NetLogger.e(NetLogger.TAG, String.format("couldn't create table in %s database", DB_NAME));

            throw ex;
        }
    }

    private void createTestTable(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TableDef.TableTest.DB_TABLE);
            db.execSQL("CREATE TABLE " + TableDef.TableTest.DB_TABLE + "(" + TableDef.TableTest.Column.COLUMN_ID
                    + " INTEGER PRIMARY KEY,"
                    + TableDef.TableTest.Column.COLUMN_CHILD_ID + " TEXT, "
                    + TableDef.TableTest.Column.COLUMN_PARENT_ID + " TEXT); ");
        } catch (SQLException ex) {
            NetLogger.e(NetLogger.TAG, String.format("couldn't create table in %s database", DB_NAME));

            throw ex;
        }
    }

}
