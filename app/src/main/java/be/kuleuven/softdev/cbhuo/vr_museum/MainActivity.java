package be.kuleuven.softdev.cbhuo.vr_museum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    private MyImageTextViewNew loginImage;
    private TextView signup;

    boolean correct = false;
    String user_name;
    String passWord;
    int userId;
    SharedPreferences userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userData = getSharedPreferences("userData", 0);
        if(userData.getString("username", "null").equals("null"))
        {
            signup = findViewById(R.id.signupText);
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),CreateAccount.class);
                    startActivity(intent);
                }
            });

            loginImage = findViewById(R.id.loginImage);
            loginImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openHome();
                }
            });
        }
        else
        {
            Intent intent=new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(getApplicationContext(),Home.class);
            startActivity(intent);
        }

    }

    public void openHome() {
        final TextView email = findViewById(R.id.emailEdit);
        final TextView password = findViewById(R.id.passwordEdit);
        final String eMail = email.getText().toString();
        final String Pass = password.getText().toString();

//get users' info from database and check whether the info that the user enters are correct or not
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
                                String pass = jobj.getString("password");
                                String name = jobj.getString("username");
                                int userid = jobj.getInt("idUsers");
                                if(eMail.equals(Email) && Pass.equals(pass))
                                {
                                    correct = true;
                                    user_name = name;
                                    passWord=pass;
                                    userId = userid;
                                }
                            }
                            if(correct)
                            {
                                Intent intent=new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClass(getApplicationContext(),Home.class);
                                startActivity(intent);

                                //store the information of the user in shared preference
                                SharedPreferences.Editor editor = userData.edit();
                                editor.putString("username", user_name);
                                editor.putString("password",passWord);
                                editor.putString("email", eMail);
                                editor.putInt("userid", userId);
                                editor.commit();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Please check your email and password.",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "Error...",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
        correct =false;
    }
}
