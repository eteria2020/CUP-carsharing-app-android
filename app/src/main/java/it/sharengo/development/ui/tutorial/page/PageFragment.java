package it.sharengo.development.ui.tutorial.page;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.sharengo.development.R;

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
                R.layout.fragment_tutorial_page, container, false);


        ImageView slideImageView = (ImageView) rootView.findViewById(R.id.slideImageView);

        switch (page){
            case 1:
                slideImageView.setImageDrawable(getIcon(R.drawable.tutorial_01));
                break;
            case 2:
                slideImageView.setImageDrawable(getIcon(R.drawable.tutorial_02));
                break;
            case 3:
                slideImageView.setImageDrawable(getIcon(R.drawable.tutorial_03));
                break;
            case 4:
                slideImageView.setImageDrawable(getIcon(R.drawable.tutorial_04));
                break;
            case 5:
                slideImageView.setImageDrawable(getIcon(R.drawable.tutorial_05));
                break;
            case 6:
                slideImageView.setImageDrawable(getIcon(R.drawable.tutorial_06));
                break;
            case 7:
                slideImageView.setImageDrawable(getIcon(R.drawable.tutorial_07));
                break;
            case 8:
                slideImageView.setImageDrawable(getIcon(R.drawable.tutorial_08));
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
