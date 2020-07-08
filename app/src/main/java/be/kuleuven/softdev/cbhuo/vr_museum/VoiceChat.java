package be.kuleuven.softdev.cbhuo.vr_museum;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static be.kuleuven.softdev.cbhuo.vr_museum.ImageSplitter.split;
import static be.kuleuven.softdev.cbhuo.vr_museum.ImageSplitter.splitInit;

public class VoiceChat extends AppCompatActivity {

    SharedPreferences userData;
    GridView simpleGrid;
    List<ImagePiece> pieces = new ArrayList<ImagePiece>(2 * 2);
    Bitmap picture;

    EditText codeEdit;
    ImageView submit;
    ImageView save;
    String code;
    String uri;
    boolean codeCorrect = false;
    int pieceCount = 0;
    int userId, idPictures;

    String channelName = "voiceDemoChannel1"; //default value

    private static final String LOG_TAG = VoiceChat.class.getSimpleName();

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;

    private RtcEngine mRtcEngine; // Step 1
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Step 1

        @Override
        public void onUserOffline(final int uid, final int reason) { // Step 4
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft(uid, reason);
                }
            });
        }

        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) { // Step 6
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVoiceMuted(uid, muted);
                }
            });
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat);

        Intent intent=getIntent();
        channelName = intent.getStringExtra("channelName");

        userData = getSharedPreferences("userData", 0);
        userId = userData.getInt("userid", 0);

        simpleGrid = (GridView) findViewById(R.id.simpleGridView); // init GridView

        Bitmap grey = ((BitmapDrawable)getDrawable(R.drawable.grey)).getBitmap();
        pieces = splitInit(grey, 2, 2);
        // Create an object of CustomAdapter and set Adapter to GirdView
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), pieces);
        simpleGrid.setAdapter(customAdapter);

        codeEdit = (EditText)findViewById(R.id.codeEdit);
        submit = findViewById(R.id.submitImage);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = codeEdit.getText().toString();
                codeEdit.setText("");
                getPicture();
            }
        });

        save = findViewById(R.id.saveImage);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pieceCount >= 4)
                {
                    saveImage();
                    saveImageVR();
                }
                else
                {
                    Toast.makeText(VoiceChat.this, "incomplete pieces",Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initAgoraEngineAndJoinChannel();
        }
    }

    private void saveImage() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://studev.groept.be/api/a19cultvr/saveImage/"+userId+"/"+idPictures;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(VoiceChat.this, "saved to your account successfully",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VoiceChat.this, "Error...",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }

    private void saveImageVR() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://studev.groept.be/api/a19cultvr/saveImageVR/"+channelName+"/"+idPictures;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(VoiceChat.this, "saved to your account successfully",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VoiceChat.this, "Error...",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }

    private void getPicture() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://studev.groept.be/api/a19cultvr/codePictures/"+code+"/"+code+"/"+code+"/"+code;

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
                                idPictures = jobj.getInt("idPictures");
                                uri = jobj.getString("uri");

                                if(jarr.length() != 0)
                                {
                                    codeCorrect = true;
                                    setPictures();
                                    pieceCount++;
                                }
                            }
                        }
                        catch(JSONException e)
                        {
                            System.out.println(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VoiceChat.this, "Error...",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
        codeCorrect =false;
    }

    private void setPictures() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //uri = "https://a19cultvr.studev.groept.be/pictures/mona_lisa.jpg";
                Bitmap bmp = getURLimage(uri);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = bmp;
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
                    Bitmap bmp=(Bitmap)msg.obj;
                    picture = bmp;
                    switch(code.substring(0, 1)){
                        case "1": pieces = split(bmp, 0, 0, pieces); break;
                        case "2": pieces = split(bmp, 0, 1, pieces); break;
                        case "3": pieces = split(bmp, 1, 0, pieces); break;
                        case "4": pieces = split(bmp, 1, 1, pieces); break;
                        default: break;
                }
                    CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), pieces);
                    simpleGrid.setAdapter(customAdapter);
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
            InputStream is = conn.getInputStream();//get the streame of the picture
            bmp = BitmapFactory.decodeStream(is);//read the data
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();     // Step 1
        joinChannel();               // Step 2
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }

    // Step 7
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    // Step 5
    public void onSwitchSpeakerphoneClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.setEnableSpeakerphone(view.isSelected());
    }

    // Step 3
    public void onEncCallClicked(View view) {
        finish();
    }

    // Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Step 2
    private void joinChannel() {
        String accessToken = getString(R.string.agora_access_token);
        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "#YOUR ACCESS TOKEN#")) {
            accessToken = null; // default, no token
        }

        mRtcEngine.joinChannel(accessToken, channelName, "Extra Optional Data", 0);
    }

    //  Step 3
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    // Step 4
    private void onRemoteUserLeft(int uid, int reason) {
        showLongToast(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
    }

    //  Step 6
    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
        showLongToast(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
    }
}
