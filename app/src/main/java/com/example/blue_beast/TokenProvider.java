package com.example.blue_beast;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TokenProvider extends ContentProvider {

    private static final String DB_NAME = "token_database";
    private static final String DB_TABLE = "token_table";
    private static final int DB_VER = 1;

    public static final String AUTHORITY = "in.softpaper.blue_beast.token_provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_TABLE);

    private static SQLiteDatabase myDb;

    static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, DB_TABLE, 1);
    }

    @Override
    public boolean onCreate() {
        TokenDatabase tokenDatabase = new TokenDatabase(getContext(), DB_NAME,
                null, DB_VER);

        myDb = tokenDatabase.getWritableDatabase();

        return myDb != null;
    }

    public static SQLiteDatabase getDbInstance() {
        return myDb;
    }

    public static String getDbTable() {
        return DB_TABLE;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s,
                        @Nullable String[] strings1, @Nullable String s1) {
        SQLiteQueryBuilder myQuery = new SQLiteQueryBuilder();
        myQuery.setTables(DB_TABLE);

        Cursor cursor = myQuery.query(myDb, null, null, null, null,
                null, "_id");
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long row = myDb.insert(DB_TABLE, null, contentValues);

        if(row > 0) {
            uri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
                      @Nullable String[] strings) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case 1:
                count = myDb.update(DB_TABLE, contentValues, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private class TokenDatabase extends SQLiteOpenHelper {

        public TokenDatabase(@Nullable Context context, @Nullable String name,
                             @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table " + DB_TABLE +
                    " (_id integer primary key autoincrement, token text, role text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("drop table if exists " + DB_TABLE);
        }
    }
}
