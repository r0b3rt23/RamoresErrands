package com.example.ramoreserrands.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.request.RequestOptions;
import com.example.ramoreserrands.R;
import com.example.ramoreserrands.adapters.DBHelper;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private EditText email_add, password;
    private Button btn_login;
    private static String URL_LOGIN = "http://www.ramores.com/rema/php/login.php";
    private static String URL_FBLOGIN = "http://www.ramores.com/rema/php/fblogin.php";
    private static String URL_REGIST = "http://www.ramores.com/rema/php/register.php";

    DBHelper rema_db;
    ProgressDialog progressDialog;
    Toast toast;
    TextView toast_text;
    ImageView toast_image;

    private LoginButton fbloginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rema_db = new DBHelper(this);
        Toolbar mToolbar = findViewById(R.id.toolbar);

        mToolbar.setTitle("Login");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        toast_text = (TextView) layout.findViewById(R.id.toast_text);
        toast_image = (ImageView) layout.findViewById(R.id.toast_iv);
        toast_image.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0,100);
        toast.setView(layout);//setting the view of custom toast layout

        TextView signupnow_tv = (TextView)findViewById(R.id.sign_up_now_id);
        signupnow_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, CreateAccount.class);
                startActivity(i);
                finish();
            }
        });

        email_add = findViewById(R.id.email_login_id);
        password = findViewById(R.id.pass_login_id);
        btn_login = findViewById(R.id.signup_btn_id);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting...");

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email_add.getText().toString();
                String mPass = password.getText().toString();

                if (!mEmail.isEmpty() && !mPass.isEmpty()){
                    progressDialog.show();
                    Login(mEmail, mPass);
                }else if(mEmail.isEmpty()){
                    email_add.setError("Please insert email");

                }else if(mPass.isEmpty()){
                    password.setError("Please insert password");
                }
            }
        });

        fbloginButton = findViewById(R.id.fb_login_button);

        fbloginButton.setPermissions(Arrays.asList("email","public_profile"));

        callbackManager = CallbackManager.Factory.create();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "your.package",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }



    }

    private void Login (final String email, final String password){

//        btn_login.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){

                                int user_id = jsonObject.getInt("user_id");
                                String mobile = jsonObject.getString("mobile");
                                String firstname = jsonObject.getString("firstname");
                                String lastname = jsonObject.getString("lastname");
                                String birthday = jsonObject.getString("birthday");
                                String gender = jsonObject.getString("gender");
                                String city = jsonObject.getString("city");
                                String barangay = jsonObject.getString("barangay");
                                String house_street = jsonObject.getString("house_street");
                                String email = jsonObject.getString("email");
                                String password = jsonObject.getString("password");

                                boolean check = rema_db.insertLoginUser(user_id,firstname,lastname,birthday,gender,city,barangay,house_street,email,mobile,password);

                                if (check == true){
                                    progressDialog.dismiss();
                                    toast_image.setImageResource(R.drawable.ic_check_24dp);
                                    toast_text.setText("Success Login!");
                                    toast.show();
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    progressDialog.dismiss();
                                    toast_image.setImageResource(R.drawable.ic_error_24dp);
                                    toast_text.setText("Failed to login");
                                    toast.show();
                                }

                            }
                            else if (success.equals("0")){
                                progressDialog.dismiss();
                                toast_image.setImageResource(R.drawable.ic_error_24dp);
                                toast_text.setText("Invalid username or password!");
                                toast.show();

                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            btn_login.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                            toast_image.setImageResource(R.drawable.ic_error_24dp);
                            toast_text.setText("Invalid username or password!");
                            toast.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btn_login.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                        toast_image.setImageResource(R.drawable.ic_error_24dp);
                        toast_text.setText("Invalid username or password!");
                        toast.show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void FBLogin (final String email, final String first_name, final String image_url){

//        btn_login.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_FBLOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(Login.this,response, Toast.LENGTH_SHORT).show();
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){

                                int user_id = jsonObject.getInt("user_id");
                                String mobile = jsonObject.getString("mobile");
                                String firstname = jsonObject.getString("firstname");
                                String lastname = jsonObject.getString("lastname");
                                String birthday = jsonObject.getString("birthday");
                                String gender = jsonObject.getString("gender");
                                String city = jsonObject.getString("city");
                                String barangay = jsonObject.getString("barangay");
                                String house_street = jsonObject.getString("house_street");
                                String email = jsonObject.getString("email");
                                String password = jsonObject.getString("password");

                                boolean check = rema_db.FBinsertLoginUser(user_id,firstname,lastname,birthday,gender,city,barangay,house_street,email,mobile,password, image_url);

                                if (check == true){
                                    progressDialog.dismiss();
                                    toast_image.setImageResource(R.drawable.ic_check_24dp);
                                    toast_text.setText("Success Login!");
                                    toast.show();
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    progressDialog.dismiss();
                                    toast_image.setImageResource(R.drawable.ic_error_24dp);
                                    toast_text.setText("Failed to login");
                                    toast.show();
                                }

                            }
                            else if (success.equals("0")){
                                progressDialog.dismiss();
                                toast_image.setImageResource(R.drawable.ic_error_24dp);
                                toast_text.setText("Invalid username or password!");
                                toast.show();

                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            btn_login.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                            toast_image.setImageResource(R.drawable.ic_error_24dp);
                            toast_text.setText("No Internet Connection!");
                            toast.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btn_login.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                        toast_image.setImageResource(R.drawable.ic_error_24dp);
                        toast_text.setText("No Internet Connection!");
                        toast.show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("firstname", first_name);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void onCreateAccount(final String first_name, final String last_name, final String email, final String image_url, final String created_at){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                FBLogin(email, first_name, image_url);
                            }else if(success.equals("2")){
                                FBLogin(email, first_name, image_url);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            toast_image.setImageResource(R.drawable.ic_error_24dp);
                            toast_text.setText("Failed to Register! 1");
                            toast.show();
                            fbloginButton.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast_image.setImageResource(R.drawable.ic_error_24dp);
                        toast_text.setText("Failed to Register! 2");
                        toast.show();
                        fbloginButton.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", first_name);
                params.put("last_name", last_name);
                params.put("email_add", email);
                params.put("created_at",created_at);

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void onStart() {
        super.onStart();
        tokenTracker.startTracking();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            loadUserProfile(accessToken);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        // We stop the tracking before destroying the activity
        tokenTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
        {

            if (currentAccessToken != null) {
                // AccessToken is not null implies user is logged in and hence we sen the GraphRequest
                loadUserProfile(currentAccessToken);

            }else{
                Toast.makeText(Login.this,"User Logged out",Toast.LENGTH_LONG).show();
            }

        }
    };

    private void loadUserProfile(AccessToken newAccessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/"+id+ "/picture?type=normal";
                    Date c_created_at = Calendar.getInstance().getTime();
                    SimpleDateFormat df_created_at = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                    String created_at = df_created_at.format(c_created_at);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();
                    onCreateAccount(first_name, last_name, email, image_url,created_at);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }


}
