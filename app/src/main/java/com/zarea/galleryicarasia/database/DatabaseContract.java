package com.zarea.galleryicarasia.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by zarea on 2/22/16.
 */
public class DatabaseContract {

    public static final String AUTHORITY = "com.zarea.icarasia";
    public static final Uri BASE_URI = Uri.parse("content://"+AUTHORITY);

    /**
     * Gallery class containing meta data of our Gallery table.
     */
    public static final class Gallery implements BaseColumns{

        public static final String TABLE_PATH = "gallery";

        public static final Uri CONTENT_URI = BASE_URI
                .buildUpon()
                .appendPath(TABLE_PATH)
                .build();

        // Table Constants
        public static final String TABLE_NAME = "Gallery";
        public static final String COLUMN_IMAGE_NAME = "image_name";
        public static final String COLUMN_VIEW_TYPE = "view_type";
        public static final String COLUMN_PRIORITY = "priority";

        // Table queries
        public static final String SQL_CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("
                +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_VIEW_TYPE +" TEXT NOT NULL,"
                + COLUMN_PRIORITY +" INTEGER,"
                + COLUMN_IMAGE_NAME +" TEXT NOT NULL);";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

        // Helper methods
        public static Uri buildUriForSearchViewType(String viewType){
            return CONTENT_URI.buildUpon().appendPath(viewType).build();
        }

        public static String getSearchViewTypeFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static interface ViewTypes{
            String IMAGE_GALLERY = "image";
            String ADD_BUTTON = "add_button";
        }
    }
}
