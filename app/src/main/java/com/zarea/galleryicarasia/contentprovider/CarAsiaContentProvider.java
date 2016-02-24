package com.zarea.galleryicarasia.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.zarea.galleryicarasia.R;
import com.zarea.galleryicarasia.database.DatabaseContract;
import com.zarea.galleryicarasia.database.DatabaseHelper;


/**
 * Created by zarea on 2/22/16.
 */
public class CarAsiaContentProvider extends ContentProvider{

    private DatabaseHelper mDatabaseHelper;

    private static UriMatcher mUriMatcher;

    private static final int GET_FROM_GALLERY = 1;
    private static final int SEARCH_ADD_BUTTON_IMAGE = 2;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.Gallery.TABLE_PATH,
                GET_FROM_GALLERY);
        mUriMatcher.addURI(DatabaseContract.AUTHORITY,
                DatabaseContract.Gallery.TABLE_PATH + "/*",
                SEARCH_ADD_BUTTON_IMAGE);
    }
    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return mDatabaseHelper != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        int match = mUriMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        switch (match){
            case GET_FROM_GALLERY:
                cursor = sqLiteDatabase.query(DatabaseContract.Gallery.TABLE_NAME,
                        projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case SEARCH_ADD_BUTTON_IMAGE:
                cursor = sqLiteDatabase.query(
                        DatabaseContract.Gallery.TABLE_NAME,
                        projection,
                        DatabaseContract.Gallery.COLUMN_VIEW_TYPE+ " = ?",
                        new String[]{DatabaseContract.Gallery.getSearchViewTypeFromUri(uri)}, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.uri_not_defined)+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = mUriMatcher.match(uri);
        switch (match) {
            case GET_FROM_GALLERY:
                SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                long id = sqLiteDatabase.insert(DatabaseContract.Gallery.TABLE_NAME,null,values);
                uri = ContentUris.withAppendedId(uri,id);
                getContext().getContentResolver().notifyChange(uri,null);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.uri_not_defined)+uri);
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = mUriMatcher.match(uri);
        int deletedRowId = 0;
        switch (match) {
            case GET_FROM_GALLERY:
                SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                deletedRowId = sqLiteDatabase
                        .delete(DatabaseContract.Gallery.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.uri_not_defined)+uri);
        }

        return deletedRowId;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = mUriMatcher.match(uri);
        int updatedRows = 0;
        switch (match) {
            case GET_FROM_GALLERY:
                SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase();
                updatedRows = sqLiteDatabase.update(DatabaseContract.Gallery.TABLE_NAME,
                        values,selection,selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.uri_not_defined)+uri);
        }
        return updatedRows;
    }


}
