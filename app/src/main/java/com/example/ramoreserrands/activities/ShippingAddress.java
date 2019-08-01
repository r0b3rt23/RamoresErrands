package com.example.ramoreserrands.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ramoreserrands.R;
import com.example.ramoreserrands.adapters.DBHelper;
import com.example.ramoreserrands.model.UserInfo;

public class ShippingAddress extends AppCompatActivity {

    public EditText email, fullname, housenumber,  contactnumber;
    public Spinner barangay, city;
    public ArrayAdapter adapter_city,adapter_barangay;
    private DBHelper db;
    String email_str, name_str, housenum_str, brgy_str, city_str, contactnum_str, shipping_name,account,user_id,
            shipping_email,shipping_mobile,shipping_city,shipping_barangay,shipping_house_street,subtotal,items,user_mac,Item_display;
    int[] ids = new int[]
            {
                    R.id.email_shp,
                    R.id.fullname_shp,
                    R.id.housenum_shp,
                    R.id.contactnum_shp
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar mToolbar = findViewById(R.id.toolbar);

        mToolbar.setTitle("Delivery Address");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        db = new DBHelper(this);

        city = (Spinner) findViewById(R.id.spinner_city);
        adapter_city = ArrayAdapter.createFromResource(this, R.array.city_array, android.R.layout.simple_spinner_item);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter_city);

        barangay = (Spinner) findViewById(R.id.spinner_barangay);
        adapter_barangay = ArrayAdapter.createFromResource(this, R.array.barangay_array, android.R.layout.simple_spinner_item);
        adapter_barangay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barangay.setAdapter(adapter_barangay);

        email = (EditText) findViewById(R.id.email_shp);
        fullname = (EditText) findViewById(R.id.fullname_shp);
        housenumber = (EditText) findViewById(R.id.housenum_shp);
        contactnumber = (EditText) findViewById(R.id.contactnum_shp);


        int user_count = db.getUserCount();

        if (user_count > 0 ){
            UserInfo userinfo = db.getUserInfo();
            shipping_name = userinfo.getUser_fname() +" "+userinfo.getUser_lname();
            shipping_email = userinfo.getUser_email();
            shipping_mobile = String.valueOf(userinfo.getUser_mobile());
            shipping_city = userinfo.getUser_city();
            shipping_barangay = userinfo.getUser_barangay();
            shipping_house_street = userinfo.getUser_house_street();

            fullname.setText(shipping_name);
            email.setText(shipping_email);
            housenumber.setText(shipping_house_street);
            contactnumber.setText(shipping_mobile);
            int cityPosition = adapter_city.getPosition(shipping_city);
            city.setSelection(cityPosition);
            int barangayPosition = adapter_barangay.getPosition(shipping_barangay);
            barangay.setSelection(barangayPosition);

        }

        Button saveaddress = findViewById(R.id.continue_btn);
        saveaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getintent = getIntent();
                user_mac = getintent.getStringExtra("user_mac");
                account = getintent.getStringExtra("account");
                Item_display = getintent.getStringExtra("Item_display");
                subtotal = getintent.getStringExtra("subtotal");
                items = getintent.getStringExtra("items");

                email_str=email.getText().toString();
                name_str=fullname.getText().toString();
                housenum_str=housenumber.getText().toString();
                brgy_str=barangay.getSelectedItem().toString();
                city_str=city.getSelectedItem().toString();
                contactnum_str=contactnumber.getText().toString();
                CharSequence temp_emilID=email.getText().toString();//here username is the your edittext object...

                if(!validateEditText(ids))
                {
                    //if not empty do something
                    if(!isValidEmail(temp_emilID))
                    {
                        email.requestFocus();
                        email.setError("Enter valid email address");
                    }
                    else {

                        Intent intent = new Intent(ShippingAddress.this, Checkout.class);
                        intent.putExtra("user_mac",user_mac);
                        intent.putExtra("account",account);
                        intent.putExtra("Item_display",Item_display);
                        intent.putExtra("subtotal",subtotal);
                        intent.putExtra("items",items);
                        intent.putExtra("name",name_str);
                        intent.putExtra("email",email_str);
                        intent.putExtra("housenumber",housenum_str);
                        intent.putExtra("barangay",brgy_str);
                        intent.putExtra("city",city_str);
                        intent.putExtra("contact",contactnum_str);
                        startActivity(intent);
                    }
                }
            }
        });

    }
    public boolean validateEditText(int[] ids)
    {
        boolean isEmpty = false;

        for(int id: ids)
        {
            EditText et = (EditText)findViewById(id);

            if(TextUtils.isEmpty(et.getText().toString()))
            {
                et.setError("This field is required");
                isEmpty = true;
            }
        }

        return isEmpty;
    }

    public final static boolean isValidEmail(CharSequence target)
    {
        if (TextUtils.isEmpty(target))
        {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
