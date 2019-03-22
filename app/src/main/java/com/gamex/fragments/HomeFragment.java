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
import android.support.annotation.NonNull;
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

    private final String TAG = HomeFragment.class.getSimpleName() + "////";
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView rvOngoing, rvNear, rvUpcoming;
    private TextView txtNoInternet, txtLoading, btnAllOngoing, btnAllUpcoming, btnAllNear, txtNoDataOngoing, txtNoDataUpcoming, txtNoDataNear;
    private ProgressBar progressBar;
    private FragmentActivity mContext;

    private int ACCESS_FINE_LOCATION_PERMISSION_CODE = 99;
    private double lat = 10.7805666, lng = 106.70075980000001;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isApiNearCalled;

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

//        checkLocationPermission();
//        getLocation();
        Log.i(TAG, "onCreateView");

        isApiNearCalled = false;
        checkLocationPermission();

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
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //PERMISSION_GRANTED
                getLocation();
            } else {
                Toast.makeText(mContext, "Location Permission DENIED, using default location!", Toast.LENGTH_SHORT).show();
                callAPINear();
            }
        }
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager)
                    mContext.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    lng = location.getLongitude();
                    lat = location.getLatitude();
                    Log.i(TAG, "New location --- " + lng + " " + lat);

                    if (!isApiNearCalled) {
                        isApiNearCalled = true;
                        Log.i(TAG, "call api near in LocationListener");
                        callAPINear();
                    }
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
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    private void setResponseToEvent() {
        refreshLayout.setOnRefreshListener(() -> {
            isApiNearCalled = false;
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
            intent.putExtra(Constant.API_LAT, lat);
            intent.putExtra(Constant.API_LNG, lng);
            startActivity(intent);
        });
    }

    private void mappingViewElement(View view) {
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

        txtNoDataOngoing = view.findViewById(R.id.fg_home_no_data_ongoing);
        txtNoDataUpcoming = view.findViewById(R.id.fg_home_no_data_upcoming);
        txtNoDataNear = view.findViewById(R.id.fg_home_no_data_near);
    }

    private void checkInternet() {
        new CheckInternetTask(internet -> {
            if (internet) {
                Log.i(TAG, "Has Internet Connection");
                txtNoInternet.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                txtLoading.setVisibility(View.VISIBLE);
                Log.i(TAG, "call api ongoing");
                callAPIOngoing();
                Log.i(TAG, "call api upcoming");
                callAPIUpcoming();
//                Log.i(TAG, "call api near");
//                callAPINear();
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
                    if (response.body().isEmpty()) {
                        txtNoDataOngoing.setVisibility(View.VISIBLE);
                    } else {
                        txtNoDataOngoing.setVisibility(View.GONE);
                        HomeAdapter adapter = new HomeAdapter(mContext, response.body());
                        rvOngoing.setAdapter(adapter);
                    }
                } else {
                    txtNoDataOngoing.setVisibility(View.VISIBLE);

                }
                Log.i(TAG, response.toString());
                stopLoadingAnimation();
            }

            @Override
            public void onFailure(Call<List<Exhibition>> call, Throwable t) {
                isLoadingOngoing = false;
                if (call.isCanceled()) {
                    Log.i(TAG, "Cancel HTTP request on onFailure()");
                } else {
                    txtNoDataOngoing.setVisibility(View.VISIBLE);
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

                    if (response.body().isEmpty()) {
                        txtNoDataOngoing.setVisibility(View.VISIBLE);
                    } else {
                        txtNoDataUpcoming.setVisibility(View.GONE);

                        HomeAdapter adapter = new HomeAdapter(mContext, response.body());
                        rvUpcoming.setAdapter(adapter);
                    }

                } else {
                    txtNoDataOngoing.setVisibility(View.VISIBLE);
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
                    txtNoDataOngoing.setVisibility(View.VISIBLE);
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

                    if (response.body().isEmpty()) {
                        txtNoDataNear.setVisibility(View.VISIBLE);
                    } else {
                        txtNoDataNear.setVisibility(View.GONE);
                        HomeAdapter adapter = new HomeAdapter(mContext, response.body());
                        rvNear.setAdapter(adapter);
                    }

                    Log.i(TAG, response.toString());
                } else {
                    txtNoDataNear.setVisibility(View.VISIBLE);
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
                    txtNoDataNear.setVisibility(View.VISIBLE);
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
