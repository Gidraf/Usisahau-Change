package com.glab.usisahauchange;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.glab.usisahauchange.fragments.FirstIntroFragment;
import com.glab.usisahauchange.fragments.SecondIntroFragment;
import com.glab.usisahauchange.fragments.ThirdFragment;

public class MainActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(new FirstIntroFragment());
        addSlide(new SecondIntroFragment());
        addSlide(new ThirdFragment());
        setDepthAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(MainActivity.this, Home.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(MainActivity.this, Home.class));
        finish();
    }
}
