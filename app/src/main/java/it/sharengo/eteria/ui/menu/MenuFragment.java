package it.sharengo.eteria.ui.menu;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;

public class MenuFragment extends BaseMvpFragment<MenuPresenter> implements MenuMvpView {

    private final String TAG = MenuFragment.class.getSimpleName();

    public static final String ARG_MENU_ITEM = "ARG_MENU_ITEM";

    @BindView(R.id.rv)
    RecyclerView mRv;
    
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
