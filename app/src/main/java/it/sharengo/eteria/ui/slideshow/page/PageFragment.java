package it.sharengo.eteria.ui.slideshow.page;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.sharengo.eteria.BuildConfig;
import it.sharengo.eteria.R;

/**
 * Created by gretaiaconisi on 20/06/17.
 */

public class PageFragment extends Fragment {

    private static final String TAG = PageFragment.class.getSimpleName();

    public static final String ARG_PAGE = "ARG_PAGE";

    private int page = 0;


    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments() != null){
            page = getArguments().getInt(ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slideshow_page, container, false);


        ImageView slideImageView = (ImageView) rootView.findViewById(R.id.slideImageView);
        TextView slideTitleTextView = (TextView) rootView.findViewById(R.id.slideTitleTextView);
        TextView slideDescTextView = (TextView) rootView.findViewById(R.id.slideDescTextView);
        View slideIndicator1 = rootView.findViewById(R.id.slideIndicator1);
        View slideIndicator2 = rootView.findViewById(R.id.slideIndicator2);
        View slideIndicator3 = rootView.findViewById(R.id.slideIndicator3);
        View slideIndicator4 = rootView.findViewById(R.id.slideIndicator4);
        if(BuildConfig.FLAVOR.equalsIgnoreCase("slovakia")){
            slideIndicator2.setVisibility(View.GONE);
        }

        switch (page){
            case 1:
                slideImageView.setImageDrawable(getIcon(R.drawable.img_ci));
                slideTitleTextView.setText(getString(R.string.signup_step1text1_action));
                slideDescTextView.setText(getString(R.string.signup_step1text2_action));
                slideIndicator1.setBackground(getIcon(R.drawable.indicator_fill));
                break;
            case 2:
                slideImageView.setImageDrawable(getIcon(R.drawable.img_cf));
                slideTitleTextView.setText(getString(R.string.signup_step2text1_action));
                slideDescTextView.setText(getString(R.string.signup_step2text2_action));
                slideIndicator2.setBackground(getIcon(R.drawable.indicator_fill));
                break;
            case 3:
                slideImageView.setImageDrawable(getIcon(R.drawable.img_patente));
                slideTitleTextView.setText(getString(R.string.signup_step3text1_action));
                slideDescTextView.setText(getString(R.string.signup_step3text2_action));
                slideIndicator3.setBackground(getIcon(R.drawable.indicator_fill));
                break;
            case 4:
                slideImageView.setImageDrawable(getIcon(R.drawable.img_ccredito));
                slideTitleTextView.setText(getString(R.string.signup_step4text1_action));
                slideDescTextView.setText(getString(R.string.signup_step4text2_action));
                slideIndicator4.setBackground(getIcon(R.drawable.indicator_fill));
                break;
        }

        return rootView;
    }

    private Drawable getIcon(int icon){
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = (Drawable) getActivity().getDrawable(icon);
        }else{
            drawable = (Drawable) getResources().getDrawable(icon);
        }

        return drawable;
    }
}
