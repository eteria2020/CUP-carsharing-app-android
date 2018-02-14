package it.handroix.core.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.handroix.core.R;

/**
 * Created by andrealucibello on 04/07/14.
 */
public class HdxLoadingFragment extends Fragment {
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.hdx_fragment_loading, null);
//        RelativeLayout progressContainer = (RelativeLayout) mView.findViewById(R.id.progressContainer);

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            ProgressBar progressBar = new ProgressBar(getActivity());
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//            progressBar.setLayoutParams(params);
//            progressContainer.addView(progressBar);
//        } else {
//            MaterialProgressBar materialProgressBar = new MaterialProgressBar(getActivity());
//            materialProgressBar.setIndeterminate(true);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//            materialProgressBar.setLayoutParams(params);
//            progressContainer.addView(materialProgressBar);
//        }
        return mView;
    }
}
