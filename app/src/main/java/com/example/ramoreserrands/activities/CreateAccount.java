package com.example.ramoreserrands.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.ramoreserrands.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CreateAccount extends AppCompatActivity {

//    private static final Pattern PASSWORD_PATTERN =
//            Pattern.compile("^" +
//                    "(?=.*[0-9])" +         //at least 1 digit
//                    //"(?=.*[a-z])" +         //at least 1 lower case letter
//                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
//                    "(?=.*[a-zA-Z])" +      //any letter
//                    //"(?=.*[@#$%^&+=])" +    //at least 1 special character
//                    "(?=\\S+$)" +           //no white spaces
//                    ".{4,}" +               //at least 4 characters
//                    "$");

    private EditText first_name, last_name, mobile_number, email_add, password, c_password;
    private String fname, lname, mobileNumber, emailAdd, passWord,confirmPassword,created_at;
    private Button btn_create;
    private static String URL_REGIST = "http://www.ramores.com/rema/php/send_register.php";
    Toast toast;
    TextView toast_text;
    ImageView toast_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Create Account");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        first_name = findViewById(R.id.fname_create_id);
        last_name = findViewById(R.id.lname_create_id);
        mobile_number = findViewById(R.id.mobile_create_id);
        email_add = findViewById(R.id.email_create_id);
        password = findViewById(R.id.pass_create_id);
        c_password = findViewById(R.id.confirmpass_create);
        btn_create = findViewById(R.id.signup_btn_id);

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        toast_text = (TextView) layout.findViewById(R.id.toast_text);
        toast_image = (ImageView) layout.findViewById(R.id.toast_iv);
        toast_image.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0,100);
        toast.setView(layout);//setting the view of custom toast layout

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        TextView signinnow_tv = (TextView)findViewById(R.id.sign_in_now_id);
        signinnow_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateAccount.this, Login.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void Register(){
//        btn_create.setVisibility(View.GONE);
        initialize();

        if(!validate() | !validateUsername() | !validatePassword()){
//            Toast.makeText(this, "Create Account FAILED!", Toast.LENGTH_SHORT).show();
        }
        else{
            onCreateAccount();
        }
    }

    public void onCreateAccount(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                toast_image.setImageResource(R.drawable.ic_check_24dp);
                                toast_text.setText("Register Success!");
                                toast.show();
                                Intent i = new Intent(CreateAccount.this, Login.class);
                                startActivity(i);
                                finish();
                            }else if(success.equals("2")){
                                email_add.setError("Email Already Exist!");
                                toast_image.setImageResource(R.drawable.ic_close_24dp);
                                toast_text.setText("Email Already Exist!");
                                toast.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            toast_image.setImageResource(R.drawable.ic_error_24dp);
                            toast_text.setText("Failed to Register!");
                            toast.show();
                            btn_create.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast_image.setImageResource(R.drawable.ic_error_24dp);
                        toast_text.setText("No Internet Connection!");
                        toast.show();
                        btn_create.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", fname);
                params.put("last_name", lname);
                params.put("mobile_number", mobileNumber);
                params.put("email_add", emailAdd);
                params.put("password",passWord);
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

    public boolean validate(){
        boolean valid = true;

        if(emailAdd.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailAdd).matches()){
            email_add.setError("Please Enter valid Email Address");
            valid = false;
        }
        if(mobileNumber.isEmpty()){
            mobile_number.setError("Please Enter Phone Number");
            valid = false;
        }
        return valid;
    }

    private boolean validateUsername() {
        boolean valid = true;

        if (fname.isEmpty()) {
            first_name.setError("Field can't be empty");
            valid = false;
        }if (lname.isEmpty()) {
            last_name.setError("Field can't be empty");
            valid = false;
        }
//        }else{
//            first_name.setError(null);
//            last_name.setError(null);
//            valid = true;
//        }
        return valid;
    }

    private boolean validatePassword() {
        boolean valid = true;
        if (passWord.isEmpty()) {
            password.setError("Field can't be empty");
            valid = false;
        }if(confirmPassword.isEmpty()){
            c_password.setError("Field can't be empty");
            valid = false;
        }if(!(passWord.equals(confirmPassword))){
            c_password.setError("Password does not match");
            valid = false;
        }
//        if (!PASSWORD_PATTERN.matcher(passWord).matches()) {
//            password.setError("Password too weak");
//            valid = false;
//        }else{
//            password.setError(null);
//            c_password.setError(null);
//            valid = true;
//        }
        return valid;
    }

    public void initialize(){

        fname = first_name.getText().toString().trim();
        lname = last_name.getText().toString().trim();
        mobileNumber = mobile_number.getText().toString().trim();
        emailAdd = email_add.getText().toString().trim();
        passWord = password.getText().toString().trim();
        confirmPassword = c_password.getText().toString().trim();
        Date c_created_at = Calendar.getInstance().getTime();
        SimpleDateFormat df_created_at = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        created_at = df_created_at.format(c_created_at);

    }
}
