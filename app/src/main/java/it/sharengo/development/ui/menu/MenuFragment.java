package it.sharengo.development.ui.menu;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.handroix.core.utils.HdxUiUtility;
import it.sharengo.development.R;
import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.activities.BaseBackActivity;
import it.sharengo.development.ui.base.activities.BaseDrawerActivity;
import it.sharengo.development.ui.base.activities.BaseToolbarActivity;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;

import static android.content.Context.MODE_PRIVATE;

public class MenuFragment extends BaseMvpFragment<MenuPresenter> implements MenuMvpView {

    private final String TAG = MenuFragment.class.getSimpleName();

    public static final String ARG_MENU_ITEM = "ARG_MENU_ITEM";

    @BindView(R.id.rv)
    RecyclerView mRv;

    @BindView(R.id.welcomeTextView)
    TextView welcomeTextView;

    @BindView(R.id.scoreTextView)
    TextView scoreTextView;

    @BindView(R.id.profileButton)
    ViewGroup profileButton;
    
    private MenuAdapter mAdapter;

    public static MenuFragment newInstance(String sectionString) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MENU_ITEM, sectionString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpFragmentComponent(savedInstanceState).inject(this);
        mAdapter = new MenuAdapter(mActionListener, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mRv.setHasFixedSize(true);
        final LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        lm.setSmoothScrollbarEnabled(false);
        mRv.setLayoutManager(lm);
        mRv.setAdapter(mAdapter);
        //mRv.addItemDecoration(new DividerItemDecoration(mRv.getContext(), lm.getOrientation()));
        
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String sectionString = MenuItem.Section.NONE.toString();
        if(getArguments() != null) {
            String sectionArg = getArguments().getString(ARG_MENU_ITEM);
            sectionString = sectionArg != null ? sectionArg : sectionString;
        }


        mPresenter.loadMenu(sectionString);
    }

    /**
     * When user tap logout close view and open Home.
     */
    public void logoutUser(){
        Navigator.launchHome(this);
        getActivity().finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                      Butter Knife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Close actual view and open Profile.
     */
    @OnClick(R.id.profileButton)
    public void onProfileClick(){
        ((BaseDrawerActivity) getActivity()).closeRightDrawerFrame();

        if(mPresenter.getMenuSelection() != MenuItem.Section.PROFILE) {
            Navigator.launchProfile(this);
            getActivity().finish();
        }
    }
    

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //          MVP View
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Set menu with list item and show a message to user.
     * The message contain name if user is authenticated.
     *
     * @param  menuItemList  list of menu's item
     */
    @Override
    public void showList(List<MenuItem> menuItemList) {
        mAdapter.setData(menuItemList);

        //Messaggio benvenuto
        if(mPresenter.isAuth()){
            welcomeTextView.setText(String.format(getString(R.string.menu_welcome_login), mPresenter.getUserInfo().name));

            profileButton.setVisibility(View.VISIBLE);
            scoreTextView.setText("+75");
            scoreTextView.setVisibility(View.GONE);
        }else{
            welcomeTextView.setText(getString(R.string.menu_welcome));

            profileButton.setVisibility(View.GONE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //          LISTENERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private MenuAdapter.OnItemActionListener mActionListener = new MenuAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(MenuItem menuItem) {
            ((BaseDrawerActivity) getActivity()).closeRightDrawerFrame();
            startActivityDelayed(menuItem);
        }
    };
    
    private void startActivityDelayed(final MenuItem menuItem) {
        
        if(menuItem.section == mPresenter.getMenuSelection()) {
            return;
        }
        
        /**
         * Apro la nuova activity dopo 300 ms per dare il tempo al drawer di chiudersi
         * e non far "scattare" l'animazione
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch(menuItem.section) {
                    case LOGIN:
                        Navigator.launchLogin(MenuFragment.this, Navigator.REQUEST_LOGIN_START);
                        getActivity().finish();
                        break;
                    case LOGOUT:
                        mPresenter.logout(getActivity(), getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE));
                        getActivity().finish();
                        break;
                    case SIGNUP:
                        Navigator.launchSlideshow(MenuFragment.this);
                        getActivity().finish();
                        break;
                    case BOOKING:
                        Navigator.launchMapGoogle(MenuFragment.this, Navigator.REQUEST_MAP_DEFAULT);
                        getActivity().finish();
                        break;
                    case SETTINGS:
                        Navigator.launchSettings(MenuFragment.this);
                        getActivity().finish();
                        break;
                    case HISTORIC:
                        Navigator.launchChronology(MenuFragment.this);
                        getActivity().finish();
                        break;
                    case PROFILE:
                        Navigator.launchProfile(MenuFragment.this);
                        getActivity().finish();
                        break;
                    case HELP:
                        Navigator.launchAssistance(MenuFragment.this);
                        getActivity().finish();
                        break;
                    case SHARE:
                        Navigator.launchShare(MenuFragment.this);
                        getActivity().finish();
                        break;
                    case FAQ:
                        Navigator.launchFaq(MenuFragment.this);
                        break;
                }


            }
        }, 300);
    }
}
