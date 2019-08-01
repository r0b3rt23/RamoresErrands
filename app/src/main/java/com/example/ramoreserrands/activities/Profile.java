package com.example.ramoreserrands.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.ramoreserrands.R;
import com.example.ramoreserrands.adapters.DBHelper;
import com.example.ramoreserrands.adapters.GlideApp;
import com.example.ramoreserrands.model.UserInfo;

public class Profile extends AppCompatActivity {

    ImageView editButton, profilePhoto;
    TextView email_profile, name_profile, mobile_profile, address_profile,birthday_profile,gender_profile;
    String user_name_profile,user_email_profile, user_address, user_birthday,user_mobile,user_gender;
    private DBHelper db;
    RequestOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar);

        Toolbar mToolbar = findViewById(R.id.toolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        EditProfileButton();

        setProfile();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setProfile();
    }

    public void setProfile(){
        db = new DBHelper(this);
        UserInfo userinfo = db.getUserInfo();
        user_name_profile = userinfo.getUser_fname() +" "+ userinfo.getUser_lname();
        user_email_profile = userinfo.getUser_email();
        if(!userinfo.getUser_mobile().isEmpty()){
            user_mobile = userinfo.getUser_mobile();
        }
        if (!userinfo.getUser_house_street().isEmpty()){
            user_address = userinfo.getUser_house_street() +" "+ userinfo.getUser_barangay() +", "+userinfo.getUser_city();
        }
        user_birthday = userinfo.getUser_birthday();
        user_gender = userinfo.getUser_gender();



        name_profile = (TextView) findViewById(R.id.user_profile_name);
        name_profile.setText(user_name_profile);

        email_profile = (TextView) findViewById(R.id.user_email);
        email_profile.setText(user_email_profile);

        mobile_profile = (TextView) findViewById(R.id.mobile_profile);
        mobile_profile.setText(user_mobile);

        address_profile = (TextView) findViewById(R.id.address_profile);
        address_profile.setText(user_address);

        gender_profile = (TextView) findViewById(R.id.gender_profile);
        gender_profile.setText(user_gender);

        birthday_profile = (TextView) findViewById(R.id.birthday_profile);
        birthday_profile.setText(user_birthday);

        profilePhoto = (ImageView) findViewById(R.id.user_profile_photo);

        String user_img = userinfo.getUser_img();
//        Intent intent = getIntent();
//        item_img = intent.getStringExtra("product_img");
        GlideApp.with(Profile.this).load(user_img).apply(options).into(profilePhoto);

    }

    public void EditProfileButton() {

        editButton = (ImageView) findViewById(R.id.edit_profile_btn);

        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(Profile.this, EditProfile.class);
                startActivity(i);

            }

        });

    }

}
