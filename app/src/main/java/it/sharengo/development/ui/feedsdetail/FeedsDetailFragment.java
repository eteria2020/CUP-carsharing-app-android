package it.sharengo.development.ui.feedsdetail;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.data.models.Feed;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.utils.ImageUtils;
import jp.wasabeef.glide.transformations.MaskTransformation;

import static android.content.Context.MODE_PRIVATE;


public class FeedsDetailFragment extends BaseMvpFragment<FeedsDetailPresenter> implements FeedsDetailMvpView {

    private static final String TAG = FeedsDetailFragment.class.getSimpleName();

    public static final String ARG_FEED = "ARG_FEED";

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
    @OnClick(R.id.backImageView)
    public void onBackClick(){
        getActivity().finish();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void showInformations(Feed feed){


        //Categoria
        feedHeaderTextView.setText(feed.category.name);

        //Immagine copertina
        Glide.with(feedImageView.getContext())
                .load(feed.media.images.image.uri)
                .crossFade()
                .bitmapTransform(new CenterCrop(feedImageView.getContext()),
                        new MaskTransformation(feedImageView.getContext(), R.drawable.trapezoid1))
                .into(feedImageView);


        //Overlay copertina
        feedTrapezoidImageView.setColorFilter(Color.parseColor(feed.appearance.color.rgb));

        //Advantage
        feedTriangleImageView.setColorFilter(Color.parseColor(feed.appearance.color.rgb));
        if(feed.informations.advantage_top.isEmpty()) {
            feedTriangleView.setVisibility(View.GONE);
        }else {
            feedTriangleView.setVisibility(View.VISIBLE);
            feedAdvantageTextView.setText(feed.informations.advantage_top);
        }

        //Icona
        ImageUtils.loadImage(feedIconImageView, feed.category.media.images.icon.uri);
        GradientDrawable backgroundShape = (GradientDrawable) feedIconImageView.getBackground();
        backgroundShape.setColor(Color.parseColor(feed.appearance.color.rgb));

        //Data
        feedDateTextView.setText(feed.informations.date.friendly);

        //Launch title
        if(feed.informations.launch_title.isEmpty()){
            feedLaunchTitleTextView.setVisibility(View.GONE);
        }else{
            feedLaunchTitleTextView.setText(feed.informations.launch_title);
            feedLaunchTitleTextView.setTextColor(Color.parseColor(feed.appearance.color.rgb));
        }

        //Titolo
        feedTitleTextView.setText(feed.title);

        //Abstract
        if(feed.informations.abstract_text.isEmpty()){
            feedAbstractTextView.setVisibility(View.GONE);
        }else{
            feedAbstractTextView.setText(feed.informations.abstract_text);
        }

        //Location
        feedLocationTextView.setText(feed.informations.location + ", " +  feed.informations.address.friendly +  ", " + feed.informations.city.name);

        //Advantage bottom
        if(feed.informations.advantage_bottom.isEmpty()){
            feedAdvantageBottomTextView.setVisibility(View.GONE);
        }else{
            feedAdvantageBottomTextView.setText(feed.informations.advantage_bottom);

            if(feed.appearance.color.enforce.equals("true"))
                feedAdvantageBottomTextView.setTextColor(Color.parseColor(feed.appearance.color.rgb));
        }

        //Descrizione
        if(feed.informations.description.isEmpty()){
            feedDescriptionTextView.setVisibility(View.GONE);
        }else{
            feedDescriptionTextView.setText(feed.informations.description);
        }
    }


}
