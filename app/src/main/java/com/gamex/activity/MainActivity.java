package com.gamex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gamex.R;
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
    int primaryColor, secondaryColor, preClickPosition;
    Toolbar toolbar;
    Drawer drawerMenu;
    boolean isInit = true;
    boolean isCloseMenuOnBackButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        primaryColor = R.color.color_primary;
        secondaryColor = R.color.color_secondary;
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar); // for getSupportActionBar in Fragment of this activity
        createDrawer();
    }

    private void createDrawer() {
        // Create the Drawer header
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withHeaderBackground(getResources().getDrawable(R.drawable.color_gradient_blue))
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName("Thuy Vy") // TODO add name + email from shared pref
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
                .withDelayOnDrawerClose(200)
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
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    // event select an menu item in drawer
                    selectDrawerItem(drawerItem);
                    return true;
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) { }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        /*
                         * Drawer will be closed by 2 ways
                         * 1. User choose an drawer item -> drawer close -> change fragment -> prevent menu lag
                         * 2. User press back button when drawer is open -> close drawer and DO NOTHING
                         * */
                        if (!isCloseMenuOnBackButton) {
                            // 1. change fragment
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.activity_main_container, fragment)
                                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                                    .commit();
                        } else {
                            // 2. do nothing ^^
                            isCloseMenuOnBackButton = false; // reset status
                        }
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) { }
                })
//                .withOnDrawerNavigationListener(clickedView -> {
//                    //this method is only called if the Arrow icon is shown. The hamburger is automatically managed by the MaterialDrawer
//                    //if the back arrow is shown. close the activity
//                    MainActivity.this.finish();
//                    //return true if we have consumed the event
//                    return true;
//                })
                .build();
        drawerMenu.setSelection(Constant.ITEM_HOME);
    }

    Fragment fragment = null;
    Class fragmentClass = null;
    private void selectDrawerItem(IDrawerItem drawerItem) {
        // itemId == current position in drawer
        int itemId = (int) drawerItem.getIdentifier();

        // 1st time open app: Home Fragment show first
        if (isInit) {
            isInit = false;
            preClickPosition = itemId;
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_main_container, new HomeFragment())
                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                    .commit();
            return;
        } else if (preClickPosition == itemId) {
            // Select the same option again -> do nothing
            return;
        }

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
        drawerMenu.closeDrawer();
        preClickPosition = itemId; //update position
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

    @Override
    public void onBackPressed() {
        Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.activity_main_container);

        if (drawerMenu != null && drawerMenu.isDrawerOpen()) {
            // Close drawer if it is open
            isCloseMenuOnBackButton = true;
            drawerMenu.closeDrawer();
        } else {
            // drawer is closed and currently at Home Fragment -> exit app
            if (fragment instanceof HomeFragment) {
                finish();
            } else { // drawer is closed, press back button wil bring user back to Home Fragment
                drawerMenu.setSelection(Constant.ITEM_HOME);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.activity_main_container, new HomeFragment())
                        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                        .commit();

//                if (getFragmentManager().getBackStackEntryCount() > 0 ){
//                    getFragmentManager().popBackStack(Constant.FG_HOME_TAG, 0);
//                } else {
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    fragmentManager.beginTransaction()
//                        .replace(R.id.activity_main_container, new HomeFragment())
//                        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
//                        .commit();
//                }


//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.activity_main_container, new HomeFragment())
//                        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
//                        .commit();
            }
        }
    }

    //    @Override
//    public void onBackPressed() {
//        // Get FrameLayout that contain the fragment inside
//        FrameLayout layoutContainer = findViewById(R.id.activity_main_container);
//
//        if (layoutContainer.getChildCount() == 1) {
//            super.onBackPressed();
//            if (layoutContainer.getChildCount() == 0) {
//                new AlertDialog.Builder(this)
//                        .setTitle("Close App?")
//                        .setMessage("Close this app?")
//                        .setPositiveButton("YES",
//                                (dialog, which) -> finish())
//                        .setNegativeButton("NO",
//                                (dialog, which) -> {
//                                }).show();
//                // load your first Fragment here
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.activity_main_container, new HomeFragment())
//                        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
//                        .addToBackStack(null)
//                        .commit();
//            }
//        } else if (layoutContainer.getChildCount() == 0) {
//            // load your first Fragment here
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.activity_main_container, new HomeFragment())
//                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
//                    .addToBackStack(null)
//                    .commit();
//        } else {
//            super.onBackPressed();
//        }
//    }
}
