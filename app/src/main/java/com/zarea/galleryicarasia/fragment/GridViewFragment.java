package com.zarea.galleryicarasia.fragment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.zarea.galleryicarasia.R;
import com.zarea.galleryicarasia.adapter.GalleryAdapter;
import com.zarea.galleryicarasia.utils.FooterHelper;
import com.zarea.galleryicarasia.utils.PhotoUtils;

import static android.provider.BaseColumns._ID;
import static com.zarea.galleryicarasia.database.DatabaseContract.Gallery;
import static com.zarea.galleryicarasia.database.DatabaseContract.Gallery.COLUMN_IMAGE_NAME;

/**
 * Created by zarea on 2/22/16.
 */
public class GridViewFragment extends CarAsiaFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int MAXIMUM_IMAGES_LIMIT = 21;
    public static final int INIT_LOADER = 1;
    private GridView gridView;
    private PhotoUtils photoUtils;
    private GalleryAdapter galleryAdapter;
    private FooterHelper footerHelper;
    private AdapterView.OnItemClickListener gridItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (galleryAdapter.getItemViewType(position) == 0) {
                photoUtils.createImageChooseDialog(GridViewFragment.this);
            } else {
                Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                footerHelper.setCursor(cursor);
            }
        }
    };

    public GridViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galleryAdapter = new GalleryAdapter(getActivity(), R.layout.grid_cell,
                new String[]{COLUMN_IMAGE_NAME}, new int[]{R.id.image_view_cell}, MAXIMUM_IMAGES_LIMIT);
        photoUtils = PhotoUtils.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_gridview, container, false);
        footerHelper = new FooterHelper(this, (LinearLayout) findViewById(R.id.Footer_container));
        gridView = (GridView) findViewById(R.id.FragmentGridView_grid_view);
        gridView.setAdapter(galleryAdapter);
        gridView.setOnItemClickListener(gridItemClickListener);

        getLoaderManager().initLoader(INIT_LOADER, null, this);
        return mRootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLoaderManager().destroyLoader(1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = photoUtils.onActivityResultHandler(requestCode, resultCode, data);
        if (null != bitmap) {
            String name = photoUtils.saveImage(getActivity(), bitmap);
            if (null != name) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_IMAGE_NAME, name);

                if (footerHelper.isReplace()) {
                    replaceImage(contentValues);
                } else {
                    insertImage(contentValues);
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                Gallery.CONTENT_URI,
                null, null, null, Gallery.COLUMN_PRIORITY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        galleryAdapter.changeCursor(cursor);
    }

    // Private methods

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        galleryAdapter.changeCursor(null);
    }

    private void insertImage(ContentValues contentValues) {
        contentValues.put(Gallery.COLUMN_VIEW_TYPE, Gallery.ViewTypes.IMAGE_GALLERY);
        Uri uri = getActivity().getContentResolver().insert(Gallery.CONTENT_URI,
                contentValues);
        int id = (int) ContentUris.parseId(uri);
        contentValues.put(Gallery.COLUMN_PRIORITY, id);
        getActivity().getContentResolver().update(Gallery.CONTENT_URI,
                contentValues, _ID + "=?",
                new String[]{id + ""});
    }

    private void replaceImage(ContentValues contentValues) {
        photoUtils.deleteImageBitmap(getActivity(), footerHelper.getImageName());
        getActivity().getContentResolver().update(Gallery.CONTENT_URI,
                contentValues, _ID + "=?",
                new String[]{footerHelper.getImageId() + ""});
    }

}
