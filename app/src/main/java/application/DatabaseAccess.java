package application;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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


    public String getBook(String ean) {
        if (!ean.isEmpty()) {
            Cursor cursor = db.rawQuery("Select NAME from articles where EAN =" + ean, new String[]{});
            StringBuffer buffer = new StringBuffer();
            if (cursor.moveToFirst()) {
                String name = cursor.getString(0);
                buffer.append("" + name);
                cursor.close();
                return buffer.toString();
            }
            cursor.close();
        }
        return "notFound";
    }




}
