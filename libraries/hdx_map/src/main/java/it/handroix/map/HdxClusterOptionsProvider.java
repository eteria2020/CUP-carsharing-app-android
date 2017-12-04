/*
 * Copyright (C) 2013 Maciej Górski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.handroix.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.LruCache;

import com.androidmapsextensions.ClusterOptions;
import com.androidmapsextensions.ClusterOptionsProvider;
import com.androidmapsextensions.Marker;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.List;

import it.handroix.core.utils.HdxUiUtility;

public class HdxClusterOptionsProvider implements ClusterOptionsProvider {

    private Context mContext;

    private int mTextColorId;
    private int mBkgColorId;

    private LruCache<Integer, BitmapDescriptor> cache = new LruCache<Integer, BitmapDescriptor>(128);

    private ClusterOptions clusterOptions = new ClusterOptions().anchor(0.5f, 0.5f);

    public HdxClusterOptionsProvider(Context context, int textColorId, int bkgColorId ) {
        this.mContext = context;
        this.mTextColorId = textColorId;
        this.mBkgColorId = bkgColorId;
    }

    @Override
    public ClusterOptions getClusterOptions(List<Marker> markers) {

        int markersCount = markers.size();
        int markersGroup = 0;

        if (markersCount <= 10) {
            markersGroup = markersCount;
        } else if (markersCount > 10 && markersCount <= 20) {
            markersGroup = 11;
        } else if (markersCount > 20 && markersCount <= 50) {
            markersGroup = 12;
        } else if (markersCount > 50 && markersCount <= 100) {
            markersGroup = 13;
        } else if (markersCount > 100 && markersCount <= 200) {
            markersGroup = 14;
        } else if (markersCount > 200 && markersCount <= 400) {
            markersGroup = 15;
        } else if (markersCount > 400 && markersCount <= 1000) {
            markersGroup = 16;
        } else if (markersCount > 1000) {
            markersGroup = 17;
        }

        BitmapDescriptor cachedIcon = cache.get(markersGroup);
        if (cachedIcon != null) {
            return clusterOptions.icon(cachedIcon);
        }

        Bitmap bitmap = getCluster(mContext,markersCount);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);

        cache.put(markersGroup, icon);

        return clusterOptions.icon(icon);
    }

    private Bitmap getCluster(Context pContext, int clusterItems) {
        String text = "";
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        int bitmapWidthHeight = HdxUiUtility.fromDptoPx(pContext, 70);
        int circleCxCy = HdxUiUtility.fromDptoPx(pContext, 35);
        int circleRadius = 0;
        int paddingLeftForText = 0;
        int paddingTopForText = 0;

        if (clusterItems <= 10) {
            text = String.valueOf(clusterItems);
            paddingLeftForText = HdxUiUtility.fromDptoPx(pContext, 35);
            paddingTopForText = HdxUiUtility.fromDptoPx(pContext, 39);
            circleRadius = HdxUiUtility.fromDptoPx(pContext, 10);
        } else if (clusterItems > 10 && clusterItems <= 20) {
            text = "10+";
            paddingLeftForText = HdxUiUtility.fromDptoPx(pContext, 35);
            paddingTopForText = HdxUiUtility.fromDptoPx(pContext, 40);
            circleRadius = HdxUiUtility.fromDptoPx(pContext, 15);
        } else if (clusterItems > 20 && clusterItems <= 50) {
            text = "20+";
            paddingLeftForText = HdxUiUtility.fromDptoPx(pContext, 35);
            paddingTopForText = HdxUiUtility.fromDptoPx(pContext, 40);
            circleRadius = HdxUiUtility.fromDptoPx(pContext, 15);
        } else if (clusterItems > 50 && clusterItems <= 100) {
            text = "50+";
            paddingLeftForText = HdxUiUtility.fromDptoPx(pContext, 35);
            paddingTopForText = HdxUiUtility.fromDptoPx(pContext, 40);
            circleRadius = HdxUiUtility.fromDptoPx(pContext, 15);
        } else if (clusterItems > 100 && clusterItems <= 200) {
            text = "100+";
            paddingLeftForText = HdxUiUtility.fromDptoPx(pContext, 35);
            paddingTopForText = HdxUiUtility.fromDptoPx(pContext, 40);
            circleRadius = HdxUiUtility.fromDptoPx(pContext, 20);
        } else if (clusterItems > 200 && clusterItems <= 400) {
            text = "200+";
            paddingLeftForText = HdxUiUtility.fromDptoPx(pContext, 35);
            paddingTopForText = HdxUiUtility.fromDptoPx(pContext, 40);
            circleRadius = HdxUiUtility.fromDptoPx(pContext, 20);
        } else if (clusterItems > 400 && clusterItems <= 1000) {
            text = "400+";
            paddingLeftForText = HdxUiUtility.fromDptoPx(pContext, 35);
            paddingTopForText = HdxUiUtility.fromDptoPx(pContext, 40);
            circleRadius = HdxUiUtility.fromDptoPx(pContext, 20);
        } else if (clusterItems > 1000) {
            text = "1000+";
            paddingLeftForText = HdxUiUtility.fromDptoPx(pContext, 35);
            paddingTopForText = HdxUiUtility.fromDptoPx(pContext, 40);
            circleRadius = HdxUiUtility.fromDptoPx(pContext, 22);
        }

        Bitmap bmp = Bitmap.createBitmap(bitmapWidthHeight, bitmapWidthHeight, conf);

        Canvas canvas = new Canvas(bmp);

        Paint textPaint = new Paint();
        textPaint.setColor(pContext.getResources().getColor(mTextColorId));
        textPaint.setTextSize(HdxUiUtility.fromDptoPx(pContext, 12));
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint bgPaint = new Paint();
        bgPaint.setColor(pContext.getResources().getColor(mBkgColorId));
        bgPaint.setStrokeWidth(10);
        bgPaint.setAntiAlias(true);
        bgPaint.setShadowLayer(3f, 0, 0, pContext.getResources().getColor(mTextColorId));

        canvas.drawCircle(circleCxCy, circleCxCy, circleRadius, bgPaint);

        canvas.drawText(text, paddingLeftForText, paddingTopForText, textPaint);

        return bmp;
    }
}
