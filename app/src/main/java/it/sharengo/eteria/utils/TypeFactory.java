package it.sharengo.development.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by greta on 29/05/17.
 */

public class TypeFactory {
    private String P_BOLD= "Poppins-Bold.ttf";
    private String P_LIGHT="Poppins-Light.ttf";
    private String P_REGULAR= "Poppins-Regular.ttf";
    private String P_MEDIUM= "Poppins-Medium.ttf";
    private String P_SEMIBOLD="Poppins-SemiBold.ttf";

    public Typeface poppinsBold;
    public Typeface poppinsLight;
    public Typeface poppinsRegular;
    public Typeface poppinsMedium;
    public Typeface poppinsSemiBold;

    public TypeFactory(Context context){
        poppinsBold = Typeface.createFromAsset(context.getAssets(),P_BOLD);
        poppinsLight = Typeface.createFromAsset(context.getAssets(),P_LIGHT);
        poppinsRegular = Typeface.createFromAsset(context.getAssets(),P_REGULAR);
        poppinsMedium = Typeface.createFromAsset(context.getAssets(),P_MEDIUM);
        poppinsSemiBold = Typeface.createFromAsset(context.getAssets(),P_SEMIBOLD);
    }
}
