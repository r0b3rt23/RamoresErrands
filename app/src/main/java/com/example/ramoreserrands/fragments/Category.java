package com.example.ramoreserrands.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.ramoreserrands.activities.Search;
import com.example.ramoreserrands.adapters.RecyclerSearchViewAdapter;
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
public class Category extends Fragment implements RecyclerSearchViewAdapter.OnItemCLickListener{

    private RecyclerSearchViewAdapter adapter_category;
    private RecyclerView category_rv;
    private List<Product> list_subcategory = new ArrayList<>();

    private String URL_CATEGORY= "http://www.ramores.com/rema/php/get_sub_category.php";
    private String category_name_fragment;
    public Category() {
        // Category empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        list_subcategory = new ArrayList<>();

        category_rv = rootView.findViewById(R.id.category_recycler_id);

        category_name_fragment = getArguments().getString("category_name");

        jsonrequest(category_name_fragment);

        return rootView;

    }

    private void jsonrequest (final String category){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CATEGORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject datalist = array.getJSONObject(i);

                                if(datalist.getString("sub_category_available").equals("available")){
                                    Product product = new Product();
                                    product.setSubcategory_name(datalist.getString("sub_category_name"));
                                    product.setSubcategory_img(datalist.getString("sub_category_img")+datalist.getString("category_name")+"/"+datalist.getString("sub_category_name")+".png");

                                    list_subcategory.add(product);
                                }

                            }

                            setuprecyclerview(list_subcategory);

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
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Category", category);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    private void setuprecyclerview(List<Product> list_product) {

        category_rv.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        adapter_category = new RecyclerSearchViewAdapter(getContext(),list_product, "sub_category");
        category_rv.setLayoutManager(layoutManager);
        category_rv.setAdapter(adapter_category);
        adapter_category.setOnItemClickListener(Category.this);
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(getContext(), Search.class);
        Product clickedItem = list_subcategory.get(position);

        detailIntent.putExtra("subcat_name", clickedItem.getSubcategory_name());
        detailIntent.putExtra("category",category_name_fragment );
        startActivity(detailIntent);
    }
}