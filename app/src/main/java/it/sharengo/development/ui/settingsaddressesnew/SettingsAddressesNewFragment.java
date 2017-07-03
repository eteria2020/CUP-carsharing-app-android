package it.sharengo.development.ui.settingsaddressesnew;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import it.sharengo.development.R;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;
import it.sharengo.development.ui.map.MapSearchListAdapter;

import static android.content.Context.MODE_PRIVATE;


public class SettingsAddressesNewFragment extends BaseMvpFragment<SettingsAddressesNewPresenter> implements SettingsAddressesNewMvpView {

    private static final String TAG = SettingsAddressesNewFragment.class.getSimpleName();

    private final int SPEECH_RECOGNITION_CODE = 1;

    private View view;
    private boolean searchViewOpen = false;

    private MapSearchListAdapter mAdapter;
    private MapSearchListAdapter.OnItemActionListener mActionListener = new MapSearchListAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(SearchItem searchItem) {
            if(!searchItem.type.equals("none"))
                setSearchItemSelected(searchItem);
        }
    };

    private final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            setKeyboardListener();
        }
    };

    @BindView(R.id.addressEditText)
    EditText addressEditText;

    @BindView(R.id.nameEditText)
    EditText nameEditText;

    @BindView(R.id.searchView)
    ViewGroup searchView;

    @BindView(R.id.searchMapView)
    LinearLayout searchMapView;

    @BindView(R.id.searchEditText)
    EditText searchEditText;

    @BindView(R.id.searchMapResultView)
    ViewGroup searchMapResultView;

    @BindView(R.id.microphoneImageView)
    ImageView microphoneImageView;

    @BindView(R.id.searchRecyclerView)
    RecyclerView searchRecyclerView;

    public static SettingsAddressesNewFragment newInstance() {
        SettingsAddressesNewFragment fragment = new SettingsAddressesNewFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);

        mAdapter = new MapSearchListAdapter(mActionListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings_addresses_new, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        //Ricerca
        final LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        searchRecyclerView.setLayoutManager(lm);
        searchRecyclerView.setAdapter(mAdapter);
        setSearchDefaultContent();


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(view != null)
            view.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == getActivity().RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    searchEditText.setText(text);
                    initMapSearch();

                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                }
                break;
            }

        }
    }

    private void checkFormValidation(){
        String address  = addressEditText.getText().toString().trim();
        String name     = nameEditText.getText().toString().trim();

        //Verifico se tutti i campi sono compilati
        if(address.isEmpty() || name.isEmpty()){
            final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
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

        //Se è tutto ok procedo con il salvataggio
        mPresenter.saveFavourite(getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE), new SearchItem(name, address, "favorite"));
        getActivity().finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Ricerca
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener: apertura / chiusura della tastiera
    private void setKeyboardListener(){

        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);

        if (view.getRootView().getHeight() - (r.bottom - r.top) > 500) { //Tastiera aperta

            //Setto l'altezza della view dei risultati di ricerca
            setSearchViewHeight();

            //Verifico se la view era precedentemente aperta
            if(!searchViewOpen) {


                searchViewOpen = true;
            }
        } else { //Tastiera chiusa
            //Verifico se la view era precedentemente aperta
            if(searchViewOpen) {
                clearSearch();
                searchView.setVisibility(View.GONE);
            }

            //searchEditText.clearFocus();

        }
    }

    //Metodo richiamato quando viene scritto qualcosa nella casella di ricerca
    private void initMapSearch(){

        String searchMapText = searchEditText.getText().toString();

        //Verifico prima di tutto che l'utente abbia scritto 3 caratteri. La ricerca parte nel momento in cui vengono digitati 3 caratteri
        if (searchMapText.length() > 2) {

            mPresenter.findAddress(searchMapText);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchRecyclerView.scrollToPosition(0);
                }
            });


        }else{ //Se i caratteri digitati sono meno di 3, ripulisco la lista

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mAdapter.setData(null);

                }
            });


            if(searchMapText.length() == 0) setSearchDefaultContent();
        }
    }

    //Setto l'altezza della view contente i risultati di una ricerca
    private void setSearchViewHeight(){

        //Prelevo l'altezza di una singola voce della lista
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = getResources().getDimension(R.dimen.search_item_height); // * (metrics.densityDpi / 160f)
        float itemHeight = Math.round(px);

        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);

        //Calcolo il numero di elementi che possono essere visualizzati all'interno dell'intefaccia senza che nessuno venga tagliato a livello visivo
        float totalHeight = r.height()- searchMapView.getHeight();
        int nItem = (int) (totalHeight / itemHeight) - 1;

        //Setto l'altezza della lista
        searchMapResultView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (itemHeight*nItem) + 5));
        searchRecyclerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (itemHeight*nItem)));
    }

    private void clearSearch(){
        searchViewOpen = false;

        //Pulisco la Edittext
        searchEditText.setText("");

        //Setto il contenuto di default
        setSearchDefaultContent();
    }

    private void setSearchDefaultContent(){
        mPresenter.getHistoric("", getContext(), getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE));
    }


    //Microfono
    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.maps_searchmicrophone_message));
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Snackbar.make(view, getString(R.string.error_generic_msg), Snackbar.LENGTH_LONG).show();
        }
    }

    private void setSearchItemSelected(SearchItem searchItem){
        hideSoftKeyboard();

        //Inserisco nella casella di testo il valore cercato
        addressEditText.setText(searchItem.display_name);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.backImageView)
    public void onBackClick(){
        getActivity().finish();
    }

    private Timer timerEditText=new Timer();
    private final long DELAY = 500;
    @OnTextChanged(value = R.id.searchEditText,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void searchEditText() {


        timerEditText.cancel();
        timerEditText = new Timer();
        timerEditText.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        initMapSearch();
                    }
                },
                DELAY
        );
    }

    @OnClick(R.id.microphoneImageView)
    public void onSearchMicrophone(){
        startSpeechToText();
    }

    @OnFocusChange(R.id.addressEditText)
    @OnClick(R.id.addressEditText)
    public void onAddressClick(){
        if(addressEditText.isFocused()) {
            //hideSoftKeyboard();
            //addressEditText.clearFocus();
            searchEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);

            searchView.setVisibility(View.VISIBLE);
            searchMapResultView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.saveButton)
    public void onSaveClick(){
        checkFormValidation();
    }

    @OnClick(R.id.cancelButton)
    public void onCancelClick(){
        getActivity().finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void showSearchResult(List<SearchItem> searchItemList) {

        setSearchViewHeight();
        mAdapter.setData(searchItemList);

    }

}
