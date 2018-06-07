package net.sarasarasa.lifeup;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;

import me.relex.circleindicator.CircleIndicator;

public class WelcomeActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private WelcomeFragment welcomeFragment[];


    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        indicator.setViewPager(mViewPager);


        welcomeFragment = new WelcomeFragment[3];
        for (int i = 0; i < 3; i++)
            welcomeFragment[i] = WelcomeFragment.newInstance(i + 1);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * ViewPager滑动回调方法
             * @param position 页签[0 ~ 2]
             * @param positionOffset 页百分比偏移[0F ~ 1F]
             * @param positionOffsetPixels 页像素偏移[0 ~ ViewPager的宽度]
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ArgbEvaluator evaluator = new ArgbEvaluator(); // ARGB求值器
                int evaluate = 0x00FFFFFF; // 初始默认颜色（透明白）
                if (position == 0) {
                    evaluate = (Integer) evaluator.evaluate(positionOffset, 0XFF4FC3F7, 0XFF9575CD); // 根据positionOffset和第0页~第1页的颜色转换范围取颜色值
                } else if (position == 1) {
                    evaluate = (Integer) evaluator.evaluate(positionOffset, 0XFF9575CD, 0XFFFFFFFF); // 根据positionOffset和第1页~第2页的颜色转换范围取颜色值
                } else {
                    evaluate = 0XFFFFFFFF; // 最终第3页的颜色
                }
                for (int i = 0; i < 3; i++) {
                    if (welcomeFragment[i] != null && welcomeFragment[i].rootView != null)
                        welcomeFragment[i].rootView.setBackgroundColor(evaluate);
                }
                ((View) mViewPager.getParent()).setBackgroundColor(evaluate); // 为ViewPager的父容器设置背景色
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //  StatusBarUtils.setWindowStatusBarColor(this,R.color.colorAccent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class WelcomeFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        protected boolean isCreate = false;
        View rootView;

        public WelcomeFragment() {
        }


        public static WelcomeFragment newInstance(int sectionNumber) {
            WelcomeFragment fragment = new WelcomeFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            LottieAnimationView animationViews;

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_welcome_page1, container, false);
                    animationViews = (LottieAnimationView) rootView.findViewById(R.id.animation_view);
                    animationViews.setAnimation(R.raw.done, LottieAnimationView.CacheStrategy.None);
                    animationViews.playAnimation();
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_welcome_page2, container, false);
                    animationViews = (LottieAnimationView) rootView.findViewById(R.id.animation_view);
                    animationViews.setAnimation(R.raw.trophy, LottieAnimationView.CacheStrategy.None);
                    animationViews.playAnimation();
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout.fragment_welcome_page3, container, false);
                    animationViews = (LottieAnimationView) rootView.findViewById(R.id.animation_view);
                    animationViews.setAnimation(R.raw.floating_cloud, LottieAnimationView.CacheStrategy.None);
                    animationViews.playAnimation();
                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_welcome_page1, container, false);
            }

            isCreate = true;

            return rootView;
        }


        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            LottieAnimationView animationView = null;

            if (rootView != null) {
                animationView = (LottieAnimationView) rootView.findViewById(R.id.animation_view);
                if (animationView != null)
                    if (isVisibleToUser && isCreate) {
                        animationView.playAnimation();
                    } else {
                        animationView.cancelAnimation();
                        animationView.setFrame(0);
                    }
            }
        }


    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Fragment mCurrentPrimaryItem;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            mCurrentPrimaryItem = welcomeFragment[position];
            return mCurrentPrimaryItem;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
        }


    }
}
