package com.example.ramoreserrands.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.ramoreserrands.adapters.DBHelper;
import com.example.ramoreserrands.model.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class EditProfile extends AppCompatActivity {

    private static String URL_SAVE = "http://www.ramores.com/rema/php/edit_profile.php";
    private Calendar myCalendar = Calendar.getInstance();
    private EditText birthDate;
    public Spinner gender, barangay, city;
    public ArrayAdapter adapter_gender, adapter_city,adapter_barangay;
    private DBHelper db;
    private BroadcastReceiver broadcastReceiver;
    ImageView saveButton;
    ProgressDialog progressDialog;
    Toast toast;
    TextView toast_text;
    ImageView toast_image;
    int user_id;
    EditText email_edit, fname_edit,lname_edit, mobile_edit,house_street_edit;
    String user_fname_edit,user_lname_edit,user_email_edit, edit_city, edit_barangay, edit_house_street,edit_birthday,edit_mobile,edit_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        db = new DBHelper(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Profile...");

        Toolbar mToolbar = findViewById(R.id.toolbar);

        mToolbar.setTitle("Edit Profile");
        mToolbar.setNavigationIcon(R.drawable.ic_close_24dp);
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

        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                birthDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        db = new DBHelper(this);
        UserInfo userinfo = db.getUserInfo();
        user_id = userinfo.getUser_id();
        user_fname_edit = userinfo.getUser_fname();
        user_lname_edit = userinfo.getUser_lname();
        user_email_edit = userinfo.getUser_email();
        edit_mobile = userinfo.getUser_mobile();
        edit_city = userinfo.getUser_city();
        edit_barangay = userinfo.getUser_barangay();
        edit_house_street = userinfo.getUser_house_street();
        edit_birthday = userinfo.getUser_birthday();
        edit_gender = userinfo.getUser_gender();

        fname_edit = (EditText) findViewById(R.id.fname_edit);
        fname_edit.setText(user_fname_edit);

        lname_edit = (EditText) findViewById(R.id.lname_edit);
        lname_edit.setText(user_lname_edit);

        email_edit = (EditText) findViewById(R.id.email_edit);
        email_edit.setText(user_email_edit);

        mobile_edit = (EditText) findViewById(R.id.mobile_edit);
        mobile_edit.setText(edit_mobile);

        birthDate = (EditText) findViewById(R.id.bday_edit_id);
        birthDate.setText(edit_birthday);
        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EditProfile.this, datePickerListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        gender = (Spinner) findViewById(R.id.spinner_gender);
        adapter_gender = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter_gender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter_gender);
        int genderPosition = adapter_gender.getPosition(edit_gender);
        gender.setSelection(genderPosition);

        city = (Spinner) findViewById(R.id.spinner_city_edit);
        adapter_city = ArrayAdapter.createFromResource(this, R.array.city_array, android.R.layout.simple_spinner_item);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter_city);
        int cityPosition = adapter_city.getPosition(edit_city);
        city.setSelection(cityPosition);

        barangay = (Spinner) findViewById(R.id.spinner_barangay_edit);
        adapter_barangay = ArrayAdapter.createFromResource(this, R.array.barangay_array, android.R.layout.simple_spinner_item);
        adapter_barangay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barangay.setAdapter(adapter_barangay);
        int barangayPosition = adapter_barangay.getPosition(edit_barangay);
        barangay.setSelection(barangayPosition);

        house_street_edit = (EditText) findViewById(R.id.house_street_edit);
        house_street_edit.setText(edit_house_street);


        saveButton = (ImageView) findViewById(R.id.save_profile_btn);

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String s_user_id = String.valueOf(user_id);
                        String s_fname = fname_edit.getText().toString();
                        String s_lname = lname_edit.getText().toString();
                        String s_email = email_edit.getText().toString();
                        String s_mobile = mobile_edit.getText().toString();
                        String s_city = city.getSelectedItem().toString();
                        String s_barangay = barangay.getSelectedItem().toString();
                        String s_house_street = house_street_edit.getText().toString();
                        String s_gender = gender.getSelectedItem().toString();
                        String s_birthday = birthDate.getText().toString();
                        if ( checkNetworkConnection()){
                            progressDialog.show();
                            SaveDatatoServer(s_user_id,s_fname,s_lname,s_email,s_mobile,s_city,s_barangay,s_house_street,s_gender,s_birthday);
                        }
                        else
                        {
                            toast_image.setImageResource(R.drawable.ic_signal_wifi__off_24dp);
                            toast_text.setText("No Internet Connection.");
                            toast.show();
                        }
                    }
                };

                registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));




            }

        });

    }

    private void SaveDatatoServer (final String user_id_server ,final String fname_server, final String lname_server, final String email_server, final String mobile_server,
                                   final String city_server, final String barangay_server, final String house_street_server, final String gender_server , final String birthday_server){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){

                                boolean update = db.updateLoginUser(Integer.parseInt(user_id_server),fname_server,lname_server,birthday_server,gender_server,
                                        city_server,barangay_server,house_street_server,email_server,mobile_server);

                                if (update == true){
                                    progressDialog.dismiss();
                                    toast_image.setImageResource(R.drawable.ic_check_24dp);
                                    toast_text.setText("Profile Saved.");
                                    toast.show();
                                    finish();
                                }
                                else {
                                    progressDialog.dismiss();
                                    toast_image.setImageResource(R.drawable.ic_error_24dp);
                                    toast_text.setText("Failed to saved.");
                                    toast.show();
                                }
                            }
                            else if (success.equals("0")){
                                progressDialog.dismiss();
                                toast_image.setImageResource(R.drawable.ic_error_24dp);
                                toast_text.setText("Error saving data to server!");
                                toast.show();


                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                            toast_image.setImageResource(R.drawable.ic_error_24dp);
                            toast_text.setText("Error saving data to server!");
                            toast.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        toast_image.setImageResource(R.drawable.ic_error_24dp);
                        toast_text.setText("Error saving data to server!");
                        toast.show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id_server);
                params.put("firstname", fname_server);
                params.put("lastname", lname_server);
                params.put("email", email_server);
                params.put("mobile", mobile_server);
                params.put("city", city_server);
                params.put("barangay", barangay_server);
                params.put("house_street", house_street_server);
                params.put("gender", gender_server);
                params.put("birthday", birthday_server);
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

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
