package it.sharengo.eteria.ui.components;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import it.sharengo.eteria.R;

/**
 * Created by greta on 29/05/17.
 */

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_BONUS = 1;

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public TextView messageTextView;
    public int type;

    private String message, yesString, noString;
    private SpannableStringBuilder builder;

    private Handler localHandler=new Handler();
    private int displayTime;

    public CustomDialogClass(Activity a, String message, String yesString, String noString) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.message = message;
        this.yesString = yesString;
        this.noString = noString;
        this.displayTime=0;
        this.type = TYPE_DEFAULT;
    }
    public CustomDialogClass(Activity a, SpannableStringBuilder message, String yesString, String noString, int type) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.message = null;
        this.builder = message;
        this.yesString = yesString;
        this.noString = noString;
        this.displayTime=0;
        this.type = type;
    }

    public CustomDialogClass(Activity a, String message, int displayTime) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.message = message;

        this.yesString = null;
        this.noString = null;
        this.displayTime=displayTime;
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

        Typeface font = Typeface.createFromAsset(c.getAssets(), "Poppins-Regular.ttf");
        messageTextView =  findViewById(R.id.txt_dia);
        messageTextView.setTypeface(font);



        if(this.message!=null)
            messageTextView.setText(message);
        else
            messageTextView.setText(builder);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        if(displayTime>0){
            localHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {

                        CustomDialogClass.this.dismiss();
                    }catch (Exception e){
                        Log.e("BOMB","Exception while auto-dismiss dialog",e);
                    }
                }
            },displayTime);
        }
        switch (this.type){
            case TYPE_BONUS:
                findViewById(R.id.customDialogLL).setBackgroundColor(c.getResources().getColor(R.color.shamrockgreen_1));
                break;
            case TYPE_DEFAULT:
            default:
                break;
        }
    }

    public void setMessage(String message, String messageBold){
        SpannableString spannableMessage = new SpannableString(message);

        SpannableString spannablePricing = new SpannableString(messageBold);
        spannableMessage.setSpan(new StyleSpan(Typeface.NORMAL), 0, spannableMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannablePricing.setSpan(new StyleSpan(Typeface.BOLD), 0, spannablePricing.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        if(this.type == TYPE_BONUS)
//            spannablePricing.setSpan(new ForegroundColorSpan(c.getResources().getColor(R.color.onyx_3)), 0, spannablePricing.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder = new SpannableStringBuilder();
        builder.append(spannableMessage);
        builder.append(spannablePricing);
        this.message = null;
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
