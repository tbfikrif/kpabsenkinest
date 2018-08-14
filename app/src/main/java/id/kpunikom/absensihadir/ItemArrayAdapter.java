package id.kpunikom.absensihadir;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import id.kpunikom.absensihadir.model.Item;

public class ItemArrayAdapter extends RecyclerView.Adapter<ItemArrayAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private ArrayList<Item> itemList;
    // Constructor of the class
    public ItemArrayAdapter(int layoutId, ArrayList<Item> itemList) {
        listItemLayout = layoutId;
        this.itemList = itemList;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView nama = holder.nama;
        TextView email = holder.email;
        TextView jam_masuk = holder.jam_masuk;
        nama.setText(itemList.get(listPosition).getNama());
        email.setText(itemList.get(listPosition).getEmail());
        jam_masuk.setText(itemList.get(listPosition).getJam_masuk());
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nama;
        public TextView email;
        public TextView jam_masuk;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nama = itemView.findViewById(R.id.nama);
            email = itemView.findViewById(R.id.email);
            jam_masuk = itemView.findViewById(R.id.jam_masuk);
        }
        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + nama.getText());
        }
    }

    public void UpdateDataRecycler(ArrayList<Item> newItems){
        itemList.clear();
        itemList.addAll(newItems);
        this.notifyDataSetChanged();
    }
}