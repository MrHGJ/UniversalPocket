package com.hgj.universal.pocket.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1; //数据库版本号
    private static String DB_NAME = "universal_pocket_db"; //数据库名
    public static String TABLE_NAME = "memo_content";  //表名

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        //创建表
        String sql = "CREATE TABLE " + TABLE_NAME + "(id integer primary key autoincrement ,title text, content text , time text , isTop integer)";
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
