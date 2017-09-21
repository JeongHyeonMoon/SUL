package sul.sul_protocol_1;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import sul.sul_protocol_1.Fragment1.Fragment1;
import sul.sul_protocol_1.Fragment2.Fragment2;
import sul.sul_protocol_1.Fragment3.Fragment3;
import sul.sul_protocol_1.Fragment4.Fragment4;

/**
 * Created by user on 2016-06-10.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 4;

    private String tabtitles[] = new String[] {"Tab1","Tab2","Tab3","Tab4"};
    Context context;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Fragment1 fragmenttab1 = new Fragment1();
                return fragmenttab1;
            case 1:
                Fragment2 fragmenttab2 = new Fragment2();
                return fragmenttab2;
            case 2:
                Fragment3 fragmenttab3 = new Fragment3();
                return fragmenttab3;
            case 3:
                Fragment4 fragmenttab4 = new Fragment4();
                return fragmenttab4;
        }
        return null;
    }


}
