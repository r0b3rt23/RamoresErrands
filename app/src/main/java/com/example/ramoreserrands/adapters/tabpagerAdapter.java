package com.example.ramoreserrands.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.ramoreserrands.fragments.AllProducts;
import com.example.ramoreserrands.fragments.Category;
import com.example.ramoreserrands.fragments.NoNetwork;

import java.util.List;

public class tabpagerAdapter extends FragmentStatePagerAdapter {

    private Integer tabnumber;
    private List<String> tabarray;
    private String network;

    public tabpagerAdapter(FragmentManager fm,Integer tabnumber,List<String> tabarray, String network) {
        super(fm);
        this.tabnumber = tabnumber;
        this.tabarray = tabarray;
        this.network = network;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabarray.get(position);
    }

    @Override
    public Fragment getItem(int i) {
        if (network.equals("connected")) {
            if (tabarray.get(i).equals("All Products")) {
                AllProducts allProducts = new AllProducts();
                return allProducts;
            } else {
                Bundle bundle = new Bundle();
                String myMessage = tabarray.get(i);
                bundle.putString("category_name", myMessage);
                Category category = new Category();
                category.setArguments(bundle);
                return category;
            }
        }
        else {
            NoNetwork noNetwork = new NoNetwork();
            return noNetwork;
        }
    }

    @Override
    public int getCount() {
        return tabnumber;
    }
}
