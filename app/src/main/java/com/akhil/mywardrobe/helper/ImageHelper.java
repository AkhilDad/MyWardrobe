package com.akhil.mywardrobe.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by akhil on 26/11/15.
 */
public class ImageHelper {

    public static Bitmap getImageFromPath(Context context, String path) {
        File file = new File(context.getFilesDir(), path);
        if (file != null && file.exists()) {
            try {
               return BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String saveImage(Context context, Bitmap  bitmap) {

        String savedPath = System.currentTimeMillis()+".png";
        File file = context.getFilesDir();
        if (file != null) {
            File imageFile = new File(file, savedPath);
            if (imageFile.exists()) {
                imageFile.delete();
            }
            try {
                OutputStream outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                return savedPath;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
