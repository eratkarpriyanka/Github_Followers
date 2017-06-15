package com.ymedialabs.githubfollowers.uiutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.ymedialabs.githubfollowers.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    // provides methods to manage termination of tasks and methods to produce Future to track progress of async tasks
    ExecutorService executorService;

    public ImageLoader(Context context) {


        fileCache = new FileCache(context);
        // 5 active threads , any new thread gets queued
        executorService = Executors.newFixedThreadPool(5);
    }

    // set a placeholder image
    final int stub_id = R.drawable.ic_face_black_24dp;

    /**
     * check if the bitmap is already present in memory cache,
     * if not present in-memory, check if its present in file cache,
     * if not present on disk, then download from server
     */
    public void displayImage(String url, ImageView imageView) {

        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {
            queuePhoto(url, imageView);
            imageView.setImageResource(stub_id);
        }
    }

    /**
     * add new thread of retrieving photo, to queue
     * @param url
     * @param imageView
     */
    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad photoToLoad = new PhotoToLoad(url, imageView);
        // returns {@link Future} to track progress of one or more tasks
        executorService.submit(new PhotosLoader(photoToLoad));
    }

    /**
     * returns Bitmap from cachedFile if file exists, else
     * download image from server into file, then decodeFile to get scaled bitmap
     * @param url
     * @return returns Bitmap from cachedFile if file exists
     */
    private Bitmap getBitmap(String url) {
        File cachedFile = fileCache.getFile(url);

        //from SD cache
        Bitmap bitmapFromFile = decodeFile(cachedFile);
        if (bitmapFromFile != null)
            return bitmapFromFile;

        //from web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setInstanceFollowRedirects(true);
            InputStream is = connection.getInputStream();
            OutputStream os = new FileOutputStream(cachedFile);
            IoUtils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(cachedFile);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();

            // if out of memory clear the cache
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, options);

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = options.outWidth;
            int height_tmp = options.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options newOptions = new BitmapFactory.Options();
            newOptions.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, newOptions);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    /**
     * checks if the image is getting reused
     * starts the thread to download image
     * get bitmap, add it to cache
     * add image to the imageview if imageview not in use
     */
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (isImageViewReused(photoToLoad))
                return;
            Bitmap bitmap = getBitmap(photoToLoad.url);
            // add bitmap to cache
            memoryCache.put(photoToLoad.url, bitmap);
            if (isImageViewReused(photoToLoad))
                return;
            BitmapDisplayer bitmapDisplayer = new BitmapDisplayer(bitmap, photoToLoad);
            Activity activity = (Activity) photoToLoad.imageView.getContext();
            activity.runOnUiThread(bitmapDisplayer);
        }
    }

    /**
     *
     * @param photoToLoad
     * @return
     */
    boolean isImageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (isImageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
}

