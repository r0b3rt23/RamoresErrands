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
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.ramoreserrands.R;
import com.example.ramoreserrands.adapters.DBHelper;
import com.example.ramoreserrands.adapters.tabpagerAdapter;
import com.example.ramoreserrands.fragments.NoNetwork;
import com.example.ramoreserrands.model.Category;
import com.example.ramoreserrands.model.Product;
import com.example.ramoreserrands.model.UserInfo;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DBHelper db;
    private static String URL_JSON_COUNT = "http://www.ramores.com/rema/php/get_place_order_comp.php";
    private static String URL_JSON_CATEGORY = "http://www.ramores.com/rema/php/get_category.php";
    private JsonArrayRequest request ;
    private RequestQueue requestQueue ;
    private BroadcastReceiver broadcastReceiver;
    List<String> categoryarray = new ArrayList<>();
    TextView navUsername,navEmail,cart_total_item;
    ImageView navUserPhoto;
    String subtotal,items,macAddress,Network_status;
    Toast toast;
    TextView toast_text;
    ImageView toast_image;
    RequestOptions options;
    ViewPager pager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar);

        db = new DBHelper(this);

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        toast_text = (TextView) layout.findViewById(R.id.toast_text);
        toast_image = (ImageView) layout.findViewById(R.id.toast_iv);
        toast_image.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0,100);
        toast.setView(layout);//setting the view of custom toast layout

        FloatingActionButton fabfeedbak = findViewById(R.id.feedback);
        fabfeedbak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, Feedback.class);
                startActivity(i);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, Cart.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        navUserPhoto = (ImageView) headerView.findViewById(R.id.toast_iv);
        navUsername = (TextView) headerView.findViewById(R.id.header_name);
        navEmail = (TextView) headerView.findViewById(R.id.header_email);
        cart_total_item = (TextView) findViewById(R.id.cart_item_total);

        Button logout = (Button) findViewById(R.id.logout_btn_id);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean delete = db.deleteUser();

                if (delete == true) {

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    LoginManager.getInstance().logOut();
                    finish();
                }
                else {
                    Toast.makeText(MainActivity.this,"Unable to logout", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macAddress = getMacAddr();

        int user_count = db.getUserCount();

        if (user_count > 0){
            UserInfo userinfo = db.getUserInfo();
            String user_id = String.valueOf(userinfo.getUser_id());
            String user_name = userinfo.getUser_fname() + " "+ userinfo.getUser_lname();
            String user_email = userinfo.getUser_email();
            String user_img = userinfo.getUser_img();
            if (user_email.isEmpty()){
                navigationView.getMenu().setGroupVisible(R.id.guest_account, true);
                navigationView.getMenu().setGroupVisible(R.id.client_account, false);
                logout.setVisibility(View.GONE);
            }
            else{
                navigationView.getMenu().setGroupVisible(R.id.guest_account, false);
                navigationView.getMenu().setGroupVisible(R.id.client_account, true);
                logout.setVisibility(View.VISIBLE);
                Glide.with(MainActivity.this).load(user_img).into(navUserPhoto);
                navUsername.setText(user_name);
                navEmail.setText(user_email);
            }

            InternetConnectionStatus("user",user_id);
//            jsonrequest_category("user",user_id);
        }
        else {
            InternetConnectionStatus("guest",macAddress);
//            jsonrequest_category("guest",macAddress);

            navigationView.getMenu().setGroupVisible(R.id.guest_account, true);
            navigationView.getMenu().setGroupVisible(R.id.client_account, false);
            logout.setVisibility(View.GONE);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_perm_identity_24dp);

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        int user_count = db.getUserCount();

        if (user_count > 0){
            UserInfo userinfo = db.getUserInfo();
            String user_id = String.valueOf(userinfo.getUser_id());
            String user_name = userinfo.getUser_fname() + " "+ userinfo.getUser_lname();
            String user_email = userinfo.getUser_email();
            String user_img = userinfo.getUser_img();
            if (!user_email.isEmpty()){
                navUsername.setText(user_name);
                navEmail.setText(user_email);
                Glide.with(MainActivity.this).load(user_img).apply(options).into(navUserPhoto);
            }
            ResumeInternetConnectionStatus("user",user_id);
        }
        else {
            ResumeInternetConnectionStatus("guest",macAddress);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.button_search) {

//            Intent intent = new Intent(this, Search.class);
//            startActivity(intent);
//            return true;

            if (id == R.id.button_search) {

                Intent intent = new Intent(this, Search.class);
                intent.putExtra("category","allproduct" );
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
        } else if (id == R.id.nav_create) {
            Intent i = new Intent(MainActivity.this, CreateAccount.class);
            startActivity(i);

        }  else if (id == R.id.nav_profile) {
            Intent i = new Intent(MainActivity.this, Profile.class);
            startActivity(i);

        } else if (id == R.id.nav_cart) {
            Intent i = new Intent(MainActivity.this, Cart.class);
            startActivity(i);

        } else if (id == R.id.nav_favorite) {
            Intent i = new Intent(MainActivity.this, Favorites.class);
            startActivity(i);

        } else if (id == R.id.nav_feedback) {
            Intent i = new Intent(MainActivity.this, Feedback.class);
            startActivity(i);

        } else if (id == R.id.nav_contactus) {
            Intent i = new Intent(MainActivity.this, ContactUs.class);
            startActivity(i);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void InternetConnectionStatus(final String user_guest,final String id){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tabLayout = (TabLayout) findViewById(R.id.CategoryTab);
                pager = (ViewPager) findViewById(R.id.viewpager);

                if ( checkNetworkConnection()){
                    Network_status = "connected";
                    jsonrequest_count(user_guest,id);
                    categoryarray.clear();
                    jsonrequest_category(Network_status);
                }
                else
                {
                    Network_status = "not connected";
                    categoryarray.clear();
                    categoryarray.add("");
                    tabpagerAdapter Tabpageradapter = new tabpagerAdapter(getSupportFragmentManager(),1,categoryarray,Network_status);
                    pager.setAdapter(Tabpageradapter);
                    tabLayout.setupWithViewPager(pager);

                    cart_total_item.setText("No Internet Connection");
                    toast_image.setImageResource(R.drawable.ic_signal_wifi__off_24dp);
                    toast_text.setText("No Internet Connection.");
                    toast.show();
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void ResumeInternetConnectionStatus(final String user_guest,final String id){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tabLayout = (TabLayout) findViewById(R.id.CategoryTab);
                pager = (ViewPager) findViewById(R.id.viewpager);

                if ( checkNetworkConnection()){
                    jsonrequest_count(user_guest,id);
                }
                else
                {
                    cart_total_item.setText("No Internet Connection");
                    toast_image.setImageResource(R.drawable.ic_signal_wifi__off_24dp);
                    toast_text.setText("No Internet Connection.");
                    toast.show();
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void jsonrequest_category(final String network) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_JSON_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject datalist = array.getJSONObject(i);
                                if (datalist.getString("category_available").equals("available")) {
                                    String categ_name = datalist.getString("category_name");
                                    categoryarray.add(categ_name);
                                }
                            }

                            tabpagerAdapter Tabpageradapter = new tabpagerAdapter(getSupportFragmentManager(),categoryarray.size(),categoryarray,network);
                            pager.setAdapter(Tabpageradapter);
                            tabLayout.setupWithViewPager(pager);

                            pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

                            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
                            {

                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
                                    pager.setCurrentItem(tab.getPosition());
                                }

                                @Override
                                public void onTabUnselected(TabLayout.Tab tab) {

                                }

                                @Override
                                public void onTabReselected(TabLayout.Tab tab) {
                                    switch(tab.getPosition()) {
                                        case 0:
                                            RecyclerView all_product_rv = findViewById(R.id.all_product_recycler_id);
                                            all_product_rv.smoothScrollToPosition(0);
                                    }

                                }
                            });
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this,"Error2" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Error2" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

    }

    private void jsonrequest_count (final String choose, final String user_guest_id){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_JSON_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{

                            JSONObject jsonObject = new JSONObject(response);
                            subtotal = jsonObject.getString("sub_total");
                            items = jsonObject.getString("item");
                            if(subtotal.equals("null")){
                                cart_total_item.setText("(0) ITEM (TOTAL : ₱ 00.00)");
                            }
                            else {

                                String total_price = formatDecimal(subtotal);
                                cart_total_item.setText("("+items+") ITEM (TOTAL : ₱ "+total_price+")");
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("choose", choose);
                params.put("user_guest_id", user_guest_id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

    }

    public static String formatDecimal(String value) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        return df.format(Double.valueOf(value));
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

}