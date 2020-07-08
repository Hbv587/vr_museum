package be.kuleuven.softdev.cbhuo.vr_museum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class CreateAccount extends AppCompatActivity {

    EditText userName;
    EditText createEmail;
    EditText createPassword;
    MyImageTextViewNew createImage;
    String username, email, password;
    Boolean exist;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        createImage = findViewById(R.id.createImage);
        createImage.setOnClickListener(new View.OnClickListener() {
            //@TargetApi(Build.VERSION_CODES.O)
            //@RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                checkAccount();

            }
        });
    }

    private void checkAccount(){
        exist = false;

        userName = findViewById(R.id.userNameEdit);
        createEmail = findViewById(R.id.createEmailEdit);
        createPassword = findViewById(R.id.createPasswordEdit);

        username = userName.getText().toString();
        email = createEmail.getText().toString();
        password = createPassword.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://studev.groept.be/api/a19cultvr/userInfo";

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
                                String Email = jobj.getString("email");
                                if(email.equals(Email))
                                {
                                    exist = true;
                                    builder = new AlertDialog.Builder(CreateAccount.this);
                                    builder.setTitle("Account already existed");
                                    builder.setMessage("Please use another email address");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            userName.setText("");
                                            createEmail.setText("");
                                            createPassword.setText("");
                                        }
                                    });
                                    builder.create().show();
                                }
                            }
                            if(!exist){
                                saveInformation();
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
                Toast.makeText(CreateAccount.this, "Error...",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }
    private void saveInformation() {

            String serve_URL = "https://studev.groept.be/api/a19cultvr/userRegister/"+email+"/"+username+"/"+password;//+"/"+0;

            RequestQueue queue2 = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, serve_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        /*when users create an account successfully, give an information to
                        remind them to remember the email and password */
                            builder = new AlertDialog.Builder(CreateAccount.this);
                            builder.setTitle("Congratulations");
                            builder.setMessage("Create an account successfully");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    userName.setText("");
                                    createEmail.setText("");
                                    createPassword.setText("");
                                    //when click 'ok' return the login page
                                    openMain();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            System.out.println(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CreateAccount.this, "Error...",Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });
            queue2.add(stringRequest);
    }

    private void openMain(){

        Intent intent=new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setClass(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}
