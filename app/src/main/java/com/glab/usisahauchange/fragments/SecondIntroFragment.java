package com.glab.usisahauchange.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.glab.usisahauchange.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondIntroFragment extends Fragment {


    public SecondIntroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewDataBinding rootbindig = DataBindingUtil.inflate(getLayoutInflater(),R.layout.fragment_second_intro,container, false);
        ImageView matIcon = rootbindig.getRoot().findViewById(R.id.second_mat);
        ImageView sadFace = rootbindig.getRoot().findViewById(R.id.sad_face);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap blurTemplate = BitmapFactory.decodeResource(getResources(), R.drawable.side_mat, options);
        Glide.with(getContext()).load(blurTemplate).into(matIcon);
        Glide.with(getContext()).asGif().load(R.drawable.sad).into(sadFace);
        return rootbindig.getRoot();
    }

}
