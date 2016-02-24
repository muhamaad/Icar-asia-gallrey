package com.zarea.galleryicarasia.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;

import com.zarea.galleryicarasia.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zarea on 2/22/16.
 * This class is used to maintain our Photos related tasks like save,delete ...
 */
public class PhotoUtils {

    private static PhotoUtils mPhotoUtils = new PhotoUtils();
    public static PhotoUtils getInstance(){
        return mPhotoUtils;
    }

    private ArrayAdapter<String> mAdapter;
    private Uri mImageCaptureUri;
    private File mTemporaryImageFilePlaceHolder;
    private Bitmap mImagePostBitmap;
    private Activity mActivityReference;

    // Constants
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    // Null handler
    private DialogState mDialogStateListener = new DialogState() {
        @Override
        public void onCancel() {
        }
    };

    // Callback
    public static interface DialogState{
        void onCancel();
    }


    public void createImageChooseDialog(final Fragment fragment, DialogState dialogState) {
        mDialogStateListener = dialogState;
        createImageChooseDialog(fragment);
    }

    public void createImageChooseDialog(final Fragment fragment){
        mActivityReference = fragment.getActivity();
        mAdapter = new ArrayAdapter<>(mActivityReference,
                android.R.layout.select_dialog_item,
                mActivityReference.getResources().getStringArray(R.array.capture_image_items));
        mTemporaryImageFilePlaceHolder = new File(Environment.getExternalStorageDirectory(),
                "imagePlaceHolder.jpg");

        AlertDialog.Builder builder     = new AlertDialog.Builder(mActivityReference);
        builder.setTitle(mActivityReference.getString(R.string.select_image));
        builder.setAdapter( mAdapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                if (item == 0) {
                    Intent intent    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mImageCaptureUri = Uri.fromFile(mTemporaryImageFilePlaceHolder);

                    try {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);

                        fragment.startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    fragment.startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );

        final AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(mOnCancelListener);
        dialog.show();

    }

    public String saveImage(Context context, Bitmap b){
        String name="image"+getNameAsTimeStamp()+".jpg";
        FileOutputStream out;
        try {
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getImageBitmap(Context context,String name){
        try{
            FileInputStream fis = context.openFileInput(name);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        }
        catch(Exception e){
        }
        return null;
    }

    public boolean deleteImageBitmap(Context context, String name){
        return context.deleteFile(name);
    }

    public Bitmap onActivityResultHandler(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return null;

        String path     = "";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;

        if (requestCode == PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
            path = getRealPathFromURI(mImageCaptureUri);

            if (path == null)
                path = mImageCaptureUri.getPath();
            if (path != null)
                mImagePostBitmap = BitmapFactory.decodeFile(path, options);
        } else {
            path    = mImageCaptureUri.getPath();
            mImagePostBitmap = BitmapFactory.decodeFile(path,options);
        }
        return mImagePostBitmap;
    }

    // Private methods
    private DialogInterface.OnCancelListener mOnCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            mDialogStateListener.onCancel();
        }
    };

    private String getRealPathFromURI(Uri contentUri) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = mActivityReference.managedQuery(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private String getNameAsTimeStamp(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        return dateFormat.format(new Date());
    }


}
