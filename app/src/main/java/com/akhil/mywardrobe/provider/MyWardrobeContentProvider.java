package com.akhil.mywardrobe.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.akhil.mywardrobe.database.MyWardrobeDatabase;
import com.akhil.mywardrobe.database.MyWardrobeDatabase.Table;

public class MyWardrobeContentProvider extends ContentProvider {

    public MyWardrobeContentProvider() {
    }

    private MyWardrobeDatabase mAppDbHelper;

    private static final String AUTHORITY = "com.akhil.mywardrobe.provider";

    public static final Uri CONTENT_URI_FAVOURITE   = Uri.parse("content://" + AUTHORITY + "/" + Table.FAVOURITE);
    public static final Uri CONTENT_URI_SHIRTS      = Uri.parse("content://" + AUTHORITY + "/" + Table.SHIRTS);
    public static final Uri CONTENT_URI_PANTS       = Uri.parse("content://" + AUTHORITY + "/" + Table.PANTS);


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int FAVOURITE_URI = 10;
    private static final int SHIRTS_URI = 20;
    private static final int PANTS_URI = 30;

    static {
        sURIMatcher.addURI(AUTHORITY, Table.FAVOURITE, FAVOURITE_URI);
        sURIMatcher.addURI(AUTHORITY, Table.SHIRTS, SHIRTS_URI);
        sURIMatcher.addURI(AUTHORITY, Table.PANTS, PANTS_URI);
    }

    @Override
    public boolean onCreate() {
        mAppDbHelper = MyWardrobeDatabase.getInstance(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mAppDbHelper.getReadableDatabase();
        Cursor cursor = null;
        // Check if the caller has requested a column which does not exists

        // Set the table
        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case FAVOURITE_URI:
                queryBuilder.setTables(Table.FAVOURITE);
                break;
            case SHIRTS_URI:
                queryBuilder.setTables(Table.SHIRTS);
                break;
            case PANTS_URI:
                queryBuilder.setTables(Table.PANTS);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, " RANDOM()");

        // Make sure that potential listeners are getting notified
        if (cursor != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        long rowId = -1;
        Uri returnUri = null;
        switch (uriType) {
            case FAVOURITE_URI:
                rowId = mAppDbHelper.insertQuery(values, Table.FAVOURITE);
                returnUri = Uri.parse(Table.FAVOURITE + "/" + rowId);
                break;
            case SHIRTS_URI:
                rowId = mAppDbHelper.insertQuery(values, Table.SHIRTS);
                returnUri = Uri.parse(Table.SHIRTS + "/" + rowId);
                break;
            case PANTS_URI:
                rowId = mAppDbHelper.insertQuery(values, Table.PANTS);
                returnUri = Uri.parse(Table.PANTS + "/" + rowId);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsAffected = 0;
        switch (uriType) {
            case FAVOURITE_URI:
                rowsAffected = mAppDbHelper.delete(Table.FAVOURITE, selection, selectionArgs);
                break;
            case SHIRTS_URI:
                rowsAffected = mAppDbHelper.delete(Table.SHIRTS, selection, selectionArgs);
                break;
            case PANTS_URI:
                rowsAffected = mAppDbHelper.delete(Table.PANTS, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsAffected = -1;
        switch (uriType) {
            case FAVOURITE_URI:
                rowsAffected = mAppDbHelper.delete(Table.FAVOURITE, selection, selectionArgs);
                break;
            case SHIRTS_URI:
                rowsAffected = mAppDbHelper.delete(Table.SHIRTS, selection, selectionArgs);
                break;
            case PANTS_URI:
                rowsAffected = mAppDbHelper.delete(Table.PANTS, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

}
