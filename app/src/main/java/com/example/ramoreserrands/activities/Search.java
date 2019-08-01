package com.example.ramoreserrands.activities;

import android.app.ProgressDialog;
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
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ramoreserrands.R;
import com.example.ramoreserrands.adapters.DBHelper;
import com.example.ramoreserrands.adapters.RecyclerSearchViewAdapter;
import com.example.ramoreserrands.adapters.RecyclerViewAdapter;
import com.example.ramoreserrands.model.Product;
import com.example.ramoreserrands.model.UserInfo;

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

public class Search extends AppCompatActivity implements RecyclerSearchViewAdapter.OnItemCLickListener{

    RecyclerView recyclerView;
    private RecyclerSearchViewAdapter search_adapter;
    Toolbar toolbar;
    private List<Product> search_list_product;
    private String view_item;
    private DBHelper db;
    private BroadcastReceiver broadcastReceiver;
    TextView cart_total_item;
    String subtotal,items,macAddress,category,subcat;
    Toast toast;
    TextView toast_text;
    ImageView toast_image;
    int user_count;

    private static String URL_JSON_COUNT = "http://www.ramores.com/rema/php/get_place_order_comp.php";
    private String URL_JSON = "http://www.ramores.com/rema/php/get_all_products.php";
    private String URL_JSON_CAT = "http://www.ramores.com/rema/php/get_product_sub_category.php";

//    private JsonArrayRequest request ;
//    private RequestQueue requestQueue ;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = findViewById(R.id.search_toolbar);

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

        FloatingActionButton fab = findViewById(R.id.fab_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Search.this, Cart.class);
                startActivity(i);
            }
        });

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        String tab_label = intent.getStringExtra("tab_indicator");
        subcat = intent.getStringExtra("subcat_name");

        view_item = category;
        toolbar.setTitle(subcat);

        cart_total_item = (TextView) findViewById(R.id.cart_total);

        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.search_recycler_view);
        search_list_product = new ArrayList<>();

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Products...");

//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macAddress = getMacAddr();

        user_count = db.getUserCount();

        if (user_count > 0){
            UserInfo userinfo = db.getUserInfo();
            String user_id = String.valueOf(userinfo.getUser_id());
            InternetConnectionStatus("user",user_id,category,subcat);
        }
        else {
            InternetConnectionStatus("guest",macAddress,category,subcat);
        }

    }

//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//
//        int user_count = db.getUserCount();
//
//        if (user_count > 0){
//            UserInfo userinfo = db.getUserInfo();
//            String user_id = String.valueOf(userinfo.getUser_id());
//            InternetConnectionStatus("user",user_id,category,subcat);
//        }
//        else {
//            InternetConnectionStatus("guest",macAddress,category,subcat);
//        }
//    }

    private void jsonrequestall() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_JSON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject datalist = array.getJSONObject(i);
                                if (datalist.getString("product_available").equals("available")) {
                                    Product product = new Product();
                                    product.setProduct_id(datalist.getString("product_id"));
                                    product.setProduct_name(datalist.getString("product_name"));
                                    product.setProduct_desc(datalist.getString("product_desc"));
                                    Double price = datalist.getDouble("product_price");
                                    Double percentage = datalist.getDouble("product_percentage");

                                    Double price_percent = price * percentage;
                                    Double product_price = price_percent + price;

                                    product.setProduct_price(product_price.toString());
                                    product.setProduct_img(datalist.getString("product_img") + datalist.getString("product_name") + ".png");

                                    search_list_product.add(product);
                                }

                            }

                            setUpRecyclerView(search_list_product);

                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(Search.this,"Error1 " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Search.this,"Error2" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Category", category);
//                return params;
//            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(Search.this);
        requestQueue.add(stringRequest);

    }

    private void jsonrequest (final String category ,final String subcategory){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_JSON_CAT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject datalist = array.getJSONObject(i);

                                if (datalist.getString("product_available").equals("available")) {
                                    Product product = new Product();
                                    product.setProduct_id(datalist.getString("product_id"));
                                    product.setProduct_name(datalist.getString("product_name"));
                                    product.setProduct_desc(datalist.getString("product_desc"));
                                    Double price = datalist.getDouble("product_price");
                                    Double percentage = datalist.getDouble("product_percentage");

                                    Double price_percent = price * percentage;
                                    Double product_price = price_percent + price;

                                    product.setProduct_price(product_price.toString());
                                    product.setProduct_img(datalist.getString("product_img") + datalist.getString("product_name") + ".png");

                                    search_list_product.add(product);
                                }

                            }

                            setUpRecyclerView(search_list_product);

                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(Search.this,"Error " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Search.this,"Error " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Category", category);
                params.put("SubCategory", subcategory);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(Search.this);
        requestQueue.add(stringRequest);

    }

    private void setUpRecyclerView(List<Product> list_product_final) {

        recyclerView.setHasFixedSize(true);
        search_adapter = new RecyclerSearchViewAdapter(this,list_product_final, "product");
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(search_adapter);
        search_adapter.setOnItemClickListener(Search.this);
        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        if(view_item.equals("allproduct")){
            searchItem.expandActionView();

            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    // Do whatever you need
                    return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do whatever you need
                    finish();
                    return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
                }
            });
        }


        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setIconified(true);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        EditText txtSearch = ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        txtSearch.setTextColor(Color.WHITE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search_adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this, ItemProduct.class);
        Product clickedItem = search_list_product.get(position);

        detailIntent.putExtra("product_id", clickedItem.getProduct_id());
        detailIntent.putExtra("product_name", clickedItem.getProduct_name());
        detailIntent.putExtra("product_price", clickedItem.getProduct_price());
        detailIntent.putExtra("product_desc", clickedItem.getProduct_desc());
        detailIntent.putExtra("product_img", clickedItem.getProduct_img());
        detailIntent.putExtra("quantity", String.valueOf(1));
        startActivity(detailIntent);
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

        RequestQueue requestQueue = Volley.newRequestQueue(Search.this);
        requestQueue.add(stringRequest);

    }

    public static String formatDecimal(String value) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        return df.format(Double.valueOf(value));
    }

    private void InternetConnectionStatus(final String user_guest,final String id,final String category_s, final String subcat_s){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ( checkNetworkConnection()){
                    progressDialog.show();
                    jsonrequest_count(user_guest,id);
                    if(category_s.equals("allproduct")){
                        jsonrequestall();
                    }else{
                        jsonrequest(category_s,subcat_s);
                    }
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

    public void removeAllItem() {
        search_list_product.clear();
        search_adapter.notifyDataSetChanged();
    }
    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
