package com.example.ramoreserrands.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class Feedback extends AppCompatActivity {
    private EditText name_edit, suggest_edit, comment_edit;
    private String name, suggest_product, comment, created_at;
    private Button submit;
    private static String URL_FEEDBACK= "http://www.ramores.com/rema/php/send_feedback.php";
    Toast toast;
    TextView toast_text;
    ImageView toast_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar mToolbar = findViewById(R.id.toolbar);

        mToolbar.setTitle("Feedback");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        name_edit = findViewById(R.id.feedback_name_id);
        suggest_edit = findViewById(R.id.suggest_id);
        comment_edit = findViewById(R.id.comment_id);
        submit = findViewById(R.id.feedback_btn_id);

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        toast_text = (TextView) layout.findViewById(R.id.toast_text);
        toast_image = (ImageView) layout.findViewById(R.id.toast_iv);
        toast_image.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0,100);
        toast.setView(layout);//setting the view of custom toast layout

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize();
                SendFeedback();
            }
        });

    }

    public void SendFeedback(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_FEEDBACK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                toast_image.setImageResource(R.drawable.ic_check_24dp);
                                toast_text.setText("Send Success!");
                                toast.show();
                                finish();
                            }else{
                                toast_image.setImageResource(R.drawable.ic_close_24dp);
                                toast_text.setText("Failed to Send!");
                                toast.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            toast_image.setImageResource(R.drawable.ic_error_24dp);
                            toast_text.setText("Failed to Send!");
                            toast.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast_image.setImageResource(R.drawable.ic_error_24dp);
                        toast_text.setText("No Internet Connection!");
                        toast.show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("suggest_product", suggest_product);
                params.put("comment", comment);
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

    public void initialize(){

        name = name_edit.getText().toString();
        suggest_product = suggest_edit.getText().toString();
        comment = comment_edit.getText().toString();
        Date c_created_at = Calendar.getInstance().getTime();
        SimpleDateFormat df_created_at = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        created_at = df_created_at.format(c_created_at);

    }

}
