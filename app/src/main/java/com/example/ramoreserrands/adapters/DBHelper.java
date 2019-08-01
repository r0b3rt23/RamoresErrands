package com.example.ramoreserrands.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ramoreserrands.model.UserInfo;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "rema.db";
    public static final String REMA_TABLE_NAME = "rema_login";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_FIRSTNAME = "firstname";
    public static final String COLUMN_LASTNAME = "lastname";
    public static final String COLUMN_BIRTHDAY = "birthday";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_BARANGAY = "barangay";
    public static final String COLUMN_HOUSE_STREET = "house_street";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_MOBILE = "mobile";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_IMG  = "img_url";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + REMA_TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER,"
                        + COLUMN_USER_ID + " INTEGER,"
                        + COLUMN_FIRSTNAME + " TEXT,"
                        + COLUMN_LASTNAME + " TEXT,"
                        + COLUMN_BIRTHDAY + " DATETIME,"
                        + COLUMN_GENDER + " TEXT,"
                        + COLUMN_CITY + " TEXT,"
                        + COLUMN_BARANGAY + " TEXT,"
                        + COLUMN_HOUSE_STREET + " TEXT,"
                        + COLUMN_EMAIL + " TEXT,"
                        + COLUMN_MOBILE + " TEXT,"
                        + COLUMN_PASSWORD + " TEXT,"
                        + COLUMN_IMG + " TEXT"
                        + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS rema_login");
        onCreate(db);
    }

    public boolean insertLoginUser (int user_id, String firstname,String lastname, String birthday,String gender, String city, String barangay, String house_street,String email,String mobile, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, 1);
        contentValues.put(COLUMN_USER_ID, user_id);
        contentValues.put(COLUMN_FIRSTNAME, firstname);
        contentValues.put(COLUMN_LASTNAME, lastname);
        contentValues.put(COLUMN_BIRTHDAY, birthday);
        contentValues.put(COLUMN_GENDER, gender);
        contentValues.put(COLUMN_CITY, city);
        contentValues.put(COLUMN_BARANGAY, barangay);
        contentValues.put(COLUMN_HOUSE_STREET, house_street);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_MOBILE, mobile);
        contentValues.put(COLUMN_PASSWORD, password);
        db.insert(REMA_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean FBinsertLoginUser (int user_id, String firstname,String lastname, String birthday,String gender, String city, String barangay, String house_street,String email,String mobile, String password, String img_url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, 1);
        contentValues.put(COLUMN_USER_ID, user_id);
        contentValues.put(COLUMN_FIRSTNAME, firstname);
        contentValues.put(COLUMN_LASTNAME, lastname);
        contentValues.put(COLUMN_BIRTHDAY, birthday);
        contentValues.put(COLUMN_GENDER, gender);
        contentValues.put(COLUMN_CITY, city);
        contentValues.put(COLUMN_BARANGAY, barangay);
        contentValues.put(COLUMN_HOUSE_STREET, house_street);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_MOBILE, mobile);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_IMG, img_url);
        db.insert(REMA_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean updateLoginUser (int user_id, String firstname,String lastname, String birthday,String gender, String city, String barangay, String house_street,String email,String mobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_ID, user_id);
        contentValues.put(COLUMN_FIRSTNAME, firstname);
        contentValues.put(COLUMN_LASTNAME, lastname);
        contentValues.put(COLUMN_BIRTHDAY, birthday);
        contentValues.put(COLUMN_GENDER, gender);
        contentValues.put(COLUMN_CITY, city);
        contentValues.put(COLUMN_BARANGAY, barangay);
        contentValues.put(COLUMN_HOUSE_STREET, house_street);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_MOBILE, mobile);
        db.update(REMA_TABLE_NAME, contentValues, "user_id = ?",new String[] { String.valueOf(user_id) });
        db.close();
        return true;
    }

    public UserInfo getUserInfo() {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(REMA_TABLE_NAME,null, COLUMN_ID + "=?",
                new String[]{String.valueOf(1)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        UserInfo userinfo = new UserInfo(
                cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_FIRSTNAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_LASTNAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_BIRTHDAY)),
                cursor.getString(cursor.getColumnIndex(COLUMN_GENDER)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CITY)),
                cursor.getString(cursor.getColumnIndex(COLUMN_BARANGAY)),
                cursor.getString(cursor.getColumnIndex(COLUMN_HOUSE_STREET)),
                cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_MOBILE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)),
                cursor.getString(cursor.getColumnIndex(COLUMN_IMG)));

        // close the db connection
        cursor.close();

        return userinfo;
    }

    public int getUserCount() {
        String countQuery = "SELECT  * FROM " + REMA_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public boolean deleteUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(REMA_TABLE_NAME,null,null);
        db.close();
        return true;
    }
}
