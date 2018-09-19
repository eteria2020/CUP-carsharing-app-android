package it.sharengo.eteria.ui.menu;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.eteria.App;
import it.sharengo.eteria.BuildConfig;
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.ui.mapgoogle.MapGoogleActivity;
import it.sharengo.eteria.ui.userarea.UserAreaActivity;

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

    @BindView(R.id.versionTV)
    TextView versionTV;
    
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
        versionTV.setText("Ver. " + BuildConfig.VERSION_NAME);
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
        Navigator.launchMapGoogle(this,Navigator.REQUEST_MAP_DEFAULT);
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

            String sexWelcome = (mPresenter.getUserInfo().gender.equals("female")) ? getString(R.string.menu_welcomef_login) : getString(R.string.menu_welcome_login);
            welcomeTextView.setText(String.format(sexWelcome, mPresenter.getUserInfo().name));

            //profileButton.setVisibility(View.VISIBLE);
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
                    case NONE:
                        break;
                    case HOME:
                        break;
                    case LOGIN:
                        Navigator.launchLogin(MenuFragment.this, Navigator.REQUEST_LOGIN_START);
                        closeActivity();
                        break;
                    case LOGOUT:
                        mPresenter.alertLogout(getActivity(), getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE));
                        break;
                    case SIGNUP:
                        Navigator.launchSlideshow(MenuFragment.this);
                        closeActivity();
                        break;
                    case BOOKING:
                        Navigator.launchMapGoogle(MenuFragment.this, Navigator.REQUEST_MAP_DEFAULT);
                        closeActivity();
                        break;
                    case SETTINGS:
                        Navigator.launchSettings(MenuFragment.this);
                        closeActivity();
                        break;
                    case HISTORIC:
                        Navigator.launchChronology(MenuFragment.this);
                        closeActivity();
                        break;
                    case PROFILE:
                        Navigator.launchUserArea(MenuFragment.this);
                        closeActivity();
                        break;
                    case HELP:
                        Navigator.launchAssistance(MenuFragment.this);
                        closeActivity();
                        break;
                    case SHARE:
                        Navigator.launchShare(MenuFragment.this);
                        closeActivity();
                        break;
                    case FAQ:
                        Navigator.launchFaq(MenuFragment.this);
                        closeActivity();
                        break;
                    case BUY:
                        Navigator.launchBuy(MenuFragment.this);
                        closeActivity();
                        break;
                    case RATES:
                        Navigator.launchRates(MenuFragment.this);
                        closeActivity();
                        break;
                    case PIN:
                        Navigator.launchPin(MenuFragment.this);
                        closeActivity();
                        break;
                    case FAKE_RATES:
                        Navigator.launchUserArea(MenuFragment.this, UserAreaActivity.InnerRoute.RATES);
                        closeActivity();
                        break;
                    case LEGAL_NOTE:
                        Navigator.launchLegalNote(MenuFragment.this);
                        closeActivity();
                        break;
                    case PRIVACY:
                        Navigator.launchPrivacy(MenuFragment.this);
                        closeActivity();
                        break;
                }


            }
        }, 300);
    }

    private void closeActivity(){
        if(!getActivity().getClass().equals(MapGoogleActivity.class))
            getActivity().finish();
    }
}
