package com.gamex.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.R;
import com.gamex.models.SurveyTest;

import java.util.List;

public class SurveyOverviewAdapter extends RecyclerView.Adapter<SurveyOverviewAdapter.ViewHolder> {
    private List<SurveyTest> listSurvey;
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private Context context;

    public SurveyOverviewAdapter(List<SurveyTest> listSurvey) {
        this.listSurvey = listSurvey;
        for (int i = 0; i < listSurvey.size(); i++) {
            expandState.append(i, false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_expanable_survey, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);

        viewHolder.txtName.setText(listSurvey.get(i).getTitle());
        viewHolder.txtOverview.setText(Html.fromHtml("<b>"
                + listSurvey.get(i).getTotalQuestion()
                + " questions</b> with <b>"
                + listSurvey.get(i).getPoints()
                + " points</b> gain when completed."));
        viewHolder.txtDescription.setText(listSurvey.get(i).getDescription());

        viewHolder.btnTakeSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "take survey!", Toast.LENGTH_SHORT).show();
            }
        });

        //check if view is expanded
        final boolean isExpanded = expandState.get(i);
        viewHolder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        viewHolder.btnToExpandLayout.setRotation(expandState.get(i) ? 180f : 0f);
        viewHolder.btnToExpandLayout.setOnClickListener(v -> onClickButton(viewHolder.expandableLayout, viewHolder.btnToExpandLayout, i));
    }

    @Override
    public int getItemCount() {
        return listSurvey.size();
    }

    // Viewholder class ----------------------------------------------------->
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtOverview, txtDescription;
        Button btnTakeSurvey;
        RelativeLayout btnToExpandLayout;
        LinearLayout expandableLayout;

        ViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.survey_expand_name);
            txtOverview = view.findViewById(R.id.survey_expand_overview);
            txtDescription = view.findViewById(R.id.survey_expand_description);
            btnToExpandLayout = view.findViewById(R.id.survey_expand_btnExpand);
            expandableLayout = view.findViewById(R.id.survey_expand_layout);
            btnTakeSurvey = view.findViewById(R.id.btnTakeSurvey);
        }
    }

    private void onClickButton(final LinearLayout expandableLayout, final RelativeLayout btnToExpanlayout, final int i) {
        //Simply set View to Gone if not expanded
        //Rotation on button layout
        if (expandableLayout.getVisibility() == View.VISIBLE) {
            createRotateAnimator(btnToExpanlayout, 180f, 0f).start();
            expandableLayout.setVisibility(View.GONE);
        } else {
            createRotateAnimator(btnToExpanlayout, 0f, 180f).start();
            expandableLayout.setVisibility(View.VISIBLE);
            expandState.put(i, true);
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
