package hr.apps.cookies.mcpare.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.fragments.FragmentProsli;
import hr.apps.cookies.mcpare.fragments.FragmentSljedeci;
import hr.apps.cookies.mcpare.fragments.FragmentTrenutni;

/**
 * Created by lmita_000 on 26.5.2015..
 */
public class PagerAdapter extends FragmentPagerAdapter {

    String[] tabsTitles;

    public PagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        tabsTitles = context.getResources().getStringArray(R.array.tabs_titles);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new FragmentProsli();
                break;
            case 1:
                fragment = new FragmentTrenutni();
                break;
            case 2:
                fragment = new FragmentSljedeci();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //return super.getPageTitle(position);
        return tabsTitles[position];
    }
}
