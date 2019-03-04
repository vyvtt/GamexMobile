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

@SuppressWarnings("FieldCanBeLocal")
public class ExhibitionDetailActivity extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private TextView txtExName;
    private boolean appBarExpanded = true;
    private String exId, exName, exImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_event_detail);

        // bind
        final Toolbar toolbar = findViewById(R.id.event_detail_toolbar);
        appBarLayout = findViewById(R.id.event_detail_appbar);
        collapsingToolbar = findViewById(R.id.event_detail_collapse_toolbar);
        collapsingToolbar.setContentScrim(getResources().getDrawable(R.drawable.color_gradient_light));

        // add back button
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSaveData(); // ex name, img, id from intent -> set to view
        addTabAdapter();
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> animationOnScroll(verticalOffset));
    }

    private void addTabAdapter() {
        ViewPager viewPager = findViewById(R.id.event_detail_viewpager);
        EventDetailTabAdapter adapter = new EventDetailTabAdapter(getSupportFragmentManager(), exId);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.event_detail_tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void animationOnScroll(int verticalOffset) {
        Log.d(ExhibitionDetailActivity.class.getSimpleName(), "onOffsetChanged: verticalOffset: " + verticalOffset);
        //  Vertical offset == 0 indicates appBar is fully expanded.
        if (Math.abs(verticalOffset) > 200) {
            appBarExpanded = false;
            invalidateOptionsMenu();
        } else {
            appBarExpanded = true;
            invalidateOptionsMenu();
        }
    }

    private void getSaveData() {
        // TODO get exhibition id, name and logo url from Intent extra
        // TODO set exhibition info for display toolbar
        exName = getIntent().getStringExtra("EXTRA_EX_NAME");
        exId = getIntent().getStringExtra("EXTRA_EX_ID");
        exImg = getIntent().getStringExtra("EXTRA_EX_IMG");
        txtExName = findViewById(R.id.txtExName);
        txtExName.setText(exName);
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
