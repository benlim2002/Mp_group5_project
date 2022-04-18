package com.example.mp_group5_project.sql;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mp_group5_project.sql.User;

import java.util.ArrayList;
import java.util.HashMap;

public class UserDBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "LabSkill2DB.db";

    // User table name
    private static final String TABLE_USER = "user";

    // User Table Columns names
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_PHONENO = "phone_no";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_USERNAME = "username";
    private static final String COLUMN_USER_PASSWORD = "password";

    SharedPreferences loginpref;
    private User currentUser;

    public UserDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        loginpref = context.getSharedPreferences("login_details", Context.MODE_PRIVATE);
        String currentUsername = loginpref.getString("username", "");
        if(!isEmpty(currentUsername)) {
            SQLiteDatabase db = this.getWritableDatabase();
            // array of columns to fetch
            String[] columns = {
                    COLUMN_USER_ID,
                    COLUMN_USER_NAME,
                    COLUMN_USER_PHONENO,
                    COLUMN_USER_EMAIL,
                    COLUMN_USER_USERNAME,
                    COLUMN_USER_PASSWORD,
            };

            // selection criteria
            String selection = COLUMN_USER_USERNAME + " = ?" ;

            // selection arguments
            String[] selectionArgs = {currentUsername};
            // query user table with conditions
            Cursor cursor = db.query(TABLE_USER, //Table to query
                    columns,                    //columns to return
                    selection,                  //columns for the WHERE clause
                    selectionArgs,              //The values for the WHERE clause
                    null,                       //group the rows
                    null,                       //filter by row groups
                    null);                      //The sort order

            if (cursor.moveToNext()){
                currentUser = new User(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONENO)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_USERNAME)), cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)));
            }
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table sql query
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_PHONENO + " TEXT," + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_USER_USERNAME + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        // Create tables again
        onCreate(db);
    }

    // **** CRUD (Add, Read, Update, Delete) Operations ***** //

    /**
     * This method is to create user record
     */
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

    // Get User Details based on userid
    public ArrayList<HashMap<String, String>> GetUserByUserId(int userid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT name, phone_no, email, password FROM " + TABLE_USER;
        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_USER_NAME, COLUMN_USER_PHONENO, COLUMN_USER_EMAIL, COLUMN_USER_PASSWORD}, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userid)}, null, null, null, null);
        if (cursor.moveToNext()) {
            HashMap<String, String> user = new HashMap<>();
            Log.d("Test", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)));
            user.put("name", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)));
            user.put("phoneNo", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONENO)));
            user.put("email", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)));
            user.put("password", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)));
            userList.add(user);
        }
        return userList;
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
        return  count;
    }

    // Get User id and password based on username
    public ArrayList<HashMap<String, String>>  GetUserByUsername(String username){
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
