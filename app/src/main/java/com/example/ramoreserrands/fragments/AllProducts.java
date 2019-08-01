package com.example.ramoreserrands.fragments;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ramoreserrands.R;
import com.example.ramoreserrands.activities.Favorites;
import com.example.ramoreserrands.activities.ItemProduct;
import com.example.ramoreserrands.activities.Search;
import com.example.ramoreserrands.adapters.RecyclerSearchViewAdapter;
import com.example.ramoreserrands.adapters.RecyclerViewAdapter;
import com.example.ramoreserrands.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllProducts extends Fragment implements RecyclerSearchViewAdapter.OnItemCLickListener{

    private String URL_JSON = "http://www.ramores.com/rema/php/get_all_products.php";
    private JsonArrayRequest request ;
    private RequestQueue requestQueue ;
    private List<Product> list_product = new ArrayList<>();
    private RecyclerView all_product_rv ;
    private RecyclerSearchViewAdapter adapter;
    private BroadcastReceiver broadcastReceiver;
    ProgressDialog progressDialog;
    Toast toast;
    TextView toast_text;
    ImageView toast_image,no_connection;

    public AllProducts() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_all_products, container, false);

        list_product = new ArrayList<>();

        all_product_rv = rootView.findViewById(R.id.all_product_recycler_id);

        LayoutInflater layoutinflater = getLayoutInflater();
        View layout = layoutinflater.inflate(R.layout.custom_toast, (ViewGroup) rootView.findViewById(R.id.custom_toast_layout_id));
        toast_text = (TextView) layout.findViewById(R.id.toast_text);
        toast_image = (ImageView) layout.findViewById(R.id.toast_iv);
        toast_image.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toast = new Toast(getActivity());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0,100);
        toast.setView(layout);//setting the view of custom toast layout

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Products...");
        progressDialog.show();
        jsonrequest();

        return rootView;
    }

    private void jsonrequest (){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_JSON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject jsonObject = array.getJSONObject(i);

                                if (jsonObject.getString("product_available").equals("available")) {
                                    Product product = new Product();
                                    product.setProduct_id(jsonObject.getString("product_id"));
                                    product.setProduct_name(jsonObject.getString("product_name"));
                                    product.setProduct_desc(jsonObject.getString("product_desc"));

                                    Double price = jsonObject.getDouble("product_price");
                                    Double percentage = jsonObject.getDouble("product_percentage");

                                    Double price_percent = price * percentage;
                                    Double product_price = price_percent + price;

                                    product.setProduct_price(product_price.toString());
                                    product.setProduct_img(jsonObject.getString("product_img") + jsonObject.getString("product_name") + ".png");

                                    list_product.add(product);
                                }

                            }

                            setuprecyclerview(list_product);

                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getActivity(),"Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"Error", Toast.LENGTH_SHORT).show();
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    private void setuprecyclerview(List<Product> list_product) {

        all_product_rv.setHasFixedSize(true);
        adapter = new RecyclerSearchViewAdapter(getContext(),list_product, "product");
        all_product_rv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        all_product_rv.setAdapter(adapter);
        adapter.setOnItemClickListener(AllProducts.this);
        progressDialog.dismiss();
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(getActivity(), ItemProduct.class);
        Product clickedItem = list_product.get(position);

        detailIntent.putExtra("product_id", clickedItem.getProduct_id());
        detailIntent.putExtra("product_name", clickedItem.getProduct_name());
        detailIntent.putExtra("product_desc", clickedItem.getProduct_desc());
        detailIntent.putExtra("product_price", clickedItem.getProduct_price());
        detailIntent.putExtra("product_img", clickedItem.getProduct_img());
        detailIntent.putExtra("quantity", String.valueOf(1));
        startActivity(detailIntent);
    }
}
