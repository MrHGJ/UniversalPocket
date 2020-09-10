package com.hgj.universal.pocket.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hgj.universal.pocket.model.MemoBean;

import java.util.LinkedList;
import java.util.List;

public class DBManager {
    private volatile static DBManager mInstance;   //静态引用
    private DBHelper dbHelper;

    private DBManager(Context context) {
        dbHelper = new DBHelper(context.getApplicationContext());
    }

    /**
     * 获取单例引用
     */
    public static DBManager getInstance(Context context) {
        DBManager inst = mInstance;
        if (inst == null) {
            synchronized (DBManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new DBManager(context);
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    /**
     * 查询全部数据
     */
    public List<MemoBean> queryAll() {
        List<MemoBean> list = new LinkedList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_NAME, new String[]{"id", "title", "content", "time", "isTop"}, null, null, null, null, "time DESC", null);
        while (cursor.moveToNext()) {
            MemoBean data = new MemoBean();
            data.id = cursor.getInt(cursor.getColumnIndex("id"));
            data.title = cursor.getString(cursor.getColumnIndex("title"));
            data.content = cursor.getString(cursor.getColumnIndex("content"));
            data.time = cursor.getString(cursor.getColumnIndex("time"));
            data.isTop = cursor.getInt(cursor.getColumnIndex("isTop"));
            list.add(data);
        }
        db.close();
        return list;
    }

    /**
     * 查询一条数据
     */
    public MemoBean queryMemoDetail(int id) {
        MemoBean memo = new MemoBean();
        ;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_NAME, new String[]{"id", "title", "content", "time", "isTop"}, "id = " + id, null, null, null, "time DESC", null);
        while (cursor.moveToNext()) {
            memo.id = cursor.getInt(cursor.getColumnIndex("id"));
            memo.title = cursor.getString(cursor.getColumnIndex("title"));
            memo.content = cursor.getString(cursor.getColumnIndex("content"));
            memo.time = cursor.getString(cursor.getColumnIndex("time"));
            memo.isTop = cursor.getInt(cursor.getColumnIndex("isTop"));
        }
        db.close();
        return memo;
    }

    /**
     * 插入一条数据
     */
    public void insert(MemoBean data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", data.title);
        cv.put("content", data.content);
        cv.put("time", data.time);
        cv.put("isTop", 0);
        db.insert(DBHelper.TABLE_NAME, null, cv);
        db.close();
    }

    /**
     * 更新一条数据
     */
    public void update(MemoBean data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", data.title);
        cv.put("content", data.content);
        cv.put("time", data.time);
        db.update(DBHelper.TABLE_NAME, cv, "id=?", new String[]{"" + data.id});
    }

    /**
     * 删除一条数据
     */
    public void delete(MemoBean data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_NAME, "id=?", new String[]{"" + data.id});
        db.close();
    }

    /**
     * 删除所有数据
     */
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "delete from " + DBHelper.TABLE_NAME;
        db.execSQL(sql);
        db.close();
    }
}
