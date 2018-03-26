package it.sharengo.eteria.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.sharengo.eteria.Environment;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ImageUtils {

    public static final String TAG = ImageUtils.class.getSimpleName();

    private static LayerDrawable mLayerDrawable;

    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .crossFade()
                .into(imageView);
    }

    public static void loadImage(ImageView imageView, String url, RequestListener<String, GlideDrawable> requestListener) {
        Glide.with(imageView.getContext())
                .load(url)
                .crossFade()
                .listener(requestListener)
                .into(imageView);
    }

    public static void loadImageCropped(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .crossFade()
                .into(imageView);
    }

    public static void loadImageCropped(ImageView imageView, String url, RequestListener<String, GlideDrawable> requestListener) {
        Glide.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .crossFade()
                .listener(requestListener)
                .into(imageView);
    }

    public static void loadImageRounded(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                .into(imageView);
    }

    public static void loadImageRounded(ImageView imageView, String url, Drawable placeholder) {
        Glide.with(imageView.getContext())
                .load(url)
                .centerCrop()
                .crossFade()
                .placeholder(placeholder)
                .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                .into(imageView);
    }

    public static void loadImageRounded(ImageView imageView, String url, int placeholder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadImageRounded(imageView, url, imageView.getContext().getDrawable(placeholder));
        }
        else {
            loadImageRounded(imageView, url, imageView.getContext().getResources().getDrawable(placeholder));
        }
    }

    public static File createImageFile() throws IOException {
        // Create an image file label
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp;
        File storageDir = getExternalStorageMediaDir();
        File image = new File (storageDir, imageFileName + ".jpg");
        return image;
    }

    private static File getExternalStorageMediaDir() {
        String subDir = android.os.Environment.DIRECTORY_PICTURES;
        File mediaStorageDir = new File(android.os.Environment.getExternalStoragePublicDirectory(subDir), Environment.PICTURE_DIR);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "getExternalStorageMediaDir: failed to create directory");
            }
        }
        return mediaStorageDir;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
