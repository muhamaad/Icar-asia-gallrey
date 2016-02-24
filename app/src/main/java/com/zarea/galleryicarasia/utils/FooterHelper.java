package com.zarea.galleryicarasia.utils;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zarea.galleryicarasia.R;
import com.zarea.galleryicarasia.database.DatabaseContract;
import com.zarea.galleryicarasia.fragment.GridViewFragment;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.zarea.galleryicarasia.database.DatabaseContract.Gallery.COLUMN_IMAGE_NAME;
import static com.zarea.galleryicarasia.database.DatabaseContract.Gallery.COLUMN_PRIORITY;

/**
* Created by zarea on 2/22/16.
*/
public class FooterHelper {

    private GridViewFragment gridViewFragment;
    private LinearLayout linearLayout;
    private Button deleteButton;
    private Button makeAsMainButton;
    private Button editButton;
    private boolean isReplace;
    private String imageName;
    private int imageId;
    private int priority;

    public FooterHelper(GridViewFragment gridViewFragment, LinearLayout linearLayout) {
        this.gridViewFragment = gridViewFragment;
        this.linearLayout = linearLayout;
        deleteButton = (Button) linearLayout.findViewById(R.id.Footer_delete_button);
        makeAsMainButton = (Button) linearLayout.findViewById(R.id.Footer_make_as_main_button);
        editButton = (Button) linearLayout.findViewById(R.id.Footer_edit_button);

        deleteButton.setOnClickListener(mDeleteOnClickListener);
        makeAsMainButton.setOnClickListener(mMakeAsMainOnClickListener);
        editButton.setOnClickListener(mEditButtonOnClickListener);
    }

    public int getImageId() {
        return imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public boolean isReplace() {
        return isReplace;
    }

    public void setReplace(boolean isReplace) {
        this.isReplace = isReplace;
    }

    public void setCursor(Cursor cursor) {
        if(cursor != null) {
            imageName = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_NAME));
            imageId = cursor.getInt(cursor.getColumnIndex(_ID));
            priority = cursor.getInt(cursor.getColumnIndex(COLUMN_PRIORITY));
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    // Private methods

    private View.OnClickListener mDeleteOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gridViewFragment.getActivity().getContentResolver().delete(DatabaseContract.Gallery.CONTENT_URI,
                    DatabaseContract.Gallery.COLUMN_IMAGE_NAME+" = ?",new String[]{imageName});
            setCursor(null);
            setReplace(false);
            linearLayout.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener mMakeAsMainOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Cursor cursor = gridViewFragment.getActivity().getContentResolver().query(DatabaseContract.Gallery.CONTENT_URI,
                    null, null, null, _ID + " DESC limit 1");

            Cursor maximumPriorityCursor = gridViewFragment.getActivity().getContentResolver().query(DatabaseContract.Gallery.CONTENT_URI,
                    null,null,null,COLUMN_PRIORITY+" DESC limit 1");

            cursor.moveToFirst();
            maximumPriorityCursor.moveToFirst();

            int maxPriority = maximumPriorityCursor.getInt(cursor.getColumnIndex(COLUMN_PRIORITY));
            int maximumPriorityImageId = maximumPriorityCursor.getInt(cursor.getColumnIndex(_ID));

            ContentValues currentMaximumPriorityImage = new ContentValues();
            currentMaximumPriorityImage.put(COLUMN_PRIORITY, priority);

            ContentValues currentSelectedImage = new ContentValues();
            currentSelectedImage.put(COLUMN_PRIORITY, maxPriority);
            swapImagesLocations(maximumPriorityImageId, currentMaximumPriorityImage, currentSelectedImage);
        }
    };

    private void swapImagesLocations(int maximumPriorityImageId, ContentValues currentMaximumPriority, ContentValues currentSelecctedImage) {
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        contentProviderOperations.add(ContentProviderOperation
                .newUpdate(DatabaseContract.Gallery.CONTENT_URI)
                .withValues(currentMaximumPriority)
                .withSelection(_ID + "=?", new String[]{maximumPriorityImageId + ""}).build());
        contentProviderOperations.add(ContentProviderOperation
                .newUpdate(DatabaseContract.Gallery.CONTENT_URI)
                .withValues(currentSelecctedImage)
                .withSelection(_ID + "=?", new String[]{imageId + ""}).build());
        try {
            gridViewFragment.getActivity().getContentResolver().applyBatch(DatabaseContract.AUTHORITY,contentProviderOperations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener mEditButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setReplace(true);
            PhotoUtils.getInstance().createImageChooseDialog(gridViewFragment,
                    new PhotoUtils.DialogState() {
                        @Override
                        public void onCancel() {
                            setReplace(false);
                        }
                    });
        }
    };

}
