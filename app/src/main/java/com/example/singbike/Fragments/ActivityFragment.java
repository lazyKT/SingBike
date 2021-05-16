package com.example.singbike.Fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;

import androidx.fragment.app.Fragment;

import com.example.singbike.R;

public class ActivityFragment extends Fragment {

    public ActivityFragment () {
        super (R.layout.fragment_activity);
    }

    @Override
    public void onCreate (Bundle savedInstaceState) {
        super.onCreate (savedInstaceState);

        TransitionInflater inflater = TransitionInflater.from (requireActivity());
        setEnterTransition (inflater.inflateTransition (R.transition.slide_right));
    }
}
