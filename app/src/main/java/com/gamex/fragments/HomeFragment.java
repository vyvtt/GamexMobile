package com.gamex.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gamex.MainActivity;
import com.gamex.R;
import com.gamex.adapters.ExhibitionListRecycleViewAdapter;
import com.gamex.models.Exhibition;
import com.gamex.network.GetDataService;
import com.gamex.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    RecyclerView rvOngoing, rvNear, rvYourEvent;
    TextView txtToolBarTitle;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        txtToolBarTitle = getActivity().findViewById(R.id.toolbar_title);
        txtToolBarTitle.setText("Home");

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        // TODO: THIS IS TEST DATA
//        ArrayList<String> exImg = new ArrayList<>();
//        ArrayList<String> exName = new ArrayList<>();
//        ArrayList<String> exDate = new ArrayList<>();
//        exName.add("AUTOMECHANIKA HO CHI MINH CITY 2019");
//        exName.add("TELEFILM 2019 / ICTCOMM 2019");
//        exName.add("VIFA GOOD URBAN 2019 – VIFAG.U. 2019");
//        exName.add("VIETBUILD HOME 2019");
//        exName.add("VIETWATER 2019");
//        exDate.add("February 28th to March 2nd");
//        exDate.add("February 28th to March 2nd");
//        exDate.add("February 28th to March 2nd");
//        exDate.add("February 28th to March 2nd");
//        exDate.add("February 28th to March 2nd");
//        exDate.add("February 28th to March 2nd");

        /*Create handle for the RetrofitInstance interface*/
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Exhibition>> call = service.getAllExhibition();
        call.enqueue(new Callback<List<Exhibition>>() {
            @Override
            public void onResponse(Call<List<Exhibition>> call, Response<List<Exhibition>> response) {
                rvOngoing = view.findViewById(R.id.fg_home_rv_ongoing);
                rvNear = view.findViewById(R.id.fg_home_rv_near);
                rvYourEvent = view.findViewById(R.id.fg_home_rv_your_event);
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<Exhibition>> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        rvOngoing = view.findViewById(R.id.fg_home_rv_ongoing);
//        rvOngoing.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        rvNear = view.findViewById(R.id.fg_home_rv_near);
//        rvNear.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        rvYourEvent = view.findViewById(R.id.fg_home_rv_your_event);
//        rvYourEvent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

//        ExhibitionListRecycleViewAdapter adapter = new ExhibitionListRecycleViewAdapter(getContext(), exImg, exName, exDate);
//        rvOngoing.setAdapter(adapter);
//        rvNear.setAdapter(adapter);
//        rvYourEvent.setAdapter(adapter);

        return view;
    }

    private void generateDataList(List<Exhibition> listExhibition) {
        rvOngoing.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvNear.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvYourEvent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        ExhibitionListRecycleViewAdapter adapter = new ExhibitionListRecycleViewAdapter(getContext(), listExhibition);
        rvOngoing.setAdapter(adapter);
        rvNear.setAdapter(adapter);
        rvYourEvent.setAdapter(adapter);
    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        // TODO Add your menu entries here
//        super.onCreateOptionsMenu(menu, inflater);
////        inflater.inflate(R.menu.main_menu_qr, menu);
//    }
//
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        // Add QR icon to the right side of Toolbar
////        getMenuInflater().inflate(R.menu.main_menu_qr, menu);
////        return true;
////    }
//
//    public void clickToScan(MenuItem item) {
//        Intent intent = new Intent(getContext(), ScanQRActivity.class);
//        startActivity(intent);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent intent = new Intent(getContext(), ScanQRActivity.class);
//        startActivity(intent);
//        return true;
//    }
}
