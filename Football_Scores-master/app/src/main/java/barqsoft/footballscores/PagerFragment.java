package barqsoft.footballscores;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment
{
    public static final int NUM_PAGES = 5;
    public ViewPager mPagerHandler;
    private myPageAdapter mPagerAdapter;
    private int mCurrentPage = myPageAdapter.POSITION_TODAY;
    private int mAppBarOffset = 0;
    TabLayout tabLayout;

    private MainScreenFragment[] viewFragments = new MainScreenFragment[5];
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        mPagerAdapter = new myPageAdapter(getActivity(), getChildFragmentManager());
        tabLayout.setTabsFromPagerAdapter(mPagerAdapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                mPagerHandler.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mPagerHandler.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mPagerHandler.setOffscreenPageLimit(myPageAdapter.PAGE_SIZE);
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(mCurrentPage);
        return rootView;
    }
    private class myPageAdapter extends FragmentStatePagerAdapter
    {
        public static final int POSITION_TODAY = 2;
        public static final int PAGE_SIZE = 5;

        private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        private final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("EEEE", Locale.US);

        private final Context mContext;
        private Map<Integer, MainScreenFragment> mPageReferenceMap = new HashMap<>();

        public myPageAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.roll(Calendar.DAY_OF_MONTH, position - POSITION_TODAY);
            MainScreenFragment fragment = MainScreenFragment.newInstance(DATE_FORMAT.format(calendar.getTime()));


            mPageReferenceMap.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return PAGE_SIZE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.roll(Calendar.DAY_OF_WEEK, position - POSITION_TODAY);
            return getDayName(mContext, calendar.getTimeInMillis());
        }

        public MainScreenFragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }

        public void onDestroy() {
            mPageReferenceMap.clear();
        }

        public String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if (julianDay == currentJulianDay + 1) {
                return context.getString(R.string.tomorrow);
            } else if (julianDay == currentJulianDay - 1) {
                return context.getString(R.string.yesterday);
            } else {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                return DAY_FORMAT.format(dateInMillis);
            }
        }
    }

}
