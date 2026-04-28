package com.example.photometa.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.example.photometa.R;

import java.io.InputStream;

public class ImageLoader {

    public static void loadScaledImage(ImageView target, Uri uri, int reqWidth, int reqHeight) {
        Context context = target.getContext();

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            InputStream boundsStream = context.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(boundsStream, null, options);

            if (boundsStream != null) {
                boundsStream.close();
            }

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            InputStream imageStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream, null, options);

            if (imageStream != null) {
                imageStream.close();
            }

            if (bitmap != null) {
                target.setImageBitmap(bitmap);
            } else {
                target.setImageDrawable(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            target.setImageDrawable(null);
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;

        if (height <= 0 || width <= 0) {
            return 8;
        }

        int heightRatio = (int) Math.ceil((double) height / reqHeight);
        int widthRatio = (int) Math.ceil((double) width / reqWidth);

        int sampleSize = Math.max(heightRatio, widthRatio);

        int powerOfTwo = 1;
        while (powerOfTwo < sampleSize) {
            powerOfTwo *= 2;
        }

        return powerOfTwo;
    }
}