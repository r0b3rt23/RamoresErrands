package com.example.ramoreserrands.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Brands extends Fragment implements RecyclerSearchViewAdapter.OnItemCLickListener{

    private RecyclerSearchViewAdapter adapter_brands;
    private RecyclerView brands_rv;
    private List<Product> list_brands = new ArrayList<>();

    private String URL_BRANDS= "http://www.ramores.com/rema/php/get_brands.php";
    private String category_name_fragment;

    public Brands() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_brands, container, false);

        list_brands = new ArrayList<>();

        brands_rv = rootView.findViewById(R.id.brands_recycler_id);

        category_name_fragment = getArguments().getString("category_name");

        jsonrequest();

        return rootView;
    }

    private void jsonrequest (){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_BRANDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject datalist = array.getJSONObject(i);

                                if(datalist.getString("brand_available").equals("available")){
                                    Product brand = new Product();
                                    brand.setBrand_name(datalist.getString("brand_name"));
                                    brand.setBrand_img(datalist.getString("brand_img")+datalist.getString("brand_name")+".png");

                                    list_brands.add(brand);
                                }

                            }

                            setuprecyclerview(list_brands);

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

    private void setuprecyclerview(List<Product> list_brand) {

        brands_rv.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        adapter_brands = new RecyclerSearchViewAdapter(getContext(),list_brand, "brands");
        brands_rv.setLayoutManager(layoutManager);
        brands_rv.setAdapter(adapter_brands);
        adapter_brands.setOnItemClickListener(Brands.this);
    }

    @Override
    public void onItemClick(int position) {

        Intent detailIntent = new Intent(getContext(), Search.class);
        Product clickedItem = list_brands.get(position);

        detailIntent.putExtra("subcat_name", clickedItem.getBrand_name());
        detailIntent.putExtra("category",category_name_fragment );
        startActivity(detailIntent);
    }
}
