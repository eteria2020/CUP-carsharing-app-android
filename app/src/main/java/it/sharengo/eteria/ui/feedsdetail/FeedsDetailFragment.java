package it.sharengo.eteria.ui.feedsdetail;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.Feed;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;
import it.sharengo.eteria.utils.ImageUtils;
import it.sharengo.eteria.utils.StringsUtils;
import jp.wasabeef.glide.transformations.MaskTransformation;

import static android.content.Context.MODE_PRIVATE;


public class FeedsDetailFragment extends BaseMvpFragment<FeedsDetailPresenter> implements FeedsDetailMvpView {

    private static final String TAG = FeedsDetailFragment.class.getSimpleName();

    public static final String ARG_FEED = "ARG_FEED";

    private SharedPreferences mPref;
    private boolean isInters;

    @BindView(R.id.feedHeaderTextView)
    TextView feedHeaderTextView;

    @BindView(R.id.feedTitleTextView)
    TextView feedTitleTextView;

    @BindView(R.id.feedImageView)
    ImageView feedImageView;

    @BindView(R.id.feedTrapezoidImageView)
    ImageView feedTrapezoidImageView;

    @BindView(R.id.feedAdvantageTextView)
    TextView feedAdvantageTextView;

    @BindView(R.id.feedTriangleView)
    ViewGroup feedTriangleView;

    @BindView(R.id.feedTriangleImageView)
    ImageView feedTriangleImageView;

    @BindView(R.id.feedIconImageView)
    ImageView feedIconImageView;

    @BindView(R.id.feedDateTextView)
    TextView feedDateTextView;

    @BindView(R.id.feedLaunchTitleTextView)
    TextView feedLaunchTitleTextView;

    @BindView(R.id.feedAbstractTextView)
    TextView feedAbstractTextView;

    @BindView(R.id.feedLocationTextView)
    TextView feedLocationTextView;

    @BindView(R.id.feedAdvantageBottomTextView)
    TextView feedAdvantageBottomTextView;

    @BindView(R.id.feedDescriptionTextView)
    TextView feedDescriptionTextView;

    @BindView(R.id.interestedButton)
    Button interestedButton;

    @BindView(R.id.feedIntersImageView)
    ImageView feedIntersImageView;

    public static FeedsDetailFragment newInstance(Feed feed) {
        FeedsDetailFragment fragment = new FeedsDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FEED, (Serializable) feed);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);

        if(getArguments() != null){
            mPresenter.feed = (Feed) getArguments().getSerializable(ARG_FEED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds_detail, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.loadData();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Go back section.
     */
    @OnClick(R.id.backImageView)
    public void onBackClick(){
        getActivity().finish();
    }

    /**
     * Save id of feed selected by user.
     */
    @OnClick(R.id.interestedButton)
    public void onInterestedClick(){

        SharedPreferences.Editor prefsEditor = mPref.edit();

        Type fooType = new TypeToken<List<String>>() {}.getType();
        Gson gson = new Gson();
        List<String> feedsInterested = (ArrayList<String>) gson.fromJson(mPref.getString(getString(R.string.preference_feeds), "[]"), fooType);


        //Salvo l'id del feed se non esiste nell'array già salvato
        if(!isInters){
            feedsInterested.add(mPresenter.feed.id);
            isInters = true;
            interestedButton.setText(getString(R.string.feedsdetails_notinterested_action));
            feedIntersImageView.setVisibility(View.VISIBLE);
        }else{
            //Rimuovo dall'array
            feedsInterested.remove(mPresenter.feed.id);
            isInters = false;
            interestedButton.setText(getString(R.string.feedsdetails_interested_action));
            feedIntersImageView.setVisibility(View.GONE);
        }

        String json = gson.toJson(feedsInterested, fooType);
        prefsEditor.putString(getString(R.string.preference_feeds), json);
        prefsEditor.commit();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Show and create field of any information of feed passing as param.
     *
     * @param  feed  feed to show
     */
    public void showInformations(Feed feed){

        int feedColor = Color.parseColor(feed.appearance.color.rgb);

        if(!feed.informations.sponsored.equals("true")) feedColor = Color.parseColor(feed.appearance.color.rgb_default);



        //Categoria
        feedHeaderTextView.setText(StringsUtils.fromHTML(feed.category.name));

        //Immagine copertina
        Glide.with(feedImageView.getContext())
                .load(feed.media.images.image.uri)
                .crossFade()
                .bitmapTransform(new CenterCrop(feedImageView.getContext()),
                        new MaskTransformation(feedImageView.getContext(), R.drawable.trapezoid1))
                .into(feedImageView);


        //Overlay copertina
        feedTrapezoidImageView.setColorFilter(feedColor);

        //Advantage
        feedTriangleImageView.setColorFilter(feedColor);
        if(feed.informations.advantage_top.isEmpty()) {
            feedTriangleView.setVisibility(View.GONE);
        }else {
            feedTriangleView.setVisibility(View.VISIBLE);
            feedAdvantageTextView.setText(StringsUtils.fromHTML(feed.informations.advantage_top));
        }

        //Icona
        ImageUtils.loadImage(feedIconImageView, feed.category.media.images.icon.uri);
        GradientDrawable backgroundShape = (GradientDrawable) feedIconImageView.getBackground();
        backgroundShape.setColor(feedColor);

        //Data
        feedDateTextView.setText(StringsUtils.fromHTML(feed.informations.date.friendly));

        //Launch title
        if(feed.informations.launch_title.isEmpty()){
            feedLaunchTitleTextView.setVisibility(View.GONE);
        }else{
            feedLaunchTitleTextView.setText(StringsUtils.fromHTML(feed.informations.launch_title));
            feedLaunchTitleTextView.setTextColor(feedColor);
        }

        //Titolo
        feedTitleTextView.setText(StringsUtils.fromHTML(feed.title));

        //Abstract
        if(feed.informations.abstract_text.isEmpty()){
            feedAbstractTextView.setVisibility(View.GONE);
        }else{
            feedAbstractTextView.setText(StringsUtils.fromHTML(feed.informations.abstract_text));
        }

        //Location
        feedLocationTextView.setText(StringsUtils.fromHTML(feed.informations.location + ", " +  feed.informations.address.friendly +  ", " + feed.informations.city.name));

        //Advantage bottom
        if(feed.informations.advantage_bottom.isEmpty()){
            feedAdvantageBottomTextView.setVisibility(View.GONE);
        }else{
            feedAdvantageBottomTextView.setText(StringsUtils.fromHTML(feed.informations.advantage_bottom));

            if(feed.appearance.color.enforce != null && feed.appearance.color.enforce.equals("true"))
                feedAdvantageBottomTextView.setTextColor(feedColor);
        }

        //Descrizione
        if(feed.informations.description.isEmpty()){
            feedDescriptionTextView.setVisibility(View.GONE);
        }else{
            feedDescriptionTextView.setText(StringsUtils.fromHTML(feed.informations.description));
        }

        //Mi interessa
        Type fooType = new TypeToken<List<String>>() {}.getType();
        Gson gson = new Gson();
        List<String> feedsInterested = (ArrayList<String>) gson.fromJson(mPref.getString(getString(R.string.preference_feeds), "[]"), fooType);


        isInters = false;
        for (String feed_id : feedsInterested){
            if(feed_id.equals(mPresenter.feed.id)){
                interestedButton.setText(getString(R.string.feedsdetails_notinterested_action));
                isInters = true;
            }
        }

        if(isInters){
            feedIntersImageView.setVisibility(View.VISIBLE);
        }
    }


}
