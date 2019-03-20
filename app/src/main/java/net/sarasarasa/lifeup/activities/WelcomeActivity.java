package net.sarasarasa.lifeup.activities;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import net.sarasarasa.lifeup.R;
import net.sarasarasa.lifeup.utils.DensityUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import me.relex.circleindicator.CircleIndicator;


public class WelcomeActivity extends AppCompatActivity {

    private static final int PAGE_NUMBER = 4;
    private static final int PAGE_COLOR[] =
            {0XFF4FC3F7, 0XFFE4542F, 0XFF9575CD, 0XFFFFFFFF};


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private WelcomeFragment arrWelcomeFragment[];
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_TranslucentTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        //设置FargmentManager、ViewPager和CircleIndicator
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        CircleIndicator indicator = findViewById(R.id.indicator);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        indicator.setViewPager(mViewPager);


        //初始化生成4个fragments，并且保存起来
        arrWelcomeFragment = new WelcomeFragment[PAGE_NUMBER];
        for (int i = 0; i < PAGE_NUMBER; i++)
            arrWelcomeFragment[i] = WelcomeFragment.newInstance(i);

        //ViewPager添加onPageChangeListener，实现颜色渐变效果
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * ViewPager滑动回调方法
             * @param position 页签[0 ~ 3
             * @param positionOffset 页百分比偏移[0F ~ 1F]
             * @param positionOffsetPixels 页像素偏移[0 ~ ViewPager的宽度]
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ArgbEvaluator evaluator = new ArgbEvaluator(); // ARGB求值器
                int evaluate = 0x00FFFFFF; // 初始默认颜色（透明白）
                if (position == 0) {
                    evaluate = (Integer) evaluator.evaluate(positionOffset, PAGE_COLOR[0], PAGE_COLOR[1]); // 根据positionOffset和第0页~第1页的颜色转换范围取颜色值
                } else if (position == 1) {
                    evaluate = (Integer) evaluator.evaluate(positionOffset, PAGE_COLOR[1], PAGE_COLOR[2]); // 根据positionOffset和第1页~第2页的颜色转换范围取颜色值
                } else if (position == 2) {
                    evaluate = (Integer) evaluator.evaluate(positionOffset, PAGE_COLOR[2], PAGE_COLOR[3]); // 根据positionOffset和第2页~第3页的颜色转换范围取颜色值
                } else {
                    evaluate = PAGE_COLOR[3]; // 最终第3页的颜色
                }

                //找到Fargment元素设置背景色
                for (int i = 0; i < PAGE_NUMBER; i++) {
                    if (arrWelcomeFragment[i] != null && arrWelcomeFragment[i].getView() != null) {
                        arrWelcomeFragment[i].getView().setBackgroundColor(evaluate);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 按钮事件响应，进入下一个活动
     *
     * @param view
     */
    public void enterMainActivity(View view) {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public static class WelcomeFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final int[] arrAnimation =
                {R.raw.done, R.raw.animated_graph, R.raw.trophy, R.raw.floating_cloud};
        private static final int[] arrTitleText =
                {R.string.page1_title, R.string.page2_title, R.string.page3_title, R.string.page4_title};
        private static final int[] arrContentText =
                {R.string.page1_content, R.string.page2_content, R.string.page3_content, R.string.page4_content};
        protected boolean isCreate = false;
        private View rootView;


        public WelcomeFragment() {
        }

        /**
         * 创建 WelcomeFragment 实例
         *
         * @param sectionNumber 第几页
         * @return WelcomeFragment
         */
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

            LottieAnimationView animationViews = null;
            int iPage = getArguments().getInt(ARG_SECTION_NUMBER);

            //rootView = inflater.inflate(arrFragment[iPage], container, false);
            rootView = inflater.inflate(R.layout.fragment_welcome_page, container, false);

            //根据页面设置动画、文本、背景颜色
            animationViews = rootView.findViewById(R.id.animation_view);
            animationViews.setAnimation(arrAnimation[iPage]);
            TextView titleTextView = rootView.findViewById(R.id.title_text);
            titleTextView.setText(arrTitleText[iPage]);
            TextView contentTextView = rootView.findViewById(R.id.content_text);
            contentTextView.setText(arrContentText[iPage]);
            LinearLayout linearLayout = rootView.findViewById(R.id.linearLayout);
            linearLayout.setBackgroundColor(PAGE_COLOR[iPage]);


            Button button = rootView.findViewById(R.id.welcome_btn);


            //针对各个界面进行异化处理
            //第一个页面在创建后不会执行setUserVisibleHint方法，所以要手动播放动画。
            if (iPage == 0) {
                animationViews.setPadding(DensityUtil.Companion.dp2px(this.getContext(), 25), 0, 0, 0);
                animationViews.playAnimation();
            }

            if (iPage == 3) {
                titleTextView.setTextColor(0XFF000000);
                button.setVisibility(View.VISIBLE);
            } else {
                contentTextView.setTextColor(0XFFFFFFFF);
            }


            isCreate = true;
            return rootView;

        }


        /**
         * 通过 Fargment 的可见性控制动画的播放与暂停
         *
         * @param isVisibleToUser
         */
        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            LottieAnimationView animationView = null;

            if (rootView != null) {
                animationView = rootView.findViewById(R.id.animation_view);
            }

            if (animationView != null) {
                if (isVisibleToUser && isCreate) {
                    //相当于onResume
                    animationView.playAnimation();
                } else {
                    //相当于onPause
                    animationView.cancelAnimation();
                    animationView.setFrame(0);
                }
            }

        }

        public View getView() {
            return rootView;
        }

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Fragment mCurrentPrimaryItem;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // 获取相应页面的 Fragment 实例
            mCurrentPrimaryItem = arrWelcomeFragment[position];
            return mCurrentPrimaryItem;
        }

        @Override
        public int getCount() {
            return PAGE_NUMBER;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //重写方法让其不会销毁已经创建的实例
            //super.destroyItem(container, position, object);
        }

    }
}
