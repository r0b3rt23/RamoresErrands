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
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.ramoreserrands.adapters.RecyclerViewAdapter;
import com.example.ramoreserrands.model.Product;
import com.example.ramoreserrands.model.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Favorites extends AppCompatActivity {

    private String URL_JSON = "http://www.ramores.com/rema/php/get_fav_item.php";
    private static String URL_ADD_FAV = "http://www.ramores.com/rema/php/add_delete_item_fav.php";
    private List<Product> list_product = new ArrayList<>();
    private RecyclerView all_favorite_rv ;
    private RecyclerViewAdapter adapter;
    ProgressDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;
    private DBHelper db;
    Toast toast;
    TextView toast_text;
    ImageView toast_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar mToolbar = findViewById(R.id.toolbar);

        mToolbar.setTitle("Favorites");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        db = new DBHelper(this);

        UserInfo userinfo = db.getUserInfo();
        final String user_id = String.valueOf(userinfo.getUser_id());

        list_product = new ArrayList<>();

        all_favorite_rv = findViewById(R.id.favorite_recyclerview);

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        toast_text = (TextView) layout.findViewById(R.id.toast_text);
        toast_image = (ImageView) layout.findViewById(R.id.toast_iv);
        toast_image.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0,100);
        toast.setView(layout);//setting the view of custom toast layout

        progressDialog = new ProgressDialog(Favorites.this);
        progressDialog.setMessage("Loading Favorites...");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ImageView connection_iv = (ImageView) findViewById(R.id.no_connection_fav);
                if ( checkNetworkConnection()){
                    progressDialog.show();
                    all_favorite_rv.setVisibility(View.VISIBLE);
                    connection_iv.setVisibility(View.GONE);
                    progressDialog.show();
                    jsonrequest(user_id);
                }
                else
                {
                    all_favorite_rv.setVisibility(View.GONE);
                    connection_iv.setVisibility(View.VISIBLE);
                    toast_image.setImageResource(R.drawable.ic_signal_wifi__off_24dp);
                    toast_text.setText("No Internet Connection.");
                    toast.show();
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    private void jsonrequest (final String user_id){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_JSON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject datalist = array.getJSONObject(i);
                                Product product = new Product();
                                product.setFavorite_id(datalist.getString("favorites_id"));
                                product.setProduct_id(datalist.getString("product_id"));
                                product.setProduct_name(datalist.getString("product_name"));
                                product.setProduct_price(datalist.getString("product_price"));
                                product.setProduct_img(datalist.getString("product_img")+datalist.getString("product_name")+".png");

                                list_product.add(product);

                            }

                            setuprecyclerview(list_product);

                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(Favorites.this,"Error " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Favorites.this,"Error " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userID", user_id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(Favorites.this);
        requestQueue.add(stringRequest);

    }

    private void setuprecyclerview(final List<Product> list_product) {

        all_favorite_rv.setHasFixedSize(true);
        adapter = new RecyclerViewAdapter(Favorites.this,list_product, "fav");
        all_favorite_rv.setLayoutManager(new GridLayoutManager(Favorites.this, 2));
        all_favorite_rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent detailIntent = new Intent(Favorites.this, ItemProduct.class);
                Product clickedItem = list_product.get(position);

                detailIntent.putExtra("product_id", clickedItem.getProduct_id());
                detailIntent.putExtra("product_name", clickedItem.getProduct_name());
                detailIntent.putExtra("product_price", clickedItem.getProduct_price());
                detailIntent.putExtra("product_img", clickedItem.getProduct_img());
                detailIntent.putExtra("quantity", String.valueOf(1));
                startActivity(detailIntent);
            }

            @Override
            public void onDeleteClick(int position) {

                UserInfo userinfo = db.getUserInfo();
                final String user_id = String.valueOf(userinfo.getUser_id());

                Product clickeditem = list_product.get(position);
                Add_to_fav(clickeditem.getFavorite_id(), user_id, "DEL");
                removeItem(position);
            }
        });
        progressDialog.dismiss();

    }

    public void removeItem(int position) {
        list_product.remove(position);
        adapter.notifyItemRemoved(position);
    }

    private void Add_to_fav (final String favoritesID, final String userID, final String choose){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_FAV,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                if (choose == "INS"){
                                    toast_image.setImageResource(R.drawable.ic_shopping_cart_24dp);
                                    toast_text.setText("Item Added to Favorites");
                                    toast.show();
                                }else{
                                    toast_image.setImageResource(R.drawable.ic_close_24dp);
                                    toast_text.setText("Item Deleted");
                                    toast.show();
                                }

                            }
                            else if (success.equals("0")){

                                if (choose == "DEL"){
                                    toast_image.setImageResource(R.drawable.ic_error_24dp);
                                    toast_text.setText("Failed to add");
                                    toast.show();
                                }else{
                                    toast_image.setImageResource(R.drawable.ic_error_24dp);
                                    toast_text.setText("Failed to delete");
                                    toast.show();
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(Favorites.this,"Error 1" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Favorites.this,"Error 2" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("choose", choose);
                params.put("userID", userID);
                params.put("favoritesID", favoritesID);
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
