package com.example.singbike.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.singbike.Fragments.IntroSlideFragment;

/* The pager adapter which provides the pages to view */
public class SlidePagerAdapter extends FragmentStateAdapter {

    private int numPages;

    public SlidePagerAdapter (FragmentActivity activity, int numPages) {
        super(activity);
        this.numPages = numPages;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new IntroSlideFragment(position);
    }

    @Override
    public int getItemCount() {
        return this.numPages;
    }
}
