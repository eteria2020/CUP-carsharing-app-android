package it.sharengo.development.ui.tutorial.page;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import it.sharengo.development.R;

/**
 * Created by gretaiaconisi on 20/06/17.
 */

public class PageFragment extends Fragment {

    private static final String TAG = PageFragment.class.getSimpleName();

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String ARG_LANG = "ARG_LANG";

    private int page = 0;
    private String lang = "";


    public static PageFragment newInstance(int page, String lang) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(ARG_LANG, lang);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments() != null){
            page = getArguments().getInt(ARG_PAGE);
            lang = getArguments().getString(ARG_LANG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_tutorial_page, container, false);


        ImageView slideImageView = (ImageView) rootView.findViewById(R.id.slideImageView);


        /*String lang = Locale.getDefault().getLanguage();

        if(!lang.equals("it")) lang = "en";*/


        switch (page){
            case 1:
                //lang
                slideImageView.setImageDrawable(getIcon(getResources().getIdentifier("tutorial_01_"+lang, "drawable", getActivity().getPackageName())));
                break;
            case 2:
                slideImageView.setImageDrawable(getIcon(getResources().getIdentifier("tutorial_02_"+lang, "drawable", getActivity().getPackageName())));
                break;
            case 3:
                slideImageView.setImageDrawable(getIcon(getResources().getIdentifier("tutorial_03_"+lang, "drawable", getActivity().getPackageName())));
                break;
            case 4:
                slideImageView.setImageDrawable(getIcon(getResources().getIdentifier("tutorial_04_"+lang, "drawable", getActivity().getPackageName())));
                break;
            case 5:
                slideImageView.setImageDrawable(getIcon(getResources().getIdentifier("tutorial_05_"+lang, "drawable", getActivity().getPackageName())));
                break;
            case 6:
                slideImageView.setImageDrawable(getIcon(getResources().getIdentifier("tutorial_06_"+lang, "drawable", getActivity().getPackageName())));
                break;
            case 7:
                slideImageView.setImageDrawable(getIcon(getResources().getIdentifier("tutorial_07_"+lang, "drawable", getActivity().getPackageName())));
                break;
            case 8:
                slideImageView.setImageDrawable(getIcon(getResources().getIdentifier("tutorial_08_"+lang, "drawable", getActivity().getPackageName())));
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
