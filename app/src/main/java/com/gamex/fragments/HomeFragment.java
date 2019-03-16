package com.gamex.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.R;
import com.gamex.activity.ViewAllExhibitionActivity;
import com.gamex.adapters.HomeAdapter;
import com.gamex.models.Exhibition;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.CheckInternetTask;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    @Inject
    @Named("cache")
    DataService dataService;
    Call<List<Exhibition>> callOngoing;
    Call<List<Exhibition>> callUpcoming;
    Call<List<Exhibition>> callNear;
    @Inject
    SharedPreferences sharedPreferences;
    private String accessToken;
    private boolean isLoadingOngoing, isLoadingUpcoming, isLoadingNear;
    private HashMap<String, Object> apiParam;

    private final String TAG = HomeFragment.class.getSimpleName();
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView rvOngoing, rvNear, rvUpcoming;
    private TextView txtToolBarTitle, txtNoInternet, txtLoading, btnAllOngoing, btnAllUpcoming, btnAllNear;
    private ProgressBar progressBar;
    private FragmentActivity mContext;

    private int ACCESS_FINE_LOCATION_PERMISSION_CODE = 99;
    private double lat = 10.7805666, lng = 106.70075980000001;
    private LocationManager locationManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        ((GamexApplication) context.getApplicationContext()).getAppComponent().inject(this);
        super.onAttach(context);
        if (context instanceof Activity) {
            mContext = (FragmentActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        isLoadingNear = isLoadingOngoing = isLoadingUpcoming = false;
        apiParam = new HashMap<>();
        accessToken = "Bearer " + sharedPreferences.getString(Constant.PREF_ACCESS_TOKEN, "");

        checkLocationPermission();
        getLocation();

        mappingViewElement(view);
        setResponseToEvent();
        setLayoutManager();
        checkInternet();
        return view;
    }

    private void checkLocationPermission() {
        Log.i(TAG, "checkLocationPermission");
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_CODE);
        }
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager)
                    mContext.getSystemService(Context.LOCATION_SERVICE);

            final LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    lng = location.getLongitude();
                    lat = location.getLatitude();
                    Log.i(TAG, "New location ----------------------------- " + lng + " " + lat);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    private void setResponseToEvent() {
        refreshLayout.setOnRefreshListener(() -> {
            getLocation();
            checkInternet();
        });

        btnAllOngoing.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ViewAllExhibitionActivity.class);
            intent.putExtra("VIEW_ALL_TYPE", Constant.API_TYPE_ONGOING);
            startActivity(intent);
        });

        btnAllUpcoming.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ViewAllExhibitionActivity.class);
            intent.putExtra("VIEW_ALL_TYPE", Constant.API_TYPE_UPCOMING);
            startActivity(intent);
        });

        btnAllNear.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ViewAllExhibitionActivity.class);
            intent.putExtra("VIEW_ALL_TYPE", Constant.API_TYPE_NEAR);
            startActivity(intent);
        });
    }

    private void mappingViewElement(View view) {
        txtToolBarTitle = mContext.findViewById(R.id.main_toolbar_title);
        progressBar = mContext.findViewById(R.id.main_progress_bar);
        txtNoInternet = mContext.findViewById(R.id.main_txt_no_internet);
        txtLoading = mContext.findViewById(R.id.main_txt_loading);
        refreshLayout = view.findViewById(R.id.fg_home_swipeToRefresh);

        rvOngoing = view.findViewById(R.id.fg_home_rv_ongoing);
        rvNear = view.findViewById(R.id.fg_home_rv_near);
        rvUpcoming = view.findViewById(R.id.fg_home_rv_upcoming);

        btnAllOngoing = view.findViewById(R.id.btn_view_all_ongoing);
        btnAllUpcoming = view.findViewById(R.id.btn_view_all_upcoming);
        btnAllNear = view.findViewById(R.id.btn_view_all_near);
        //set title
        txtToolBarTitle.setText("Home");
    }

    private void checkInternet() {
        new CheckInternetTask(internet -> {
            if (internet) {
                Log.i(TAG, "Has Internet Connection");
                txtNoInternet.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                txtLoading.setVisibility(View.VISIBLE);
                callAPIOngoing();
                callAPIUpcoming();
                callAPINear();
            } else {
                Log.i(TAG, "No Internet Connection");
                txtNoInternet.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                txtLoading.setVisibility(View.GONE);
            }
        });
    }

    private void callAPIOngoing() {
        apiParam.clear();
        apiParam.put("type", Constant.API_TYPE_ONGOING);

        isLoadingOngoing = true;
        callOngoing = dataService.getExhibitionsList(accessToken, apiParam);
        callOngoing.enqueue(new BaseCallBack<List<Exhibition>>(mContext) {
            @Override
            public void onSuccess(Call<List<Exhibition>> call, Response<List<Exhibition>> response) {
                isLoadingOngoing = false;
                if (response.isSuccessful()) {
                    HomeAdapter adapter = new HomeAdapter(mContext, response.body());
                    rvOngoing.setAdapter(adapter);
                    Log.i(TAG, response.toString());
                } else {
                    Log.i(TAG, response.toString());
                }
                stopLoadingAnimation();
            }

            @Override
            public void onFailure(Call<List<Exhibition>> call, Throwable t) {
                isLoadingOngoing = false;
                if (call.isCanceled()) {
                    Log.i(TAG, "Cancel HTTP request on onFailure()");
                } else {
                    Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, t.getMessage());
                }
                stopLoadingAnimation();
            }
        });
    }

    private void callAPIUpcoming() {
        apiParam.clear();
        apiParam.put("type", Constant.API_TYPE_UPCOMING);

        isLoadingUpcoming = true;
        callUpcoming = dataService.getExhibitionsList(accessToken, apiParam);
        callUpcoming.enqueue(new BaseCallBack<List<Exhibition>>(mContext) {
            @Override
            public void onSuccess(Call<List<Exhibition>> call, Response<List<Exhibition>> response) {
                isLoadingUpcoming = false;
                if (response.isSuccessful()) {
                    Log.i(TAG, response.toString());
                    List<Exhibition> tmp = response.body();
                    for (Exhibition exhibition: tmp
                         ) {
                        System.out.println(exhibition.toString());
                    }
                    HomeAdapter adapter = new HomeAdapter(mContext, response.body());
                    rvUpcoming.setAdapter(adapter);
                } else {
                    Log.i(TAG, response.toString());
                }
                stopLoadingAnimation();
            }

            @Override
            public void onFailure(Call<List<Exhibition>> call, Throwable t) {
                isLoadingUpcoming = false;
                if (call.isCanceled()) {
                    Log.i(TAG, "Cancel HTTP request on onFailure()");
                } else {
                    Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, t.getMessage());
                }
                stopLoadingAnimation();
            }
        });
    }

    private void callAPINear() {
        apiParam.clear();
        apiParam.put("type", Constant.API_TYPE_NEAR);
        apiParam.put("lat", lat);
        apiParam.put("lng", lng);

        isLoadingNear = true;
        callNear = dataService.getExhibitionsList(accessToken, apiParam);
        callNear.enqueue(new BaseCallBack<List<Exhibition>>(mContext) {
            @Override
            public void onSuccess(Call<List<Exhibition>> call, Response<List<Exhibition>> response) {
                isLoadingNear = false;
                if (response.isSuccessful()) {
                    HomeAdapter adapter = new HomeAdapter(mContext, response.body());
                    rvNear.setAdapter(adapter);
                    Log.i(TAG, response.toString());
                } else {
                    Log.i(TAG, response.toString());
                }
                stopLoadingAnimation();
            }

            @Override
            public void onFailure(Call<List<Exhibition>> call, Throwable t) {
                isLoadingNear = false;
                if (call.isCanceled()) {
                    Log.i(TAG, "Cancel HTTP request on onFailure()");
                } else {
                    Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, t.getMessage());
                }
                stopLoadingAnimation();
            }
        });
    }

    private void stopLoadingAnimation() {
        if (!isLoadingOngoing && !isLoadingUpcoming && !isLoadingNear) {
            Log.i(TAG, "All API done ---> Stop loading animation");
            progressBar.setVisibility(View.GONE);
            txtLoading.setVisibility(View.GONE);
            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }
        }
    }

    private void setLayoutManager() {
        rvOngoing.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvNear.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvUpcoming.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onStop() {
        super.onStop();
        // Cancel retrofit callGetProfile when change fragment
        if (callOngoing != null) {
            callOngoing.cancel();
        }
        if (callUpcoming != null) {
            callUpcoming.cancel();
        }
        if (callNear != null) {
            callNear.cancel();
        }
    }
}
