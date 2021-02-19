package com.pandey.saurabh.dnote.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.pandey.saurabh.dnote.R;
import com.pandey.saurabh.dnote.ui.main.view.AudioNoteActivity;
import com.pandey.saurabh.dnote.ui.main.view.MainActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity {

    Drawer result;
    ViewGroup customSideMenu;
    TextView tvUserSortName;
    TextView tvEmail, tvUserName;
    CircleImageView imgUserProfile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    public void initBack() {


        ((ImageView) findViewById(R.id.imgBack)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.imgBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public BaseActivity getActivity() {
        return this;
    }


    public void initDrawer(final boolean b) {

        View v = (View) findViewById(R.id.container);
        if (b) {


             customSideMenu = (ViewGroup) getLayoutInflater().inflate(R.layout.side_menu, null, false);
            View llHome = (LinearLayout) customSideMenu.findViewById(R.id.llHome);

            View llNotes = (LinearLayout)customSideMenu.findViewById(R.id.llNotes);

            llNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            });


            llHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), AudioNoteActivity.class);
                    startActivity(intent);
                }
            });

            View navHeader = (View) customSideMenu.findViewById(R.id.navHeader);

            tvEmail = (TextView) navHeader.findViewById(R.id.tvEmail);
             tvUserName = (TextView) navHeader.findViewById(R.id.tvUserName);

            String name = "Hello Test";
            tvUserName.setText(name);
            String number = "123456";
            tvEmail.setText(number);


            CircleImageView imgUserProfile = (CircleImageView) navHeader.findViewById(R.id.imgUserProfile);

            imgUserProfile.setImageResource(R.drawable.default_avatar);
            imgUserProfile.setVisibility(View.GONE);
            imgUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });




            Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
            setSupportActionBar(toolbar);


             result = new DrawerBuilder()
                    .withActivity(this).withCloseOnClick(true).withSelectedItemByPosition(-1)
                    .withCustomView(customSideMenu)
                    .withOnDrawerListener(new Drawer.OnDrawerListener() {
                        @Override
                        public void onDrawerOpened(View drawerView) {
                           hideKeyboardFrom(getActivity(), getWindow().getDecorView());
                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {

                        }

                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {

                        }
                    })
                    .withDrawerWidthDp(300)
                    .build();


            final ImageView imgMenu = (ImageView) findViewById(R.id.imgMenu);

            if (imgMenu != null) {
                imgMenu.setVisibility(View.VISIBLE);
                imgMenu.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        hideKeyboardFrom(getActivity(), imgMenu);

                        if (result.isDrawerOpen()) {
                            result.closeDrawer();
                        } else {
                            result.openDrawer();
                        }
                    }
                });

            }


        } else {
            ImageView imgMenu = (ImageView) findViewById(R.id.imgMenu);
            imgMenu.setVisibility(View.VISIBLE);
        }

    }


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
