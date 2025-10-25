package com.example.tech_shop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_data.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "users";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Tạo bảng lưu user
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "password TEXT)";
        db.execSQL(createTable);
    }

    // Nếu có cập nhật DB
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // ✅ Lưu thông tin user khi đăng nhập thành công
    public void saveUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null); // chỉ giữ 1 user duy nhất

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // ✅ Kiểm tra xem user đã đăng nhập chưa
    public boolean isUserLoggedIn() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " LIMIT 1", null);
        boolean loggedIn = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return loggedIn;
    }

    // ✅ Đăng xuất user
    public void logoutUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
