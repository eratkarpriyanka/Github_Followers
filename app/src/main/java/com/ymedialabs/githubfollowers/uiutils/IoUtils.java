package com.ymedialabs.githubfollowers.uiutils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class IoUtils {

    private static final String TAG = IoUtils.class.getSimpleName();

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(IOException e){
            Log.d(TAG,"IO exception in copying stream"+e);
        }
        catch (Exception e){
            Log.d(TAG,"exception in copying stream"+e);
        }
    }
}
