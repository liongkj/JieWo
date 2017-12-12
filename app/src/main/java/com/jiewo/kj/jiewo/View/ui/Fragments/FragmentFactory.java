package com.jiewo.kj.jiewo.View.ui.Fragments;

import android.support.v4.app.Fragment;
import android.content.Context;


/**
 * Created by khaij on 12/12/2017.
 */

public class FragmentFactory {
    /**
     * The Singleton Instance
     */
    private static FragmentFactory sFragmentFactory;

    /**
     * Private constructor
     */
    private FragmentFactory() {
    }

    /**
     * @return The FragmentFactory Singleton
     */
    public synchronized static FragmentFactory get() {
        if (null == sFragmentFactory) {
            sFragmentFactory = new FragmentFactory();
        }
        return sFragmentFactory;
    }

    public Fragment getFragment(final Context context, final int selection) {
        switch (selection) {
            case 0:
                return new MainFragment();
            case 1:
                return new ItemFragment();

        }
        return new Fragment();
    }
}
