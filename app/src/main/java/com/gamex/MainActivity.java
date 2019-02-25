package com.gamex;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gamex.fragments.BookmarkFragment;
import com.gamex.fragments.HomeFragment;
import com.gamex.fragments.YourExhibitionFragment;
import com.gamex.utils.Constant;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity {
    int primaryColor, secondaryColor;
    Toolbar toolbar;
    Drawer drawerMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        primaryColor = R.color.color_primary;
        secondaryColor = R.color.color_secondary;
        toolbar = findViewById(R.id.home_toolbar);
//        toolbar.setBackground(getResources().getDrawable(R.color.bg_white));
        setSupportActionBar(toolbar);
        createDrawer();
    }

    private void createDrawer() {
        // Create the Menu header
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withHeaderBackground(getResources().getDrawable(R.drawable.color_gradient_light))
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName("Thuy Vy")
                                .withEmail("thuyvyv2tv@gmail.com")
                                .withIcon(getResources().getDrawable(R.drawable.ic_default_ava))
                        .withTextColorRes(R.color.txt_white)
                        .withTextColor(getResources().getColor(R.color.txt_white))
                )
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        SectionDrawerItem headerActivity = new SectionDrawerItem()
                .withName("Activity")
                .withTextColorRes(R.color.txt_hint)
                .withDivider(false);
        SectionDrawerItem headerProfile = new SectionDrawerItem()
                .withName("Profile")
                .withTextColorRes(R.color.txt_hint)
                .withDivider(false);

        PrimaryDrawerItem menuItemHome = new PrimaryDrawerItem()
                .withIdentifier(Constant.ITEM_HOME)
                .withName("Home")
                .withIcon(getResources().getDrawable(R.drawable.ic_home))
                .withSelectedIconColorRes(secondaryColor)
                .withIconTintingEnabled(true);
        PrimaryDrawerItem menuItemYourEvent = new PrimaryDrawerItem()
                .withIdentifier(Constant.ITEM_EVENT)
                .withName("Your Exhibitions")
                .withIcon(getResources().getDrawable(R.drawable.ic_event))
                .withSelectedIconColorRes(secondaryColor)
                .withIconTintingEnabled(true);
        PrimaryDrawerItem menuItemBookmark = new PrimaryDrawerItem()
                .withIdentifier(Constant.ITEM_BOOKMARK)
                .withName("Bookmark")
                .withIcon(getResources().getDrawable(R.drawable.ic_bookmark))
                .withSelectedIconColorRes(secondaryColor)
                .withIconTintingEnabled(true);
        PrimaryDrawerItem menuItemReward = new PrimaryDrawerItem()
                .withIdentifier(Constant.ITEM_REWARD)
                .withName("Exchange Reward")
                .withIcon(getResources().getDrawable(R.drawable.ic_reward))
                .withSelectedIconColorRes(secondaryColor)
                .withIconTintingEnabled(true);
        PrimaryDrawerItem menuItemHistory = new PrimaryDrawerItem()
                .withIdentifier(Constant.ITEM_HISTORY)
                .withName("Activity History")
                .withIcon(getResources().getDrawable(R.drawable.ic_history))
                .withSelectedIconColorRes(secondaryColor)
                .withIconTintingEnabled(true);
        PrimaryDrawerItem menuEditProfile = new PrimaryDrawerItem()
                .withIdentifier(Constant.ITEM_EDIT_PROFILE)
                .withName("Edit Profile")
                .withIcon(getResources().getDrawable(R.drawable.ic_edit))
                .withSelectedIconColorRes(secondaryColor)
                .withIconTintingEnabled(true);
        PrimaryDrawerItem menuItemChangePassword = new PrimaryDrawerItem()
                .withIdentifier(Constant.ITEM_CHANGE_PASSWORD)
                .withName("Change Password")
                .withIcon(getResources().getDrawable(R.drawable.ic_password))
                .withSelectedIconColorRes(secondaryColor)
                .withIconTintingEnabled(true);
        PrimaryDrawerItem menuLogout = new PrimaryDrawerItem()
                .withIdentifier(Constant.ITEM_LOGOUT)
                .withName("Logout")
                .withIcon(getResources().getDrawable(R.drawable.ic_logout));

        //create the drawer and remember the `Drawer` menu object
        drawerMenu = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        headerActivity,
                        menuItemHome,
                        menuItemYourEvent,
                        menuItemBookmark,
                        menuItemReward,
                        menuItemHistory,
                        new DividerDrawerItem(),
                        headerProfile,
                        menuEditProfile,
                        menuItemChangePassword,
                        new DividerDrawerItem(),
                        menuLogout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        selectDrawerItem(drawerItem);
                        return true;
                    }
                })
                .build();
        drawerMenu.setSelection(Constant.ITEM_HOME);
    }

    private void selectDrawerItem(IDrawerItem drawerItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        int itemId = (int) drawerItem.getIdentifier();

        switch (itemId) {
            case Constant.ITEM_HOME:
                fragmentClass = HomeFragment.class;
                break;
            case Constant.ITEM_EVENT:
                fragmentClass = YourExhibitionFragment.class;
                break;
            case Constant.ITEM_BOOKMARK:
                fragmentClass = BookmarkFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main_container, fragment).commit();
        drawerMenu.closeDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add QR icon to the right side of Toolbar
        getMenuInflater().inflate(R.menu.main_menu_qr, menu);
        return true;
    }

    public void clickToScan(MenuItem item) {
        Intent intent = new Intent(this, ScanQRActivity.class);
        startActivity(intent);
    }
}
