package com.example.ramoreserrands.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.ramoreserrands.adapters.LongOperation;
import com.example.ramoreserrands.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Checkout extends AppCompatActivity {

    public String user_id, account, email,name, house_number, barangay, city, contact,subtotal ,items, paymentbtn, payment_change ,total_price,user_mac,Item_display;
    public Float total;
    TextView delivery_address_tv,email_order_tv,contact_order_tv,order_date_tv,delivery_date_tv,subtotal_tv,items_tv,total_tv,exactamt_tv;
    EditText change_tv,remarks_tv;
    private static String URL_ORDER = "http://www.ramores.com/rema/php/placed_order.php";
    private static String URL_UPDATE = "http://www.ramores.com/rema/php/update_listcart_state.php";
    int deliverfee = 50;
    Toast toast;
    TextView toast_text;
    ImageView toast_image;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar mToolbar = findViewById(R.id.toolbar);

        mToolbar.setTitle("Checkout");
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

        progressDialog = new ProgressDialog(Checkout.this);
        progressDialog.setMessage("Sending Order...");

        Intent intent = getIntent();

        user_mac = intent.getStringExtra("user_mac");
        account = intent.getStringExtra("account");
        email = intent.getStringExtra("email");
        name = intent.getStringExtra("name");
        house_number = intent.getStringExtra("housenumber");
        barangay = intent.getStringExtra("barangay");
        city = intent.getStringExtra("city");
        contact = intent.getStringExtra("contact");
        Item_display = intent.getStringExtra("Item_display");
        subtotal = intent.getStringExtra("subtotal");
        items = intent.getStringExtra("items");

        delivery_address_tv = (TextView) findViewById(R.id.delivery_address);
        delivery_address_tv.setText(house_number+" "+barangay+", "+city);

        remarks_tv = (EditText) findViewById(R.id.remarks_order);

        email_order_tv = (TextView) findViewById(R.id.email_order);
        email_order_tv.setText(email);

        contact_order_tv = (TextView) findViewById(R.id.contactnum_order);
        contact_order_tv.setText(contact);

        String subtotal_price = formatDecimal(subtotal);

        subtotal_tv = (TextView) findViewById(R.id.subtotal_order);
        subtotal_tv.setText("₱ "+subtotal_price);

        items_tv = (TextView) findViewById(R.id.numofitems_order);
        items_tv.setText(items);

        exactamt_tv = (TextView) findViewById(R.id.deliveryfee_order);
        exactamt_tv.setText("₱ "+deliverfee);

        total =  Float.parseFloat(subtotal) + deliverfee;
        total_price = formatDecimal(total.toString());

        total_tv = (TextView) findViewById(R.id.total_order);
        total_tv.setText("₱ "+total_price);

        exactamt_tv = (TextView) findViewById(R.id.totalamt_payment);
        exactamt_tv.setText(total_price);

        change_tv = (EditText) findViewById(R.id.amount_payment);
        change_tv.setVisibility(View.INVISIBLE);

        paymentbtn = "exact_payment";
        payment_change = "No change";

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("h:mm a MMM d, yyyy");
        final String formattedDate = df.format(c);

        Calendar cal = Calendar.getInstance();
        cal.setTime(c);
        cal.add(Calendar.HOUR, 1);
        Date cdpo = cal.getTime();
        final String currentDatePlusOne = df.format(cdpo);

        order_date_tv = (TextView)findViewById(R.id.order_datetime);
        order_date_tv.setText(formattedDate);

        delivery_date_tv = (TextView) findViewById(R.id.delivery_datetime);
        delivery_date_tv.setText(currentDatePlusOne);

        Button buttonRequest = (Button) findViewById(R.id.placeorder_btn);
        //adding click listener to button
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNetworkConnection()) {
                    Date c_created_at = Calendar.getInstance().getTime();
                    SimpleDateFormat df_created_at = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                    String created_at = df_created_at.format(c_created_at);

                    String rn_date_str = created_at.replaceAll("[^0-9]","");
                    String reciept_num = "";
                    if (account.equals("user")){
                         reciept_num = "#REU"+rn_date_str;
                    }else {
                         reciept_num = "#REG"+rn_date_str;
                    }

                    String delivery_address = delivery_address_tv.getText().toString();
                    String remarks = remarks_tv.getText().toString();
                    String order_placed = formattedDate;
                    String expect_delivery = currentDatePlusOne;
                    String payment = paymentbtn;
                    if (payment.equals("exact_payment")){
                        payment_change = "No change";
                    }else {
                        payment_change = change_tv.getText().toString();
                    }
                    String price_change = payment_change;
                    String delivery_fee = String.valueOf(deliverfee);
                    String status = "order";
                    sendMail(Item_display,email,name,user_mac,account,reciept_num,delivery_address,remarks,order_placed,expect_delivery,payment,price_change,subtotal,items,delivery_fee,total_price,status,created_at);
                } else{
                    toast_image.setImageResource(R.drawable.ic_signal_wifi__off_24dp);
                    toast_text.setText("No Internet Connection.");
                    toast.show();
                }

            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.exact_payment:
                if (checked)
                        paymentbtn = "exact_payment";
                        change_tv.setVisibility(View.INVISIBLE);
                    break;
            case R.id.change_payment:
                if (checked)
                        paymentbtn = "change_payment";
                        change_tv.setVisibility(View.VISIBLE);
                    break;
        }
    }

    public void sendMail(final String Item_display,final String email,final String name, final String user_mac, final String account, final String reciept_num, final String delivery_address, final String remarks, final String order_placed, final String expect_delivery,
                         final String payment, final String price_change, final String sub_total, final String items,final String delivery_fee, final String total,final String status, final String created_at) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Checkout.this);
        alertDialogBuilder.setTitle("Place order now?");
        alertDialogBuilder.setMessage("Please wait for a call from our personnel to confirm your order.");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    progressDialog.show();
                    String emailstr = email;
                    LongOperation l = new LongOperation();
                    l.execute(emailstr,Item_display,reciept_num,name,total);  //sends the email in background
                    place_order(user_mac,account,reciept_num,delivery_address,remarks,order_placed,expect_delivery,payment,price_change,sub_total,items,delivery_fee,total,status,created_at);

                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }

        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void place_order(final String user_mac, final String account, final String reciept_num, final String delivery_address, final String remarks, final String order_placed, final String expect_delivery,
                            final String payment, final String price_change, final String sub_total, final String items, final String delivery_fee,final String total,final String status, final String created_at){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                update_cart_state(user_mac,account);
                            }else {
                                toast_image.setImageResource(R.drawable.ic_check_24dp);
                                toast_text.setText("Failed to Send");
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
                        toast_text.setText("Failed to Send!");
                        toast.show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_mac", user_mac);
                params.put("account", account);
                params.put("reciept_num", reciept_num);
                params.put("delivery_address", delivery_address);
                params.put("remarks", remarks);
                params.put("order_placed",order_placed);
                params.put("expect_delivery",expect_delivery);
                params.put("payment",payment);
                params.put("price_change",price_change);
                params.put("sub_total",sub_total);
                params.put("items",items);
                params.put("delivery_fee",delivery_fee);
                params.put("total",total);
                params.put("status",status);
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

    public void update_cart_state(final String user_mac, final String account){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                progressDialog.dismiss();
                                toast_image.setImageResource(R.drawable.ic_check_24dp);
                                toast_text.setText("Ordered Send");
                                toast.show();
                                Intent i = new Intent(Checkout.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }else {
                                toast_image.setImageResource(R.drawable.ic_check_24dp);
                                toast_text.setText("Failed to Send");
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
                        toast_text.setText("Failed to Send!");
                        toast.show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_mac", user_mac);
                params.put("account", account);
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

    public static String formatDecimal(String value) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        return df.format(Double.valueOf(value));
    }
}
