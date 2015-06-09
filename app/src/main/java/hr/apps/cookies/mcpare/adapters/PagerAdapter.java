package hr.apps.cookies.mcpare.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.Button;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.fragments.FragmentProsli;
import hr.apps.cookies.mcpare.fragments.FragmentSljedeci;
import hr.apps.cookies.mcpare.fragments.FragmentTrenutni;

/**
 * Created by lmita_000 on 26.5.2015..
 */
public class PagerAdapter extends FragmentPagerAdapter {

    String[] tabsTitles;
    Context context;
    SparseArray<Fragment> fragmenti;

    public PagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        tabsTitles = context.getResources().getStringArray(R.array.tabs_titles);
        this.context = context;
        fragmenti = new SparseArray<Fragment>();
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
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //return super.instantiateItem(container, position);
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragmenti.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragmenti.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getFragmentAtPosition(int position){
        return fragmenti.get(position);
    }
}
