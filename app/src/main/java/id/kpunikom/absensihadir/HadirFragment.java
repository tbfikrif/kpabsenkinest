package id.kpunikom.absensihadir;


import android.content.Context;
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

        ShowDataRecycler();

        return view;
    }

    public void ShowDataRecycler(){
        if (data.getCount() == 0){
            Toast.makeText(getContext(), "Belum ada yang Login.", Toast.LENGTH_SHORT).show();
        } else {
            while (data.moveToNext()){
                itemList.add(new Item(data.getString(1), data.getString(2)));
                recyclerView.setAdapter(itemArrayAdapter);
            }
        }
    }

    public void UpdateDataRecycler(){
        itemList.clear();
        itemArrayAdapter.notifyDataSetChanged();
    }

}
