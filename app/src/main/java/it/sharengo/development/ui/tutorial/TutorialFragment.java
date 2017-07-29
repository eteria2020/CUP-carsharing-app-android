package it.sharengo.development.ui.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;
import it.sharengo.development.ui.slideshow.SlideshowFragment;
import it.sharengo.development.ui.tutorial.page.PageFragment;


public class TutorialFragment extends BaseMvpFragment<TutorialPresenter> implements TutorialMvpView {

    private static final String TAG = TutorialFragment.class.getSimpleName();

    private static final int NUM_PAGES = 8;
    private PagerAdapter mPagerAdapter;

    @BindView(R.id.tutorialViewPager)
    ViewPager mPager;

    @BindView(R.id.prevTutorialButton)
    ImageView prevTutorialButton;

    @BindView(R.id.nextTutorialButton)
    ImageView nextTutorialButton;

    public static TutorialFragment newInstance() {
        TutorialFragment fragment = new TutorialFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        //Pager
        mPagerAdapter = new PagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if(position == 0){
                    prevTutorialButton.setVisibility(View.GONE);
                }else{
                    prevTutorialButton.setVisibility(View.VISIBLE);
                }

                /*if(position >= NUM_PAGES-1){
                    //nextTutorialButton.setVisibility(View.GONE);
                }else{
                    //nextTutorialButton.setVisibility(View.VISIBLE);
                }*/
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Pager
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class PagerAdapter extends FragmentStatePagerAdapter {

        private List<PageFragment> pagerFragments;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            pagerFragments = new ArrayList<>();
            for (int i = 0; i < NUM_PAGES; i++) {
                pagerFragments.add(PageFragment.newInstance(i+1));
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Integer.toString(position + 1);
        }

        @Override
        public int getCount() {
            return pagerFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return pagerFragments.get(position);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.closeTutorialButton)
    public void closeTutorialButton(){
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.faq_close_alert),
                getString(R.string.faq_stay_action),
                getString(R.string.faq_exit_action));
        cdd.show();
        cdd.no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
                getActivity().finish();
            }
        });
    }

    @OnClick(R.id.prevTutorialButton)
    public void onPrevButton(){
        int tab = mPager.getCurrentItem();
        if (tab > 0) {
            tab--;
            mPager.setCurrentItem(tab);
        } else if (tab == 0) {
            mPager.setCurrentItem(tab);
        }
    }

    @OnClick(R.id.nextTutorialButton)
    public void onNextButton(){
        int tab = mPager.getCurrentItem();
        tab++;
        mPager.setCurrentItem(tab);

        if(tab == 8){
            getActivity().finish();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
