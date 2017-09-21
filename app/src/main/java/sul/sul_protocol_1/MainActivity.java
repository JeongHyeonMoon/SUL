package sul.sul_protocol_1;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import sul.sul_protocol_1.Fragment1.Fragment1;
import sul.sul_protocol_1.Fragment2.Fragment2;
import sul.sul_protocol_1.Fragment3.Fragment3;
import sul.sul_protocol_1.Fragment4.Fragment4;

public class MainActivity extends FragmentActivity {
    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;
    Fragment4 fragment4;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        Intent service = new Intent(this, MyService.class);
        startService(service);
*/
        backPressCloseHandler = new BackPressCloseHandler(this);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setIcon(R.drawable.p_list_2));
        tabs.addTab(tabs.newTab().setIcon(R.drawable.p_map));
        tabs.addTab(tabs.newTab().setIcon(R.drawable.p_pro));
        tabs.addTab(tabs.newTab().setIcon(R.drawable.p_per));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("MainActivity", "�좏깮�� �� : " + position);

                Fragment selected = null;
                if (position == 0) {
                    selected = fragment1;
                } else if (position == 1) {
                    selected = fragment2;
                } else if (position == 2) {
                    selected = fragment3;
                }else if (position == 3) {
                    selected = fragment4;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    } // end onCreate

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }
}
