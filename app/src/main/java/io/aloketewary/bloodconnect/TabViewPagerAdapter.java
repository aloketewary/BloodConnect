package io.aloketewary.bloodconnect;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.aloketewary.bloodconnect.fragment.pages.ChatFragment;
import io.aloketewary.bloodconnect.fragment.pages.FriendsFragment;
import io.aloketewary.bloodconnect.fragment.pages.RequestFragment;

/**
 * Created by AlokeT on 2/3/2018.
 */

class TabViewPagerAdapter extends FragmentPagerAdapter {

    public TabViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "REQUEST";
            case 1:
                return "CHATS";
            case 2:
                return "FRIENDS";
            default:
                return null;
        }

    }
}
