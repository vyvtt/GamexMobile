package com.gamex.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.fragments.BookmarkFragment;
import com.gamex.fragments.ChangePasswordFragment;
import com.gamex.fragments.EditProfileFragment;
import com.gamex.fragments.HistoryFragment;
import com.gamex.fragments.HomeFragment;
import com.gamex.fragments.LeaderBoardFragment;
import com.gamex.fragments.RewardFragment;
import com.gamex.fragments.YourExhibitionFragment;
import com.gamex.utils.Constant;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    private int secondaryColor, preClickPosition;
    private Toolbar toolbar;
    private TextView txtToolBarTitle, txtLoading, txtNoInternet;
    private ProgressBar progressBar;

    private Drawer drawerMenu;
    private AccountHeader accountHeader;
    private boolean isInit = true;

    private final String TAG = MainActivity.class.getSimpleName();
    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        secondaryColor = R.color.color_secondary;
        mappingViewElement();
        setSupportActionBar(toolbar); // for getSupportActionBar in Fragment of this activity
        createDrawer();
        FirebaseMessaging.getInstance().subscribeToTopic(Constant.FIREBASE_NOTIFICATION_EXHIBITION_CHANEL);
    }

    private void mappingViewElement() {
        toolbar = findViewById(R.id.home_toolbar);
        txtToolBarTitle = findViewById(R.id.main_toolbar_title);
        txtLoading = findViewById(R.id.main_txt_loading);
        txtNoInternet = findViewById(R.id.main_txt_no_internet);
        progressBar = findViewById(R.id.main_progress_bar);
    }

    public void updateProfileInDrawer(String fullName) {
        accountHeader.updateProfile(new ProfileDrawerItem()
                .withIdentifier(111)
                .withName(fullName)
                .withIcon(getResources().getDrawable(R.drawable.ic_default_ava)));
        Log.i(TAG, "Update profile in drawer ----");
    }

    private void createDrawer() {
        // Create the Drawer header
        accountHeader = new AccountHeaderBuilder()
                .withHeaderBackground(getResources().getDrawable(R.drawable.color_gradient_blue))
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withIdentifier(111)
                                .withName(sharedPreferences.getString("FULLNAME", ""))
                                .withIcon(getResources().getDrawable(R.drawable.ic_default_ava))

                )
                .withDividerBelowHeader(true)
                .withCompactStyle(true)
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        SectionDrawerItem headerExhibition = new SectionDrawerItem()
                .withName("Exhibition")
                .withTextColorRes(R.color.txt_hint)
                .withDivider(false);
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
                .withIcon(getResources().getDrawable(R.drawable.ic_star))
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
        PrimaryDrawerItem menuItemLeaderBoard = new PrimaryDrawerItem()
                .withIdentifier(Constant.ITEM_LEADERBOARD)
                .withName("Leaderboard")
                .withIcon(getResources().getDrawable(R.drawable.ic_trophy))
                .withSelectedIconColorRes(secondaryColor)
                .withIconTintingEnabled(true);

        //create the drawer and remember the `Drawer` menu object
        drawerMenu = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withDelayOnDrawerClose(200)
                .addDrawerItems(
                        headerExhibition,
                        menuItemHome,
//                        menuItemYourEvent,
                        menuItemBookmark,
//                        new DividerDrawerItem(),
//                        headerActivity,
//                        menuItemLeaderBoard,
//                        menuItemReward,
//                        menuItemHistory,
//                        new DividerDrawerItem(),
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
                    public void onDrawerOpened(View drawerView) {
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Fragment = null when (1) close drawer on back button (2) close drawer by slide in
                        if (fragment != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.activity_main_container, fragment)
                                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                                    .commit();
                        }
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }
                })
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
            txtToolBarTitle.setText("Home");
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
                txtToolBarTitle.setText("Home");
                break;
            case Constant.ITEM_EVENT:
                fragmentClass = YourExhibitionFragment.class;
                txtToolBarTitle.setText("Your Exhibition");
                break;
            case Constant.ITEM_BOOKMARK:
                fragmentClass = BookmarkFragment.class;
                txtToolBarTitle.setText("Bookmark");
                break;
            case Constant.ITEM_EDIT_PROFILE:
                fragmentClass = EditProfileFragment.class;
                txtToolBarTitle.setText("Edit Profile");
                break;
            case Constant.ITEM_CHANGE_PASSWORD:
                fragmentClass = ChangePasswordFragment.class;
                txtToolBarTitle.setText("Change Password");
                break;
            case Constant.ITEM_HISTORY:
                fragmentClass = HistoryFragment.class;
                txtToolBarTitle.setText("Activity History");
                break;
            case Constant.ITEM_REWARD:
                fragmentClass = RewardFragment.class;
                txtToolBarTitle.setText("Exchange Reward");
                break;
            case Constant.ITEM_LEADERBOARD:
                fragmentClass = LeaderBoardFragment.class;
                txtToolBarTitle.setText("Leaderboard");
                break;
            case Constant.ITEM_LOGOUT:
                fragment = null;
                SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                dialog
                        .setTitleText("Logout")
                        .setContentText("Are you sure?")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constant.FIREBASE_NOTIFICATION_EXHIBITION_CHANEL);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();
                            dialog.dismissWithAnimation();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            this.finish();
                        })
                        .setCancelText("Cancel")
                        .setCancelClickListener(sweetAlertDialog -> {
                            dialog.dismissWithAnimation();
                            drawerMenu.closeDrawer();
                            drawerMenu.setSelection(preClickPosition);
                        });
                dialog.setCancelable(false);
                dialog.show();
                return;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }

        txtNoInternet.setVisibility(View.GONE);
        txtLoading.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        drawerMenu.closeDrawer();
        preClickPosition = itemId; //update position
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add QR icon to the right side of Toolbar
        // HIDE SCAN QR
//        getMenuInflater().inflate(R.menu.main_menu_qr, menu);
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
            fragment = null;
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
            }
        }
    }
}
