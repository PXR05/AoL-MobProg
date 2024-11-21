package com.pxr.golf.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.Nullable;

import com.pxr.golf.models.Course;
import com.pxr.golf.models.Hole;
import com.pxr.golf.models.User;
import com.pxr.golf.utils.Generate;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static final String TAG = "DBManager";
    private final Context ctx;
    private SQLiteDatabase db;
    private DBHelper helper;

    public DBManager(Context ctx) {
        this.ctx = ctx;
        this.helper = new DBHelper(ctx);
    }

    public void open() {
        if (db != null && db.isOpen()) {
            return;
        }
        helper = new DBHelper(ctx);
        db = helper.getWritableDatabase();
        db.setForeignKeyConstraintsEnabled(true);
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (helper != null) {
            helper.close();
        }
    }

    public SQLiteDatabase getDB() {
        return db;
    }

    public User signIn(String email, String password) {
        if (email == null || password == null) return null;
        open();
        String sql = "SELECT " + DBHelper.USER_ID + ", " + DBHelper.USER_NAME + ", " + DBHelper.USER_EMAIL +
                " FROM " + DBHelper.TABLE_USERS +
                " WHERE " + DBHelper.USER_EMAIL + " = ? AND " + DBHelper.USER_PASSWORD + " = ?";

        Cursor c = db.rawQuery(sql, new String[]{email, password});
        User user = null;
        if (c.moveToFirst()) {
            user = new User(c.getString(0), c.getString(1), c.getString(2));
        }
        c.close();
        close();
        return user;
    }

    public User signUp(String name, String email, String password) {
        if (name == null || email == null || password == null) return null;

        open();
        String checkSql = "SELECT COUNT(*) FROM " + DBHelper.TABLE_USERS + " WHERE " + DBHelper.USER_EMAIL + " = ?";

        Cursor c = db.rawQuery(checkSql, new String[]{email});
        if (c.moveToFirst() && c.getInt(0) > 0) {
            c.close();
            return null;
        }

        String insertSql = "INSERT INTO " + DBHelper.TABLE_USERS +
                " (" + DBHelper.USER_NAME + ", " + DBHelper.USER_EMAIL + ", " + DBHelper.USER_PASSWORD + ")" +
                " VALUES (?, ?, ?)";
        db.execSQL(insertSql, new String[]{name, email, password});

        c.close();
        close();
        return signIn(email, password);
    }

    public List<Hole> getHoles(String cid, String uid) {
        if (cid == null || uid == null) return null;
        Log.d(TAG, "getHoles: querying holes with cid: " + cid + " and uid: " + uid);

        open();
        String sql = "SELECT " +
                DBHelper.HOLE_ID + ", " +
                DBHelper.HOLE_NUMBER + ", " +
                DBHelper.HOLE_PAR + ", " +
                DBHelper.HOLE_SCORE + ", " +
                DBHelper.HOLE_NOTE +
                " FROM " + DBHelper.TABLE_HOLES +
                " WHERE " + DBHelper.HOLE_COURSE_ID + " = ? AND " + DBHelper.HOLE_USER_ID + " = ?";

        Cursor c = db.rawQuery(sql, new String[]{cid, uid});
        if (c.getCount() == 0) {
            Log.d(TAG, "getHoles: no result");
            return null;
        }
        c.moveToFirst();

        List<Hole> holes = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++) {
            Log.d(TAG, "getHoles: hole " + i);
            holes.add(new Hole(c.getString(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getString(4)));
            c.moveToNext();
        }

        Log.d(TAG, "getHoles: " + holes);
        c.close();
        close();
        return holes;
    }

    public void saveHoles(List<Hole> holes, String cid, String uid) {
        Log.d(TAG, "saveHoles: saving holes for cid: " + cid + " and uid: " + uid);
        open();
        db.beginTransaction();
        try {
            String sql = "INSERT OR REPLACE INTO " + DBHelper.TABLE_HOLES + "("
                    + DBHelper.HOLE_ID + ", "
                    + DBHelper.HOLE_NUMBER + ", "
                    + DBHelper.HOLE_PAR + ", "
                    + DBHelper.HOLE_SCORE + ", "
                    + DBHelper.HOLE_NOTE + ", "
                    + DBHelper.HOLE_COURSE_ID + ", "
                    + DBHelper.HOLE_USER_ID + ") "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            SQLiteStatement stmt = db.compileStatement(sql);

            int i = 0;
            for (Hole hole : holes) {
                Log.d(TAG, "saveHoles: hole " + i++);
                stmt.bindString(1, hole.getId());
                stmt.bindLong(2, hole.getNumber());
                stmt.bindLong(3, hole.getPar());
                stmt.bindLong(4, hole.getScore());
                stmt.bindString(5, hole.getNote());
                stmt.bindString(6, cid);
                stmt.bindString(7, uid);
                stmt.execute();
                stmt.clearBindings();
            }

            stmt.close();

            Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DBHelper.TABLE_HOLES, null);
            c.moveToFirst();
            Log.d(TAG, "saveHoles: " + c.getInt(0) + " holes in database");
            c.close();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            close();
        }
    }

    public List<Course> getCourses(@Nullable String uid) {
        open();
        String sql = "SELECT " +
                DBHelper.COURSE_ID + ", " +
                DBHelper.COURSE_NAME + ", " +
                DBHelper.COURSE_IMAGE + ", " +
                DBHelper.COURSE_HOLE_COUNT +
                " FROM " + DBHelper.TABLE_COURSE +
                (uid != null ? " WHERE " + DBHelper.COURSE_USER_ID + " = ? OR " + DBHelper.COURSE_USER_ID + " IS NULL"
                        : " WHERE " + DBHelper.COURSE_USER_ID + " IS NULL");

        Cursor c = db.rawQuery(sql, uid != null ? new String[]{uid} : null);

        Log.d(TAG, "getCourses: " + c.getCount() + " courses");
        if (!c.moveToFirst()) return new ArrayList<>();

        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++) {
            courses.add(new Course(c.getString(0), c.getString(1), c.getInt(2), c.getInt(3)));
            c.moveToNext();
        }

        Log.d(TAG, "getCourses: " + courses);
        c.close();
        close();
        return courses;
    }

    public Course getCourse(String cid, String uid) {
        open();
        String sql = "SELECT " +
                DBHelper.COURSE_ID + ", " +
                DBHelper.COURSE_NAME + ", " +
                DBHelper.COURSE_IMAGE + ", " +
                DBHelper.COURSE_HOLE_COUNT +
                " FROM " + DBHelper.TABLE_COURSE +
                " WHERE " + DBHelper.COURSE_ID + " = ? OR " + DBHelper.COURSE_USER_ID + " = ?";

        Cursor c = db.rawQuery(sql, new String[]{cid, uid});
        if (c.getCount() == 0) {
            c.close();
            close();
            return null;
        }
        c.moveToFirst();

        List<Hole> holes = getHoles(cid, uid);
        if (holes == null || holes.isEmpty()) {
            holes = Generate.holes(c.getInt(3), 72);
        }
        Course course = new Course(
                c.getString(0),
                c.getString(1),
                c.getInt(2),
                holes
        );

        c.close();
        close();
        return course;
    }

    public List<Course> getHistory(String uid) {
        if (uid == null) return null;

        open();
        String sql = "SELECT " +
                "c. " + DBHelper.COURSE_ID + ", " +
                "c. " + DBHelper.COURSE_NAME + ", " +
                "c. " + DBHelper.COURSE_IMAGE + ", " +
                "c. " + DBHelper.COURSE_HOLE_COUNT + ", " +
                "h." + DBHelper.HISTORY_DATE +
                " FROM " + DBHelper.TABLE_HISTORY + " h" +
                " JOIN " + DBHelper.TABLE_COURSE + " c" +
                " ON c." + DBHelper.COURSE_ID + " = h." + DBHelper.HISTORY_COURSE_ID +
                " WHERE h." + DBHelper.HISTORY_USER_ID + " = ?";

        Cursor c = db.rawQuery(sql, new String[]{uid});
        Log.d(TAG, "getHistory: found " + c.getCount() + " history");
        if (c.getCount() == 0) {
            c.close();
            return null;
        }

        c.moveToFirst();
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++) {
            Log.d(TAG, "getHistory: history " + i);
            List<Hole> holes = getHoles(c.getString(0), uid);
            if (holes == null) holes = Generate.holes(c.getInt(3), 72);

            Course course = new Course(c.getString(0), c.getString(1), c.getInt(2), holes);
            course.setDate(Instant.ofEpochMilli(c.getLong(4)).atZone(ZoneId.systemDefault()).toLocalDate());
            courses.add(course);
            c.moveToNext();
        }

        Log.d(TAG, "getHistory: " + courses);
        c.close();
        close();
        return courses;
    }

    public void saveHistory(String cid, String uid) {
        if (cid == null || uid == null) return;
        Log.d(TAG, "addHistory: saving for cid: " + cid + ", uid: " + uid);

        open();
        String hid = null;
        String checkSql = "SELECT " + DBHelper.HISTORY_ID + " FROM " + DBHelper.TABLE_HISTORY +
                " WHERE " + DBHelper.HISTORY_COURSE_ID + " = ? AND " + DBHelper.HISTORY_USER_ID + " = ?";

        Cursor c = db.rawQuery(checkSql, new String[]{cid, uid});
        if (c.getCount() > 0) {
            c.moveToFirst();
            hid = c.getString(0);
            c.close();
        }
        Log.d(TAG, "addHistory: hid - " + hid);

        if (hid == null) {
            Log.d(TAG, "addHistory: new history");
            String sql = "INSERT INTO " + DBHelper.TABLE_HISTORY + "(" +
                    DBHelper.HISTORY_COURSE_ID + ", " +
                    DBHelper.HISTORY_USER_ID + ", " +
                    DBHelper.HISTORY_DATE + ") " +
                    "VALUES (?, ?, ?)";
            db.execSQL(sql, new String[]{cid, uid, String.valueOf(System.currentTimeMillis())});
        } else {
            Log.d(TAG, "addHistory: existing history");
            String sql = "UPDATE " + DBHelper.TABLE_HISTORY +
                    " SET " + DBHelper.HISTORY_DATE + " = ? " +
                    " WHERE " + DBHelper.HISTORY_ID + " = ?";
            db.execSQL(sql, new String[]{String.valueOf(System.currentTimeMillis()), hid});
        }
        close();
        Log.d(TAG, "addHistory: history saved");
    }
}
