package com.example.ramoreserrands.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.ramoreserrands.R;
import com.example.ramoreserrands.adapters.DBHelper;
import com.example.ramoreserrands.adapters.GlideApp;
import com.example.ramoreserrands.model.UserInfo;
import com.sun.mail.imap.Rights;

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

public class ItemProduct extends AppCompatActivity {

    ImageButton minus, plus;
    String item_id, item_name,item_price,item_desc,item_img,item_quantity;
    TextView item_name_tv,item_price_tv,item_desc_tv, price_item_tv, add_fav_tv;
    EditText item_product_price;
    private Button btn_add;
    private BroadcastReceiver broadcastReceiver;
    int minteger;
    float total = 0;
    RequestOptions options;
    private DBHelper db;
    int user_count;
    Toast toast;
    TextView toast_text;
    ImageView toast_image,imageView,fullimage;
    AlertDialog alertDialog,alertDialogFullimage;

    private static String URL_GET_ITEM = "http://www.ramores.com/rema/php/get_item_cart.php";
    private static String URL_ADD_FAV = "http://www.ramores.com/rema/php/add_delete_item_fav.php";
    private String URL_GET_FAV = "http://www.ramores.com/php/get_fav_status.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_product);

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        toast_text = (TextView) layout.findViewById(R.id.toast_text);
        toast_image = (ImageView) layout.findViewById(R.id.toast_iv);
        toast_image.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0,100);
        toast.setView(layout);//setting the view of custom toast layout

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        btn_add = findViewById(R.id.button);

        db = new DBHelper(this);
        user_count = db.getUserCount();

        final String macAddress = getMacAddr();

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        final String formattedDate = df.format(c);

        // Request option for Glide
        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.loading)
                .error(R.drawable.noimage);

        Toolbar mToolbar = findViewById(R.id.toolbar);

        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_close_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        item_id = intent.getStringExtra("product_id");
        item_name = intent.getStringExtra("product_name");
        item_desc = intent.getStringExtra("product_desc");
        item_price = intent.getStringExtra("product_price");
        item_img = intent.getStringExtra("product_img");
        item_quantity = intent.getStringExtra("quantity");
        minteger = Integer.parseInt(item_quantity);

        imageView = (ImageView) findViewById(R.id.item_img_id);
        imageView .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullimage();
            }
        });

        Glide.with(this).load(item_img).apply(options).into(imageView);

        add_fav_tv = (TextView) findViewById(R.id.add_fav);

        item_name_tv = (TextView) findViewById(R.id.item_name_id);
        item_name_tv.setText(item_name);

        item_desc_tv = (TextView) findViewById(R.id.item_desc_id);
        item_desc_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
//                Toast.makeText(getApplicationContext(), "Description", Toast.LENGTH_LONG).show();
            }
        });

        item_price_tv = (TextView) findViewById(R.id.item_price_id);
        String price = formatDecimal(item_price);
        item_price_tv.setText("₱ "+price);

        item_product_price = (EditText) findViewById(R.id.itemcount_id);
        item_product_price.setText(item_quantity);
        item_quantity = item_product_price.getText().toString();

        final float quantity_price = Float.parseFloat(item_quantity) * Float.parseFloat(item_price);

        price_item_tv = (TextView) findViewById(R.id.price_item_id);
        price_item_tv.setText("₱ "+quantity_price);

        total = display(Integer.parseInt(item_quantity),quantity_price);

        minus = (ImageButton) findViewById(R.id.minus_id);
        minus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        minus.setImageResource(R.drawable.ic_minus_pink);
                        if (minteger > 1) {
                            minteger = minteger - 1;
                            float quantity_price = minteger * Float.parseFloat(item_price);
                            total = display(minteger, quantity_price);
                        }else{
                            minus.setEnabled(false);
                        }
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        minus.setImageResource(R.drawable.ic_minus_24dp);
                        return true; // if you want to handle the touch event
                }
                return false;
            }

        });

        plus = (ImageButton) findViewById(R.id.plus_id);
        plus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        plus.setImageResource(R.drawable.ic_add_pink);
                        minteger = minteger + 1;
                        float quantity_price = minteger * Float.parseFloat(item_price);
                        total = display(minteger,quantity_price);
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        plus.setImageResource(R.drawable.ic_add_24dp);
                        if (minteger > 1){
                            minus.setImageResource(R.drawable.ic_minus_24dp);
                            minus.setEnabled(true);
                        }
                        return true; // if you want to handle the touch event
                }
                return false;
            }

        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast_image.setImageResource(R.drawable.ic_shopping_cart_24dp);
                toast_text.setText("Item Added to Cart");
                toast.show();
                if (user_count > 0){

                    UserInfo userinfo = db.getUserInfo();
                    String user_id = String.valueOf(userinfo.getUser_id());
                    InternetConnectionStatus("user",user_id, item_id, minteger, total, formattedDate,"pending");

                }else {
                    InternetConnectionStatus("guest",macAddress, item_id, minteger, total, formattedDate,"pending");

                }
            }
        });


        if (user_count > 0) {
            add_fav_tv.setVisibility(View.VISIBLE);
            UserInfo userinfo = db.getUserInfo();
            final String user_id = String.valueOf(userinfo.getUser_id());
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if ( checkNetworkConnection()){
                        jsonrequest_fav_Status(item_id, user_id);
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
        else {
            add_fav_tv.setVisibility(View.INVISIBLE);
        }

    }

    private void SendData (final String choose, final String user_guest_id, final String item_id, final int quantity, final float subitem_total,
                           final String Date, final String state){

        final String quantity_final = String.valueOf(quantity);
        final String subitem_total_final = String.valueOf(subitem_total);

//        btn_login.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GET_ITEM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            toast_image.setImageResource(R.drawable.ic_signal_wifi__off_24dp);
                            toast_text.setText("No Internet Connection.");
                            toast.show();
                            btn_add.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast_image.setImageResource(R.drawable.ic_signal_wifi__off_24dp);
                        toast_text.setText("No Internet Connection.");
                        toast.show();
                        btn_add.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("choose", choose);
                params.put("user_guest_id", user_guest_id);
                params.put("product_id", item_id);
                params.put("quantity", quantity_final);
                params.put("subitem_total", subitem_total_final);
                params.put("state", state);
                params.put("date", Date);

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

    private float display(int number,float quantity_price) {
        item_product_price.setText("" + number);
        String price_quantity = formatDecimal(Float.toString(quantity_price));
        price_item_tv = (TextView) findViewById(R.id.price_item_id);
        price_item_tv.setText("₱ "+price_quantity);

        return quantity_price;
    }

    public static String formatDecimal(String value) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        return df.format(Double.valueOf(value));
    }

    private void jsonrequest_fav_Status(final String productID, final String userID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GET_FAV,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                add_fav_tv.setTextColor(Color.GRAY);
                                add_fav_tv.setEnabled(false);
                            }
                            else if (success.equals("0")){

                                add_fav_tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        add_fav_tv.setTextColor(Color.GRAY);
                                        Add_to_fav(item_id, userID,"INS");
                                        add_fav_tv.setEnabled(false);

                                    }
                                });

                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            toast_image.setImageResource(R.drawable.ic_signal_wifi__off_24dp);
                            toast_text.setText("No Internet Connection.");
                            toast.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toast_image.setImageResource(R.drawable.ic_signal_wifi__off_24dp);
                        toast_text.setText("No Internet Connection.");
                        toast.show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("productID", productID);
                params.put("userID", userID);
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

    private void Add_to_fav (final String productID, final String userID, final String choose){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_FAV,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                toast_image.setImageResource(R.drawable.ic_favorite_24dp);
                                toast_text.setText("Item Added to Favorites");
                                toast.show();
                            }
                            else if (success.equals("0")){
                                toast_image.setImageResource(R.drawable.ic_error_24dp);
                                toast_text.setText("Failed to Add");
                                toast.show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(ItemProduct.this,"Error 1" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ItemProduct.this,"Error 2" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("productID", productID);
                params.put("userID", userID);
                params.put("choose", choose);
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

    private void InternetConnectionStatus(final String user_guest,final String id,final String item_id, final int minteger, final Float total, final String formattedDate, final String status){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ( checkNetworkConnection()){
                    SendData(user_guest,id, item_id, minteger, total, formattedDate,status);
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

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(ItemProduct.this).inflate(R.layout.custom_dialog, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();

        TextView desc = (TextView) dialogView.findViewById(R.id.desc_dialog);
        if (item_desc.isEmpty()){
            desc.setText("No Description.");
        }else {
            desc.setText(item_desc);
        }


        Button btnOK = (Button) dialogView.findViewById(R.id.buttonOk);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showFullimage() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroupFullimage = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogViewFullimage = LayoutInflater.from(ItemProduct.this).inflate(R.layout.full_image, viewGroupFullimage, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogViewFullimage);

        //finally creating the alert dialog and displaying it
        alertDialogFullimage = builder.create();
        alertDialogFullimage.show();

        fullimage = (ImageView) dialogViewFullimage.findViewById(R.id.full_image);
//        fullimage.setAdjustViewBounds(true);
//        fullimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        fullimage.setScaleType(ImageView.ScaleType.FIT_XY);
        GlideApp.with(this).load(item_img).centerCrop().apply(options).into(fullimage);

    }
}
