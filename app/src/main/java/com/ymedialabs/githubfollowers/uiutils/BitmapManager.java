package com.ymedialabs.githubfollowers.uiutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class BitmapManager {

    static BitmapManager bitmapManager = null;

    public static BitmapManager getInstance(){

        if(bitmapManager == null)
            bitmapManager = new BitmapManager();
        return bitmapManager;
    }

    public Bitmap downloadBitmap(String url, int width, int height) {
        try {

            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            //cache.put(url, new SoftReference<Bitmap>(bitmap));
            return bitmap;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*public void loadBitmap(final String url, final ImageView imageView,
                           final int width, final int height) {
        imageViews.put(imageView, url);
        Bitmap bitmap = getBitmapFromCache(url);

        // check in UI thread, so no concurrency issues
        if (bitmap != null) {
            Log.d(null, "Item loaded from cache: " + url);
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(placeholder);
            queueJob(url, imageView, width, height);
        }
    }*/
}
