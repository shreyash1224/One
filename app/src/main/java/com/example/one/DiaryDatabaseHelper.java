package com.example.one;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DiaryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "OneDiaryDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_DIARY = "diary";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";

    public DiaryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_DIARY + " (" +
                COLUMN_TITLE + " TEXT PRIMARY KEY, " +
                COLUMN_CONTENT + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIARY);
        onCreate(db);
    }

    public void addPage(String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        db.insert(TABLE_DIARY, null, values);
        db.close();
    }

    public DiaryPage getPage(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DIARY, null, COLUMN_TITLE + "=?", new String[]{title},
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
            cursor.close();
            return new DiaryPage(title, content);
        }
        return null;
    }

    public void updatePage(String oldTitle, String newTitle, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, newTitle);
        values.put(COLUMN_CONTENT, content);
        db.update(TABLE_DIARY, values, COLUMN_TITLE + "=?", new String[]{oldTitle});
        db.close();
    }

    public void deletePage(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_DIARY, COLUMN_TITLE + "=?", new String[]{title});
        db.close();

        // Add a log to check if the delete operation is successful
        if (rowsAffected > 0) {
            Log.d("DiaryDatabaseHelper", "Page deleted: " + title);
        } else {
            Log.d("DiaryDatabaseHelper", "Failed to delete page: " + title);
        }
    }


    public ArrayList<String> getAllPages() {
        ArrayList<String> pages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DIARY, new String[]{COLUMN_TITLE}, null, null,
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                pages.add(title);
            }
            cursor.close();
        }
        return pages;
    }
}
