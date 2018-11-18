package com.mtr.application;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    private static final String TAG = "DatabaseHelper";


    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }


    public void open() {
        this.db = openHelper.getWritableDatabase();
    }


    public void close() {
        if (db != null) {
            this.db.close();
        }
    }


//    public String getBook(String ean) {
//        if (!ean.isEmpty()) {
//            Cursor cursor = db.rawQuery("Select NAME from articles where EAN =" + ean, new String[]{});
//            StringBuffer buffer = new StringBuffer();
//            if (cursor.moveToFirst()) {
//                String name = cursor.getString(0);
//                buffer.append("" + name);
//                cursor.close();
//                return buffer.toString();
//            }
//            cursor.close();
//        }
//        return "notFound";
//    }

    public Item getBook(String ean) {
        Item item = new Item();
        if (!ean.isEmpty()) {
            Cursor cursor = db.rawQuery("Select NAME, PRICE from articles where EAN =" + ean, new String[]{});
            if (cursor.moveToFirst()) {
                String name = cursor.getString(0);
                String price = cursor.getString(1);
                cursor.close();
                item.setEan(ean);
                item.setName(name);
                item.setPrice(price);
                return item;
            }
            cursor.close();
        }
        item.setName("notFound");
        item.setPrice("");
        item.setEan(ean);
        return item;
    }

    public SQLiteDatabase getDb() {
        return this.db;
    }

    public long getTableSize() {
        long count = DatabaseUtils.queryNumEntries(db, "articles");
        return count;
    }


    public String getBookByName(String name) {
        if (!name.isEmpty()) {
            System.out.println("TEST + " + name);
            Cursor cursor = db.rawQuery("Select EAN from articles where NAME = " + name, new String[]{});
            StringBuffer buffer = new StringBuffer();
            if (cursor.moveToFirst()) {
                String ean = cursor.getString(0);
                System.out.println(ean);
                buffer.append("" + ean);
                cursor.close();
                return buffer.toString();
            }
            cursor.close();
        }
        return "notFound";
    }

    public String getPrice(String ean) {
        if (!ean.isEmpty()) {
            Cursor cursor = db.rawQuery("Select PRICE from articles where EAN = " + ean, new String[]{});
            StringBuffer buffer = new StringBuffer();
            if (cursor.moveToFirst()) {
                String price = cursor.getString(0);
                buffer.append("" + ean);
                cursor.close();
                return buffer.toString();
            }
            cursor.close();
        }
        return "0 ,- Kč";
    }


    public ArrayList<Item> fullTextSearch(String string) {
        ArrayList<Item> list = new ArrayList<>();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] selectionArgs = {string};
        Cursor cursor = db.rawQuery(String.format("Select ean, name FROM fts_books_names WHERE fts_books_names MATCH ' \"^%s\" ' ORDER BY name ASC", selectionArgs), null);
        System.out.println("TEST " + cursor.getCount());
        if (cursor.moveToNext()) {
            do {
                Item item = new Item();
                item.setEan(cursor.getString(cursor.getColumnIndex("ean")));
                item.setName(cursor.getString(cursor.getColumnIndex("name")));
                list.add(item);
            } while (cursor.moveToNext());
        }
        return list;
    }

}
