package it.sharengo.eteria.ui.slideshow;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.eteria.BuildConfig;
import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.components.CenteredImageSpan;
import it.sharengo.eteria.ui.slideshow.page.PageFragment;


public class SlideshowFragment extends BaseMvpFragment<SlideshowPresenter> implements SlideshowMvpView {

    private static final String TAG = SlideshowFragment.class.getSimpleName();

    private static final int NUM_PAGES = 4;
    private PagerAdapter mPagerAdapter;

    @BindView(R.id.slideshowViewPager)
    ViewPager mPager;

    @BindView(R.id.arrowLeftImageView)
    ImageView arrowLeftImageView;

    @BindView(R.id.arrowRightImageView)
    ImageView arrowRightImageView;

    @BindView(R.id.signupDescrTextView)
    TextView signupDescrTextView;

    public static SlideshowFragment newInstance() {
        SlideshowFragment fragment = new SlideshowFragment();
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
        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        //Pager
        mPagerAdapter = new PagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if(position == 0){
                    arrowLeftImageView.setVisibility(View.GONE);
                }else{
                    arrowLeftImageView.setVisibility(View.VISIBLE);
                }

                if(position >= NUM_PAGES-1){
                    arrowRightImageView.setVisibility(View.GONE);
                }else{
                    arrowRightImageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Icona chat
        CenteredImageSpan is = new CenteredImageSpan(getContext(), R.drawable.ic_chat);
        String description = getString(R.string.signup_description_label);
        Spannable text = new SpannableString(description + "  ");
        text.setSpan(is, text.length()-1, text.length(), 0);
        signupDescrTextView.setText(text);

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
            for (int i = 1; i <= NUM_PAGES; i++) {
                if((BuildConfig.FLAVOR.equalsIgnoreCase("slovakia") || BuildConfig.FLAVOR.equalsIgnoreCase("olanda")) && i ==2)
                    continue;
                pagerFragments.add(PageFragment.newInstance(i));
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

    @OnClick(R.id.arrowLeftImageView)
    public void onLeftArrowClick(){
        int tab = mPager.getCurrentItem();
        if (tab > 0) {
            tab--;
            mPager.setCurrentItem(tab);
        } else if (tab == 0) {
            mPager.setCurrentItem(tab);
        }
    }

    @OnClick(R.id.arrowRightImageView)
    public void onRightArrowClick(){
        int tab = mPager.getCurrentItem();
        tab++;
        mPager.setCurrentItem(tab);
    }

    @OnClick(R.id.subscribeButton)
    public void onSubscribeClick(){
        Navigator.launchSignup(this);
        getActivity().finish();
    }

    @OnClick(R.id.signupDescrTextView)
    public void onChatClick(){
        Navigator.launchAssistance(this);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




}
