package id.kpunikom.absensihadir;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import id.kpunikom.absensihadir.control.ApiClient;
import id.kpunikom.absensihadir.control.ApiInterface;
import id.kpunikom.absensihadir.model.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class HadirFragment extends Fragment {

    //RecyclerView
    RecyclerView recyclerView;
    ArrayList<Item> itemList;
    ItemArrayAdapter itemArrayAdapter;

    //Database
    DatabaseHelper myDB;
    Cursor data;

    //Retrofit
    private ApiInterface apiInterface;

    public static int jumlahSudahAbsen = 0;

    public HadirFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hadir, container, false);

        //RecyclerView
        itemList = new ArrayList<>();
        itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item, itemList);
        recyclerView = view.findViewById(R.id.itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        myDB = new DatabaseHelper(getContext());
        data = myDB.getListContents();

        //API
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<Item>> call = apiInterface.getListSudahAbsen();

        call.enqueue(new Callback<ArrayList<Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                itemList = response.body();
                itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item, itemList);
                recyclerView.setAdapter(itemArrayAdapter);
                jumlahSudahAbsen = itemArrayAdapter.getItemCount();
            }

            @Override
            public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                //Toast.makeText(getContext(), "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
            }
        });

        //ShowDataRecycler();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //API
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ArrayList<Item>> call = apiInterface.getListSudahAbsen();

        call.enqueue(new Callback<ArrayList<Item>>() {
            @Override
            public void onResponse(Call<ArrayList<Item>> call, Response<ArrayList<Item>> response) {
                itemList = response.body();
                itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item, itemList);
                recyclerView.setAdapter(itemArrayAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Item>> call, Throwable t) {
                //Toast.makeText(getContext(), "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ShowDataRecycler(){
        if (data.getCount() == 0){
            Toast.makeText(getContext(), "Belum ada yang Absen.", Toast.LENGTH_SHORT).show();
        } else {
            while (data.moveToNext()){
                //itemList.add(new Item(data.getString(1), data.getString(2), data.getString(3)));
                recyclerView.setAdapter(itemArrayAdapter);
            }
        }
    }
}
