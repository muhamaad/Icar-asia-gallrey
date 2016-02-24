package com.zarea.galleryicarasia.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.zarea.galleryicarasia.R;
import com.zarea.galleryicarasia.database.DatabaseContract;
import com.zarea.galleryicarasia.utils.PhotoUtils;

import static com.zarea.galleryicarasia.database.DatabaseContract.Gallery.COLUMN_PRIORITY;
import static com.zarea.galleryicarasia.database.DatabaseContract.Gallery.COLUMN_VIEW_TYPE;
/**
 * Created by zarea on 2/22/16.
 */
public class GalleryAdapter extends SimpleCursorAdapter{

    private LayoutInflater inflater;
    private int layout;
    private PhotoUtils photoUtils;
    private int maxImagesLimit = 10;

    public GalleryAdapter(Context context, int layout, String[] from, int[] to, int maximumImagesLimit) {
        super(context, layout, null, from,to,0);
        this.layout = layout;
        inflater = LayoutInflater.from(context);
        photoUtils = PhotoUtils.getInstance();
        maxImagesLimit = maximumImagesLimit <= 0 ? maxImagesLimit : maximumImagesLimit;
        maxImagesLimit++;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_cell);
        cursor.getInt(cursor.getColumnIndex(COLUMN_PRIORITY));
        if(cursor.getString(cursor.getColumnIndex(COLUMN_VIEW_TYPE))
                .equals(DatabaseContract.Gallery.ViewTypes.ADD_BUTTON)){
            imageView.setImageResource(R.drawable.add_new_image);
        }else{
            imageView.setImageBitmap(photoUtils.getImageBitmap(context,cursor.getString(cursor.getColumnIndex(DatabaseContract.Gallery.COLUMN_IMAGE_NAME))));
        }
    }

    @Override
    public boolean isEnabled(int position) {
        if(getItemViewType(position) == 0 && getCount() == maxImagesLimit){
            overLoadImages();
            return false;
        }
        return super.isEnabled(position);
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        String viewType = cursor.getString(cursor.getColumnIndex(COLUMN_VIEW_TYPE));
        if( viewType.equals(DatabaseContract.Gallery.ViewTypes.ADD_BUTTON)){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    private void overLoadImages() {
        Toast.makeText(inflater.getContext(),
                inflater.getContext().getString(R.string.error_over_load), Toast.LENGTH_SHORT).show();
    }

}
