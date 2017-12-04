package it.handroix.core.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HdxImageUtils {

    /**
     * This method should not work on ui thread.
     *
     * @see <a href="https://developer.android.com/training/displaying-bitmaps/load-bitmap.html">Load large bitmaps efficiently</a>
     * @param context Android context
     * @param url Image uri
     * @param reqWidth view width
     * @param reqHeight view height
     * @return Bitmap decoded
     */
    public static Bitmap decodeBitmapFromStorage(final Context context, final Uri url, final int reqWidth, final int reqHeight) throws FileNotFoundException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // First InputStream creation
        InputStream justDecodeInputStream = context.getContentResolver().openInputStream(url);
        BitmapFactory.decodeStream(justDecodeInputStream, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        // Second InputStream creation
        InputStream realInputStream = context.getContentResolver().openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(realInputStream, null, options);
        return bitmap;
    }

    /**
     * This method should not work on ui thread.
     *
     * @see <a href="https://developer.android.com/training/displaying-bitmaps/load-bitmap.html">Load large bitmaps efficiently</a>
     * @param context Android context
     * @param resId Image resource id
     * @param reqWidth view width
     * @param reqHeight view height
     * @return Bitmap decoded
     */
    public static Bitmap decodeBitmapFromResource(final Context context, final int resId, final int reqWidth, final int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        return bitmap;
    }

    public static String saveBitmapToJpegFile(String imagePath, Bitmap bitmap) {
        if(!imagePath.endsWith(".jpg") || !imagePath.endsWith(".jpg")) {
            throw new IllegalArgumentException("Image path must ends with '.jpg' or '.jpeg' suffix");
        }

        OutputStream os;
        try {
            File image = new File(imagePath);
            os = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();

            return image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void refreshGalleryImages(Context context, Uri contentUri) {
        if (contentUri != null && !contentUri.equals(Uri.EMPTY)) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
