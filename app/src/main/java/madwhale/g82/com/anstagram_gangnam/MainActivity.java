package madwhale.g82.com.anstagram_gangnam;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /**
     * TODO
     * MainActivity
     *  ViewPager (below), TabLayout (above)
     *
     *  2 Fragment
     *  TimelineFragment, EmptyFragment
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl_tabs);

        Fragment[] arrFragments = new Fragment[3];
        arrFragments[0] = new TimelineFragment();
        arrFragments[1] = new EmptyFragment();
        arrFragments[2] = new FriendsFragment();

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), arrFragments);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    class MainPagerAdapter extends FragmentPagerAdapter {

        Fragment[] arrFragments;

        public MainPagerAdapter(FragmentManager fm, Fragment[] arrFragments) {
            super(fm);
            this.arrFragments = arrFragments;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Timeline";
                case 1:
                    return "Empty";
                case 2:
                    return "Friends";
                default:
                    return "";
            }
        }

        @Override
        public Fragment getItem(int position) {
            return arrFragments[position];
        }

        @Override
        public int getCount() {
            return arrFragments.length;
        }
    }

}
