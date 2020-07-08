package be.kuleuven.softdev.cbhuo.vr_museum;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

class ShowPicture extends AppCompatActivity {
    ImageView selectedImage;

    byte[] res;  //receive the byte array passed
    Bitmap mImageIds ;  //bitmap after conversion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);
        selectedImage = (ImageView) findViewById(R.id.selectedImage); // init a ImageView
        Intent intent = getIntent(); // get Intent which we set from Previous Activity
        Bundle b=intent.getExtras();
        Bitmap bmp=(Bitmap) b.getParcelable("bitmap");
        selectedImage.setImageBitmap(bmp);
    }

    //convert the byte array to bitmap object
    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {

        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,  opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;

    }

}
