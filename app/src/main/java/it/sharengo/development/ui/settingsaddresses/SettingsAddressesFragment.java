package it.sharengo.development.ui.settingsaddresses;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;

import static android.content.Context.MODE_PRIVATE;


public class SettingsAddressesFragment extends BaseMvpFragment<SettingsAddressesPresenter> implements SettingsAddressesMvpView {

    private static final String TAG = SettingsAddressesFragment.class.getSimpleName();

    private SettingsAddressesAdapter mAdapter;

    private SearchItem searchItemSelected;

    @BindView(R.id.addressLayout)
    ViewGroup addressLayout;

    @BindView(R.id.addressListLayout)
    ViewGroup addressListLayout;

    @BindView(R.id.addressesRecyclerView)
    RecyclerView mRv;

    @BindView(R.id.editLayout)
    ViewGroup editLayout;

    @BindView(R.id.editTitleTextView)
    TextView editTitleTextView;

    @BindView(R.id.addressEditText)
    EditText addressEditText;

    @BindView(R.id.nameEditText)
    EditText nameEditText;

    public static SettingsAddressesFragment newInstance() {
        SettingsAddressesFragment fragment = new SettingsAddressesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);
        mAdapter = new SettingsAddressesAdapter(mActionListener, getActivity(), this);

        mPresenter.setData(getContext(), getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_addresses, container, false);
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

        mPresenter.loadData(getContext(), getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE));
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //          LISTENERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private SettingsAddressesAdapter.OnItemActionListener mActionListener = new SettingsAddressesAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(SearchItem searchItem) {

        }
    };

    public void setAddFavorite(SearchItem searchItem){
        searchItemSelected = searchItem;

        editTitleTextView.setText(getString(R.string.settingsaddress_addtitle_label));
        addressEditText.setText(searchItem.display_name);

        editLayout.setVisibility(View.VISIBLE);
    }

    public void setEditFavorite(SearchItem searchItem){
        searchItemSelected = searchItem;

        editTitleTextView.setText(getString(R.string.settingsaddress_edittitle_label));
        addressEditText.setText(searchItem.display_name);
        nameEditText.setText(searchItem.name);

        editLayout.setVisibility(View.VISIBLE);
    }

    public void setDeleteFavorite(SearchItem searchItem){

    }

    private void closeModal(){
        editLayout.setVisibility(View.GONE);
        addressEditText.setText("");
        nameEditText.setText("");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.backImageView)
    public void onBackClick(){
        Navigator.launchSettings(this);
    }

    @OnClick(R.id.newAddressButton)
    public void onAddressClick(){
        Navigator.launchSettingsAddressesNew(this);
    }

    @OnClick(R.id.okSaveButton)
    public void onOkSaveClick(){

        String address  = addressEditText.getText().toString().trim();
        String name     = nameEditText.getText().toString().trim();

        //Verifico se tutti i campi sono compilati
        if(address.isEmpty() || name.isEmpty()) {
            final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                    getString(R.string.settingsaddressnew_empty_error),
                    getString(R.string.ok),
                    null);
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                }
            });
            return;
        }

        if(searchItemSelected != null){
            if(searchItemSelected.type.equals("historic")){
                mPresenter.addFavourite(getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE),
                        searchItemSelected,
                        name,
                        address);
            }else {
                mPresenter.editFavourite(getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE),
                        searchItemSelected,
                        name,
                        address);
            }
        }
    }

    @OnClick(R.id.closeButton)
    public void onCloseClick(){
        closeModal();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void showEmptyResult(){
        addressLayout.setVisibility(View.VISIBLE);
        addressListLayout.setVisibility(View.GONE);
    }

    public void showList(List<SearchItem> searchItems){

        closeModal();

        mAdapter.setData(searchItems);
        addressLayout.setVisibility(View.GONE);
        addressListLayout.setVisibility(View.VISIBLE);
    }

}
