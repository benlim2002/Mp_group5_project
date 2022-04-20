package com.example.mp_group5_project.sql;

import static android.text.TextUtils.isEmpty;
import static org.xmlpull.v1.XmlPullParser.TEXT;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Database extends SQLiteOpenHelper {
    static final String databaseName = "My database";
    static final int databaseVersion = 0;
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Filtergraphy.db";

    // User table name;
    private static final String TABLE_USER = "user";

    // User Table Columns names
    private static final String COLUMN_USER_USERNAME = "username";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_PHONENO = "phone_no";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";

    // Images table foreign keys
    private static final String COLUMN_USER_ID = "id";

    // Images table name
    private static final String TABLE_IMAGE = "image";

    // Images Table Column names
    private static final String COLUMN_IMAGE_ID = "imgId";
    private static final String COLUMN_IMAGE_PATH = "path";

    SharedPreferences loginpref;
    final Context context;

    private User currentUser;

    SQLiteDatabase db;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        loginpref = context.getSharedPreferences("login_details", Context.MODE_PRIVATE);
        String currentUsername = loginpref.getString("username", "");
        Log.d("Image DB", currentUsername);
        if(!isEmpty(currentUsername)) {
            SQLiteDatabase db = getWritableDatabase();
            // array of columns to fetch
            String[] columns = {
                    COLUMN_USER_ID,
                    COLUMN_USER_NAME,
                    COLUMN_USER_PHONENO,
                    COLUMN_USER_EMAIL,
                    COLUMN_USER_PASSWORD,
                    COLUMN_USER_USERNAME
            };

            // selection criteria
            String selection = COLUMN_USER_USERNAME + " = ?" ;

            // selection arguments
            String[] selectionArgs = {currentUsername};
            // query user table with conditions
            Cursor cursor = db.query(TABLE_USER,//Table to query
                    columns,                    //columns to return
                    selection,                  //columns for the WHERE clause
                    selectionArgs,              //The values for the WHERE clause
                    null,               //group the rows
                    null,                //filter by row groups
                    null);              //The sort order

            if (cursor.moveToNext()){
                currentUser = new User(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONENO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD))
                );

            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w("Database", "Crreating newest Database Tables");
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_PHONENO + " TEXT," + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_USER_USERNAME + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";
        String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGE + " ("
                + COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_IMAGE_PATH + " TEXT, "
                + COLUMN_USER_ID + " TEXT" + ")";
        try {
            db.execSQL(CREATE_USER_TABLE);
            db.execSQL(CREATE_IMAGE_TABLE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Database", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public void addImage(String path) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_PATH, path);
        values.put(COLUMN_USER_ID, currentUser.getId());

        // Inserting Row
        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }

    // Get User Images based on userid
    public String[] getUserImages() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_IMAGE, new String[]{COLUMN_IMAGE_PATH}, COLUMN_USER_ID + "=?", new String[]{String.valueOf(currentUser.getId())}, null, null, null, null);
        int i = 0;
        String images[] = new String[cursor.getCount()];
        while (cursor.moveToNext()) {
            images[i] = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH));
            i++;
        }
        return images;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void addUser(String name, String phoneNo, String email, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_PHONENO, phoneNo);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_USERNAME, username);
        values.put(COLUMN_USER_PASSWORD, password);

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    // Update User Details
    public int UpdateUserDetails(String name, String email, String phoneNo, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put(COLUMN_USER_NAME, name);
        cVals.put(COLUMN_USER_EMAIL, email);
        cVals.put(COLUMN_USER_PHONENO, phoneNo);
        cVals.put(COLUMN_USER_PASSWORD, password);
        int count = db.update(TABLE_USER, cVals, COLUMN_USER_ID+" = ?",new String[]{String.valueOf(currentUser.getId())});
        currentUser.setEmail(email);
        currentUser.setName(name);
        currentUser.setPhoneNo(phoneNo);
        currentUser.setPassword(password);
        return count;
    }

    // Get User id and password based on username
    public ArrayList<HashMap<String, String>> GetUserByUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_PASSWORD
        };

        // selection criteria
        String selection = COLUMN_USER_USERNAME + " = ?" ;

        // selection arguments
        String[] selectionArgs = {username};
        // query user table with conditions
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        if (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("id",cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.put("password",cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)));
            userList.add(user);
        }
        return  userList;
    }
}
