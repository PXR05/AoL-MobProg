package com.pxr.golf.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.pxr.golf.R;

public class DBHelper extends SQLiteOpenHelper {
    public static final String TABLE_USERS = "users";
    public static final String USER_ID = "id";
    public static final String USER_NAME = "name";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";
    public static final String TABLE_COURSE = "courses";
    public static final String COURSE_ID = "id";
    public static final String COURSE_NAME = "name";
    public static final String COURSE_IMAGE = "image";
    public static final String COURSE_HOLE_COUNT = "hole_count";
    public static final String COURSE_USER_ID = "user_id";
    public static final String TABLE_HISTORY = "history";
    public static final String HISTORY_ID = "id";
    public static final String HISTORY_COURSE_ID = "course_id";
    public static final String HISTORY_USER_ID = "user_id";
    public static final String HISTORY_DATE = "date";
    public static final String TABLE_HOLES = "holes";
    public static final String HOLE_ID = "id";
    public static final String HOLE_NUMBER = "number";
    public static final String HOLE_PAR = "par";
    public static final String HOLE_SCORE = "score";
    public static final String HOLE_NOTE = "note";
    public static final String HOLE_HISTORY_ID = "history_id";
    public static final String DB_NAME = "golf.db";
    public static final int DB_VERSION = 2;
    private static final String TAG = "DBHelper";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "(" +
                    USER_ID + " INTEGER PRIMARY KEY, " +
                    USER_NAME + " TEXT, " +
                    USER_EMAIL + " TEXT, " +
                    USER_PASSWORD + " TEXT)");

            db.execSQL("CREATE INDEX IF NOT EXISTS " +
                    USER_EMAIL + "_index " +
                    "ON " + TABLE_USERS + "(" + USER_EMAIL + ");");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COURSE + "(" +
                    COURSE_ID + " INTEGER PRIMARY KEY, " +
                    COURSE_NAME + " TEXT, " +
                    COURSE_IMAGE + " INTEGER, " +
                    COURSE_HOLE_COUNT + " INTEGER, " +
                    COURSE_USER_ID + " TEXT, " +
                    "FOREIGN KEY(" + COURSE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USER_ID + "));");

            db.execSQL("CREATE INDEX IF NOT EXISTS " +
                    COURSE_USER_ID + "_index " +
                    "ON " + TABLE_COURSE + "(" + COURSE_USER_ID + ");");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_HISTORY + "(" +
                    HISTORY_ID + " INTEGER PRIMARY KEY, " +
                    HISTORY_COURSE_ID + " TEXT, " +
                    HISTORY_USER_ID + " TEXT, " +
                    HISTORY_DATE + " INTEGER, " +
                    "FOREIGN KEY(" + HISTORY_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + COURSE_ID + "), " +
                    "FOREIGN KEY(" + HISTORY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USER_ID + "));");

            db.execSQL("CREATE INDEX IF NOT EXISTS " +
                    HISTORY_COURSE_ID + "_" + HISTORY_USER_ID + "_index " +
                    "ON " + TABLE_HISTORY + "(" + HISTORY_COURSE_ID + ", " + HISTORY_USER_ID + ");");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_HOLES + "(" +
                    HOLE_ID + " INTEGER PRIMARY KEY, " +
                    HOLE_NUMBER + " INTEGER, " +
                    HOLE_PAR + " INTEGER, " +
                    HOLE_SCORE + " INTEGER, " +
                    HOLE_NOTE + " TEXT, " +
                    HOLE_HISTORY_ID + " TEXT, " +
                    "FOREIGN KEY(" + HOLE_HISTORY_ID + ") REFERENCES " + TABLE_HISTORY + "(" + HISTORY_ID + "));");

            db.execSQL("CREATE INDEX IF NOT EXISTS " +
                    HOLE_HISTORY_ID + "_index " +
                    "ON " + TABLE_HOLES + "(" + HOLE_HISTORY_ID + ");");

            seedUsers(db);
            seedCourses(db);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error creating database", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HOLES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(sqLiteDatabase);
    }

    private void seedUsers(SQLiteDatabase db) {
        db.execSQL("INSERT OR IGNORE INTO " + TABLE_USERS +
                "(" + USER_ID + ", " + USER_NAME + ", " + USER_EMAIL + ", " + USER_PASSWORD + ") " +
                "VALUES ('1', 'guest', 'guest@mail.com', 'guestpwd')");
        db.execSQL("INSERT OR IGNORE INTO " + TABLE_USERS +
                "(" + USER_ID + ", " + USER_NAME + ", " + USER_EMAIL + ", " + USER_PASSWORD + ") " +
                "VALUES ('2', 'user', 'user@mail.com', 'userpwd')");
        db.execSQL("INSERT OR IGNORE INTO " + TABLE_USERS +
                "(" + USER_ID + ", " + USER_NAME + ", " + USER_EMAIL + ", " + USER_PASSWORD + ") " +
                "VALUES ('3', 'person', 'person@mail.com', 'personpwd')");
    }

    private void seedCourses(SQLiteDatabase db) {
        try {
            db.delete(TABLE_COURSE, null, null);

            for (int i = 0; i < 3; i++) {
                db.execSQL("INSERT INTO " + TABLE_COURSE +
                        "(" + COURSE_ID + ", " + COURSE_NAME + ", " + COURSE_IMAGE + ", " + COURSE_HOLE_COUNT + ") " +
                        "VALUES (" + i + ", '( " + i + " ) Senayan Golf Course', " + R.drawable.course1 + ", 18)");
                db.execSQL("INSERT INTO " + TABLE_COURSE +
                        "(" + COURSE_ID + ", " + COURSE_NAME + ", " + COURSE_IMAGE + ", " + COURSE_HOLE_COUNT + ") " +
                        "VALUES (" + (i + 3) + ", '( " + i + " ) Kedaton Golf Course', " + R.drawable.course2 + " , 18)");
            }
        } catch (Exception e) {
            Log.e(TAG, "seedCourses: Error seeding courses", e);
        }
    }
}
