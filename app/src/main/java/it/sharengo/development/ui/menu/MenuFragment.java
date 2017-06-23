package it.sharengo.development.ui.menu;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
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
import it.sharengo.development.R;
import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.activities.BaseDrawerActivity;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;

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
        mAdapter = new MenuAdapter(mActionListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mRv.setHasFixedSize(true);
        final LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRv.setLayoutManager(lm);
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(new DividerItemDecoration(mRv.getContext(), lm.getOrientation()));
        
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                      Butter Knife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //          MVP View
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void showList(List<MenuItem> menuItemList) {
        mAdapter.setData(menuItemList);

        //Messaggio benvenuto
        if(mPresenter.isAuth()){
            welcomeTextView.setText(String.format(getString(R.string.menu_welcome_login), mPresenter.getUserInfo().name));

            profileButton.setVisibility(View.VISIBLE);
            scoreTextView.setText("+75");
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
        
        if(menuItem.selected) {
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
                    case HOME:
                        Navigator.launchHome(MenuFragment.this);
                        break;
                }
            }
        }, 300);
    }
}
