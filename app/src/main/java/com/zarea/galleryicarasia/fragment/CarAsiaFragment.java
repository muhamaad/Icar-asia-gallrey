package com.zarea.galleryicarasia.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by zarea on 2/22/16.
 */
public abstract class CarAsiaFragment extends Fragment {

    protected View mRootView;

    protected View findViewById(int id) {
        return mRootView.findViewById(id);
    }
}
