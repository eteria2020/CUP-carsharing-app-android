package it.sharengo.eteria.ui.components;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import it.sharengo.eteria.R;

/**
 * Created by greta on 29/05/17.
 */

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public CustomTextView messageTextView;

    private String message, yesString, noString;

    public CustomDialogClass(Activity a, String message, String yesString, String noString) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.message = message;
        this.yesString = yesString;
        this.noString = noString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_custom_dialog);
        yes = (Button) findViewById(R.id.btn_yes);
        yes.setText(yesString);
        if(yesString == null) yes.setVisibility(View.GONE);

        no = (Button) findViewById(R.id.btn_no);
        no.setText(noString);
        if(noString == null) no.setVisibility(View.GONE);

        messageTextView = (CustomTextView) findViewById(R.id.txt_dia);
        messageTextView.setText(message);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                //dismiss();
                break;
            case R.id.btn_no:
                //dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public void dismissAlert(){
        dismiss();
    }
}
