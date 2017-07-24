package it.sharengo.development.ui.assistance;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;
import it.sharengo.development.ui.map.MapFragment;


public class AssistanceFragment extends BaseMvpFragment<AssistancePresenter> implements AssistanceMvpView {

    private static final String TAG = AssistanceFragment.class.getSimpleName();


    public static AssistanceFragment newInstance() {
        AssistanceFragment fragment = new AssistanceFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assistance, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.callAssistanceButton)
    public void onAssistanceClick(){
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.assistance_phonenumber_label),
                getString(R.string.assistance_alertcall_action),
                getString(R.string.assistance_cancelcall_action));
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
                startActivity( new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString(R.string.assistance_phonenumber_label))));
            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
