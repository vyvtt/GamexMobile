package com.gamex.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.gamex.R;
import com.gamex.adapters.EventDetailTabAdapter;

public class ExhibitionDetailActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appBarLayout;
    TextView txtExName;
    boolean appBarExpanded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_event_detail);

        final Toolbar toolbar = findViewById(R.id.event_detail_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txtExName = findViewById(R.id.txtExName);
        txtExName.setText("VIFA GOOD URBAN 2019 – VIFAG.U. 2019");

        appBarLayout = findViewById(R.id.event_detail_appbar);
        collapsingToolbar = findViewById(R.id.event_detail_collapse_toolbar);
        collapsingToolbar.setContentScrim(getResources().getDrawable(R.drawable.color_gradient_light));

        ViewPager viewPager = findViewById(R.id.event_detail_viewpager);
        EventDetailTabAdapter adapter = new EventDetailTabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.event_detail_tablayout);
        tabLayout.setupWithViewPager(viewPager);

        // Scroll event animation
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            Log.d(ExhibitionDetailActivity.class.getSimpleName(), "onOffsetChanged: verticalOffset: " + verticalOffset);
            //  Vertical offset == 0 indicates appBar is fully expanded.
            if (Math.abs(verticalOffset) > 200) {
                appBarExpanded = false;
                invalidateOptionsMenu();
            } else {
                appBarExpanded = true;
                invalidateOptionsMenu();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
