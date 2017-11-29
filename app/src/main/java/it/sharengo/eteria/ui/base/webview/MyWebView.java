package it.sharengo.eteria.ui.base.webview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;

import java.util.ArrayList;

/**
 * Created by Fulvio on 29/11/2017.
 */

public class MyWebView extends WebView {

    private int backCount=0;

    private ArrayList<String> ignoreUrls = new ArrayList<>();

    public void  setIgnoreUrls( String... urlToIgnore) {
        if(urlToIgnore!=null)
            for(String url : urlToIgnore)
                ignoreUrls.add(url.toLowerCase());
    }

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);

    }

    @Override
    public void goBack() {
        backCount++;
        super.goBack();
    }

    @Override
    public boolean canGoBack() {
        WebBackForwardList currentList = copyBackForwardList();
        int currentSize = currentList.getSize();
        boolean result=false;
        if(!canGoForward())
            backCount=0;
        for(int i = 0; i < currentSize-backCount; ++i)
        {
            WebHistoryItem item = currentList.getItemAtIndex(i);
            String url = item.getUrl();
            if(!ignoreUrls.contains(url.toLowerCase()))
                result=true;
        }
        return result;
    }
}
