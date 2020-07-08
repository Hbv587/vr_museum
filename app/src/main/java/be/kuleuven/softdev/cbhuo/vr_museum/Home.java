package be.kuleuven.softdev.cbhuo.vr_museum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Home extends AppCompatActivity {

    private Button voiceCall;
    private MyImageTextViewNew accountImage;
    private MyImageTextViewNew callImage;
    private EditText channelEdit;
    String channelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        channelEdit = findViewById(R.id.channelEdit);

        callImage = findViewById(R.id.callImage);
        callImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),VoiceChat.class);
                channelName = channelEdit.getText().toString();
                intent.putExtra("channelName", channelName);
                startActivity(intent);
                channelEdit.setText("");
            }
        });

        accountImage = findViewById(R.id.profieImage);
        accountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UserAccount.class);
                startActivity(intent);
            }
        });
    }

}
