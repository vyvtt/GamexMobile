package com.gamex.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.activity.LoginActivity;
import com.gamex.models.Exhibition;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExDetailFragment extends BaseFragment {
    @Inject
    Picasso picasso;

    private final String TAG = ExDetailFragment.class.getSimpleName();
    private TextView txtStartDateTop, txtExName, txtStartDate, txtEndDate, txtLocation, txtDescription;
    private Exhibition exhibitionFromActivity, exhibitionSaved;

    // View from activity
    private ImageView imgLogo;
    private TextView txtTitle;

    public ExDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("EXHIBITION_SAVED", exhibitionSaved);
    }

    @Override
    public void onAttach(Context context) {
        ((GamexApplication) context.getApplicationContext()).getAppComponent().inject(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        if (bundle != null) {
            exhibitionFromActivity = (Exhibition) bundle.getSerializable("EXHIBITION_DETAILS");
            Log.i(TAG, exhibitionFromActivity.toString());
        } else {
            Log.e(TAG, "No bundle from Activity");
            Toast.makeText(mActivity, "Something when wrong. Try again later.", Toast.LENGTH_LONG).show();
        }

        View view = inflater.inflate(R.layout.fragment_ex_detail, container, false);
        mappingViewElement(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            // restore of view state that had previously been frozen by onSaveInstanceState(Bundle)
            this.exhibitionSaved = (Exhibition) savedInstanceState.getSerializable("EXHIBITION_SAVED");
            updateDataToView(exhibitionSaved);
        } else {
            if (exhibitionSaved != null) {
                // Returning from back stack, data is fine, do nothing
            } else {
                // fragment create for 1st time -> get from exhibitionFromActivity
                updateDataToView(exhibitionFromActivity);
                exhibitionSaved = exhibitionFromActivity;
            }
        }
    }

    private void mappingViewElement(View view) {
        txtStartDateTop = view.findViewById(R.id.fg_ex_detail_start_date_top);
        txtExName = view.findViewById(R.id.fg_ex_detail_ex_name);
        txtStartDate = view.findViewById(R.id.fg_ex_detail_start_date);
        txtEndDate = view.findViewById(R.id.fg_ex_detail_end_date);
        txtLocation = view.findViewById(R.id.fg_ex_detail_location);
        txtDescription = view.findViewById(R.id.fg_ex_detail_description);

        imgLogo = mActivity.findViewById(R.id.event_detail_img);
        txtTitle = mActivity.findViewById(R.id.txtExName);
    }

    public void updateDataToView(Exhibition details) {
        txtStartDateTop.setText(details.getStartDate());
        txtExName.setText(details.getName());
        txtStartDate.setText(details.getStartDate());
        txtEndDate.setText(details.getEndDate());
        txtLocation.setText(Html.fromHtml("<u>" + details.getAddress() + "</u>"));
        txtDescription.setText(details.getDescription());

        picasso.load(details.getLogo())
                .placeholder((R.color.bg_grey))
                .error(R.color.bg_grey)
                .into(imgLogo);
        txtTitle.setText(details.getName());
    }

}
