package be.kuleuven.softdev.cbhuo.vr_museum;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ImgUtils {
    //Save the file to the specified path
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        //Get internal storage state
        String state = Environment.getExternalStorageState();
        //If the status is not mounted, we cannot read or write
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }

        // First save the picture
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "vr";//"/vr_museum/pictures/"
        File appDir = new File(storePath);
        /*if (!appDir.exists()) {
            appDir.mkdir();
        }*/
        if (!appDir.isDirectory()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        String fileName1 = UUID.randomUUID().toString();
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //Use io stream to compress and save pictures
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            //Insert file into system gallery
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //Send a broadcast notification to update the database after saving the picture
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                Toast.makeText(context, "saved to local storage successfully",Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
