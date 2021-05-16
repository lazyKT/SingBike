package com.example.singbike.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.singbike.R;

public class IntroSlideFragment extends Fragment {

    private int slideNum;

    public IntroSlideFragment (int slideNum) {
        this.slideNum = slideNum;
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreateView(inflater, parent, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_intro_slide, parent, false);

        final TextView slideTtitle = v.findViewById (R.id.introSlideTitle);
        final TextView slideDescription = v.findViewById (R.id.descriptionIntro);
        final ImageView slideImageView = v.findViewById (R.id.introSlideImageView);

        switch (this.slideNum) {
            case 0:
                slideTtitle.setText(getResources().getString(R.string.slide1Title));
                slideDescription.setText(getResources().getString(R.string.slide1Description));
                break;
            case 1:
                slideTtitle.setText(getResources().getString(R.string.slide2Title));
                slideDescription.setText(getResources().getString(R.string.slide2Description));
                break;
            case 2:
                slideTtitle.setText(getResources().getString(R.string.slide3Title));
                slideDescription.setText(getResources().getString(R.string.slide3Description));
                break;
            default:
                slideTtitle.setText(getResources().getString(R.string.introBtn));
                slideDescription.setText(getResources().getString(R.string.slide1Description));
        }

        return  v;
    }

}
