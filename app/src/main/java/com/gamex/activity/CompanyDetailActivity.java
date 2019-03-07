package com.gamex.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gamex.R;
import com.gamex.adapters.SurveyOverviewAdapter;
import com.gamex.models.SurveyTest;

import java.util.ArrayList;
import java.util.List;

public class CompanyDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rvListSurvey;
    private RelativeLayout btnExpand;
    private LinearLayout expandableLayout;
    private boolean isExpand = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_company_detail);

        mappingViewElement();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // expand
        expandableLayout.setVisibility(isExpand ? View.VISIBLE : View.GONE);
        btnExpand.setRotation(isExpand ? 180f : 0f);
        btnExpand.setOnClickListener(v -> onClickButtonExpand(expandableLayout, btnExpand));

        // test list survey
        rvListSurvey.setHasFixedSize(true);
        rvListSurvey.setLayoutManager(new LinearLayoutManager(getApplication()));

        List<SurveyTest> listSurvey = new ArrayList<>();
        listSurvey.add(new SurveyTest(1, "Survey Build successful!", 30, "requirements of national and international conventions, large scale public and trade exhibitions, corporate meetings and specialized events.", 10));
        listSurvey.add(new SurveyTest(1, "Technology survey?", 100, "requirements of national and international conventions, large scale public and trade exhibitions, corporate meetings and specialized events.", 1));
        listSurvey.add(new SurveyTest(1, "Survey about your life", 99, "requirements of national and international conventions, large scale public and trade exhibitions, corporate meetings and specialized events.", 8));
        listSurvey.add(new SurveyTest(1, "I'm dead inside Survey", 22, "requirements of national and international conventions, large scale public and trade exhibitions, corporate meetings and specialized events.", 5));

        rvListSurvey.setAdapter(new SurveyOverviewAdapter(listSurvey));
    }

    private void mappingViewElement() {
        toolbar = findViewById(R.id.company_detail_toolbar);
        rvListSurvey = findViewById(R.id.company_survey_list);
        btnExpand = findViewById(R.id.company_survey_btnExpand);
        expandableLayout = findViewById(R.id.company_survey_layout);
    }

    private void onClickButtonExpand(final LinearLayout expandableLayout, final RelativeLayout btnToExpanlayout) {
        //Simply set View to Gone if not expanded
        //Rotation on button layout
        if (expandableLayout.getVisibility() == View.VISIBLE) {
            createRotateAnimator(btnToExpanlayout, 180f, 0f).start();
            expandableLayout.setVisibility(View.GONE);
            isExpand = false;
        } else {
            createRotateAnimator(btnToExpanlayout, 0f, 180f).start();
            expandableLayout.setVisibility(View.VISIBLE);
            isExpand = true;
        }
    }

    //Code to rotate button
    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }
}
