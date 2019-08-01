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
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.ramoreserrands.adapters.DBHelper;
import com.example.ramoreserrands.adapters.RecyclerSearchViewAdapter;
import com.example.ramoreserrands.adapters.RecyclerViewAdapter;
import com.example.ramoreserrands.adapters.tabpagerAdapter;
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

public class Cart extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener{

    RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    Toolbar toolbar;
    private List<Product> list_product;
    private String view_item;
    ImageButton minus, plus;
    String subtotal, items,macAddress, user_mac, account;
    TextView sub_total_order_tv, number_items_tv;
    ProgressDialog progressDialog;
    private String URL_JSON = "http://www.ramores.com/rema/php/get_item_listcart.php";
    private static String URL_JSON_COUNT = "http://www.ramores.com/rema/php/get_place_order_comp.php";
    private String URL_JSON_DELETE = "http://www.ramores.com/rema/php/delete_item_cart.php";
    private DBHelper db;
    private BroadcastReceiver broadcastReceiver;
    Toast toast;
    TextView toast_text;
    ImageView toast_image;
    int user_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        progressDialog = new ProgressDialog(Cart.this);
        progressDialog.setMessage("Loading ...");

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        toast_text = (TextView) layout.findViewById(R.id.toast_text);
        toast_image = (ImageView) layout.findViewById(R.id.toast_iv);
        toast_image.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0,100);
        toast.setView(layout);//setting the view of custom toast layout

        db = new DBHelper(this);

        Toolbar mToolbar = findViewById(R.id.toolbar);

        mToolbar.setTitle("CART");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macAddress = getMacAddr();

        recyclerView = findViewById(R.id.cart_recyclerview);
        list_product = new ArrayList<>();

        sub_total_order_tv = (TextView) findViewById(R.id.total_cart);
        number_items_tv = (TextView) findViewById(R.id.quantity_cart);

        user_count = db.getUserCount();

        if (user_count > 0){
            UserInfo userinfo = db.getUserInfo();
            String user_id = String.valueOf(userinfo.getUser_id());
            InternetConnectionStatus("user",user_id);
            user_mac = user_id;
            account = "user";
        }
        else {
            InternetConnectionStatus("guest",macAddress);
            user_mac = macAddress;
            account = "guest";
        }


        Button checkout = findViewById(R.id.checkout_button);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list_product.isEmpty()){
                    toast_image.setImageResource(R.drawable.ic_shopping_cart_24dp);
                    toast_text.setText("Cart is Empty");
                    toast.show();
                }
                else {
                    String Item_display = "";
                    for (int x=0; x<list_product.size(); x++){
                        Product Item_list = list_product.get(x);
                        String temp = Item_list.getProduct_name() +"\n"+
                                      "Price: ₱" +formatDecimal(Item_list.getProduct_price())+", Quantity: "+Item_list.getQuantity()+" = Subtotal ( ₱"+formatDecimal(Item_list.getSubitem_total())+" )\n\n";
                        Item_display = Item_display + temp;
                    }
                    Intent intent = new Intent(Cart.this, ShippingAddress.class);
                    intent.putExtra("user_mac",user_mac);
                    intent.putExtra("account",account);
                    intent.putExtra("Item_display",Item_display);
                    intent.putExtra("subtotal",subtotal);
                    intent.putExtra("items",items);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        user_count = db.getUserCount();

        if (user_count > 0){
            removeAllItem();
            UserInfo userinfo = db.getUserInfo();
            String user_id = String.valueOf(userinfo.getUser_id());
            InternetConnectionStatus("user",user_id);

        }
        else {
            removeAllItem();
            InternetConnectionStatus("guest",macAddress);
        }
    }

    private void jsonrequest (final String choose, final String user_guest_id){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_JSON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            if (array.length()>0) {
                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject datalist = array.getJSONObject(i);

                                    Product product = new Product();
                                    product.setGuest_cart_id(datalist.getString("id"));
                                    product.setProduct_id(datalist.getString("product_id"));
                                    product.setProduct_name(datalist.getString("product_name"));
                                    product.setProduct_price(datalist.getString("product_price"));
                                    product.setSubitem_total(datalist.getString("subitem_total"));
                                    product.setQuantity(datalist.getString("quantity"));
                                    product.setProduct_img(datalist.getString("product_img")+datalist.getString("product_name")+".png");

                                    list_product.add(product);

                                }

                                setUpRecyclerView(list_product);
                            }
                            else {
                                toast_image.setImageResource(R.drawable.ic_shopping_cart_24dp);
                                toast_text.setText("Cart is Empty");
                                toast.show();
                                progressDialog.dismiss();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Cart.this, "Error 1 " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Cart.this,"Error " + error.toString(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(Cart.this);
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
                                number_items_tv.setText("");
                                sub_total_order_tv.setText("");
                            }
                            else {
                                String total_price = formatDecimal(subtotal);
                                number_items_tv.setText(items);
                                sub_total_order_tv.setText(total_price);
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

        RequestQueue requestQueue = Volley.newRequestQueue(Cart.this);
        requestQueue.add(stringRequest);

    }

    private void Delete (final String choose ,final String ID){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_JSON_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                toast_image.setImageResource(R.drawable.ic_delete_24dp);
                                toast_text.setText("Item Deleted");
                                toast.show();

                                db = new DBHelper(Cart.this);
                                int user_count = db.getUserCount();

                                if (user_count > 0){
                                    UserInfo userinfo = db.getUserInfo();
                                    String user_id = String.valueOf(userinfo.getUser_id());
                                    jsonrequest_count("user",user_id);
                                }
                                else {
                                    jsonrequest_count("guest",macAddress);
                                }
                            }
                            else if (success.equals("0")){
                                Toast.makeText(Cart.this,"Failed to Delete", Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(Cart.this,"Error 1" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Cart.this,"Error 2" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("choose", choose);
                params.put("ID", ID);
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

    public static String formatDecimal(String value) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        return df.format(Double.valueOf(value));
    }

    private void setUpRecyclerView(List<Product> list_product_final) {

        recyclerView.setHasFixedSize(true);
        adapter = new RecyclerViewAdapter(this, list_product_final, "cart");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        progressDialog.dismiss();
        adapter.setOnItemClickListener(Cart.this);
    }

    public void onItemClick(int position) {

        Intent detailIntent = new Intent(this, ItemProduct.class);
        Product clickedItem = list_product.get(position);

        detailIntent.putExtra("product_id", clickedItem.getProduct_id());
        detailIntent.putExtra("product_name", clickedItem.getProduct_name());
        detailIntent.putExtra("product_price", clickedItem.getProduct_price());
        detailIntent.putExtra("product_img", clickedItem.getProduct_img());
        detailIntent.putExtra("quantity", clickedItem.getQuantity());
        startActivity(detailIntent);
    }

    @Override
    public void onDeleteClick(int position) {

        Product clickeditem = list_product.get(position);
        db = new DBHelper(this);
        user_count = db.getUserCount();

        if (user_count > 0){
            Delete("user",clickeditem.getGuest_cart_id());
            removeItem(position);
        }
        else {
            Delete("guest",clickeditem.getGuest_cart_id());
            removeItem(position);
        }

    }

    public void removeItem(int position) {
        list_product.remove(position);
        adapter.notifyItemRemoved(position);
    }

    public void removeAllItem() {
        list_product.clear();
        adapter.notifyDataSetChanged();
    }

    private void InternetConnectionStatus(final String user_guest,final String id){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ImageView connection_iv = (ImageView) findViewById(R.id.no_connection_cart);
                if ( checkNetworkConnection()){
                    progressDialog.show();
                    recyclerView.setVisibility(View.VISIBLE);
                    connection_iv.setVisibility(View.GONE);
                    jsonrequest(user_guest,id);
                    jsonrequest_count(user_guest,id);
                }
                else
                {
                    recyclerView.setVisibility(View.GONE);
                    connection_iv.setVisibility(View.VISIBLE);
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

}
