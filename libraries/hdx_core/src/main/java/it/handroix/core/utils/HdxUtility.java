package it.handroix.core.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.widget.Toast;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import it.handroix.core.R;
/**
 * Created by andrealucibello on 19/05/14.
 */
public class HdxUtility {

    public static int CONNECTIVITY_TYPE_WIFI = 2;
    public static int CONNECTIVITY_TYPE_MOBILE = 1;
    public static int CONNECTIVITY_TYPE_NOT_CONNECTED = 0;


    /**
     * @param pContext
     * @return version name (es 1.0.0)
     */
    public static String getAppVersionName(Context pContext) {
        String versionName = "";
        PackageInfo pInfo = null;
        try {
            pInfo = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * @param context
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Launch share intent
     *
     * @param pContext
     * @param pTextToShare
     */
    public static void sharePlainText(Context pContext, String pTextToShare) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, pTextToShare);
        sendIntent.setType("text/plain");
        pContext.startActivity(sendIntent);
    }

    /**
     * @param html
     * @return html-stripped String
     */
    public static String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    /**
     * @param context
     * @return true if connection available, else if not
     */
    public static boolean checkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /**
     * @param context
     * @return A static number that indicates the connectivity status. See HdxUtility.CONNECTIVITY_TYPE_WIFI, HdxUtility.CONNECTIVITY_TYPE_MOBILE and HdxUtility.CONNECTIVITY_TYPE_NOT_CONNECTED for all the possible values
     */
    public static int getConnectivityStatus(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return CONNECTIVITY_TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return CONNECTIVITY_TYPE_MOBILE;
        }
        return CONNECTIVITY_TYPE_NOT_CONNECTED;
    }


    /**
     * @param inputStream
     * @return the inputStream in input converted in a String object
     */
    public static String getStringFromInputStream(InputStream inputStream) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public static String md5(String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getRandomString(int maxLength) {
        return RandomStringUtils.randomAlphabetic(maxLength);
    }


    public static void openApp(final Context pContext, final String pPackageName) {
        if (pContext != null) {
            try {
                startActivity(pContext, new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(pContext, new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + pPackageName)));
            }
        }
    }


    public static void openUrl(final Context pContext, final String pUrl) {
        if (pContext != null) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pUrl));
            startActivity(pContext, browserIntent);
        }
    }

    /**
     * Open the default mail app
     * Example for send html text: HdxDialog.openEmailDefaultApp(v.getContext(), new String[] {"a.b@c.it"}, "Subject", "", "<b>testo!!</b>");
     *
     * @param pContext
     * @param pMailAddresses
     * @param pSubject
     * @param pMailText
     * @param pMailHtmlText
     */
    public static void openEmail(final Context pContext, final String[] pMailAddresses, final String pSubject, final String pMailText, final String pMailHtmlText) {

        if (pContext != null) {
            Intent intent = null;
            try {
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
            } catch (Exception e) {
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
            }

            if (pMailAddresses != null && pMailAddresses.length > 0) {
                intent.putExtra(Intent.EXTRA_EMAIL, pMailAddresses);
            }

            if (!TextUtils.isEmpty(pSubject)) {
                intent.putExtra(Intent.EXTRA_SUBJECT, pSubject);
            }

            if (!TextUtils.isEmpty(pMailHtmlText)) {
                intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(pMailHtmlText));
            } else if (!TextUtils.isEmpty(pMailText)) {
                intent.putExtra(Intent.EXTRA_TEXT, pMailText);
            }
            startActivity(pContext, intent);
        }
    }

    /**
     * This method open the system dialer for call the number passed as parameters.
     * Don't forget to put <uses-permission android:name="android.permission.CALL_PHONE" /> in your manifest
     *
     * @param pContext
     * @param pPhoneNumber
     */
    public static void openPhoneDialer(final Context pContext, final String pPhoneNumber) {
        if (pContext != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + pPhoneNumber));
            startActivity(pContext, intent);
        }
    }

    public static void openMap(final Context pContext, final double pLat, final double pLng) {
        if (pContext != null) {
            String url = "http://maps.google.com/maps?daddr=" + String.valueOf(pLat) + "," + String.valueOf(pLng);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(pContext, intent);
        }
    }

    private static void startActivity(Context pContext, Intent intent) {
        try {
            pContext.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(pContext, pContext.getString(R.string.hdx_no_application), Toast.LENGTH_SHORT).show();
        }
    }
}
