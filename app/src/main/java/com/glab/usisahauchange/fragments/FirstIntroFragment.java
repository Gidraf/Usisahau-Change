package com.glab.usisahauchange.fragments;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.model.StreamEncoder;
import com.glab.usisahauchange.R;

import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstIntroFragment extends Fragment {


    public FirstIntroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewDataBinding rootbindig = DataBindingUtil.inflate(getLayoutInflater(),R.layout.fragment_first_intro,container, false);
        ImageView matIcon = rootbindig.getRoot().findViewById(R.id.mat_icon);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap blurTemplate = BitmapFactory.decodeResource(getResources(), R.drawable.front_mat, options);
        Glide.with(getContext()).load(blurTemplate).into(matIcon);

        return rootbindig.getRoot();
    }

}
