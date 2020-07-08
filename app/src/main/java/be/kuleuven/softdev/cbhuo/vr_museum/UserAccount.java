package be.kuleuven.softdev.cbhuo.vr_museum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static be.kuleuven.softdev.cbhuo.vr_museum.ImgUtils.saveImageToGallery;

public class UserAccount extends AppCompatActivity {

    SharedPreferences userData;
    private ImageView logoutImage;
    TextView usernameText;
    String username;
    int userId;
    GridView pictureGrid;
    ArrayList<String> uris = new ArrayList<String>();
    List<ImagePiece> pictures = new ArrayList<ImagePiece>();
    List<String> pictureInfos = new ArrayList<String>();

    byte[] result;
    ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        userData = getSharedPreferences("userData", 0);
        username = userData.getString("username", null);
        usernameText = (TextView)findViewById(R.id.usernameText);
        usernameText.setText(username);
        userId = userData.getInt("userid", 0);

        logoutImage = findViewById(R.id.logoutImage);
        logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = userData.edit();
                editor.clear(); //clear all stored data
                editor.commit();
                Intent intent=new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        pictureGrid = (GridView)findViewById(R.id.pictureGirdView);

        readUri();

        pictureGrid.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Bitmap bmp = pictures.get(position).bitmap;
                        saveImageToGallery(UserAccount.this, bmp);

                        return false;
                    }
                });

    }

    private void readUri() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://studev.groept.be/api/a19cultvr/userPictures/"+userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray jarr = new JSONArray(response);
                            for(int i=0; i<jarr.length(); i++)
                            {
                                JSONObject jobj = jarr.getJSONObject(i);
                                String uri = jobj.getString("uri");
                                uris.add(uri);
                                String pictureInfo = jobj.getString("description");
                                pictureInfos.add(pictureInfo);
                            }
                            setPictures();
                        }
                        catch(JSONException e)
                        {
                            System.out.println(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserAccount.this, "Error...",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }

    private void setPictures() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ImagePiece> images = new ArrayList<ImagePiece>();
                for(int i=0; i<uris.size(); i++)
                {
                    ImagePiece image = new ImagePiece();
                    image.bitmap = getURLimage(uris.get(i));
                    image.index = i;
                    images.add(image);
                }
                Message msg = new Message();
                msg.what = 0;
                msg.obj = images;
                System.out.println("000");
                handle.sendMessage(msg);
            }
        }).start();
    }

    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {


            switch (msg.what) {
                case 0:
                    System.out.println("111");
                    pictures = (List<ImagePiece>)msg.obj;

                    CustomAdapterAccount customAdapter = new CustomAdapterAccount(getApplicationContext(), pictures, pictureInfos);
                    pictureGrid.setAdapter(customAdapter);
                    break;
            }
        };
    };

    public Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // get connection
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//set timeout
            conn.setDoInput(true);
            conn.setUseCaches(false);//not use cache
            conn.connect();
            InputStream is = conn.getInputStream();//get the input stream of the picture
            bmp = BitmapFactory.decodeStream(is);//read the data
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
}

