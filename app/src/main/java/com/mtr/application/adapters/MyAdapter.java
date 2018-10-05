package com.mtr.application.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mtr.application.Item;
import com.mtr.application.MainActivity;

import java.util.List;

import application.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Item> dataSet;
    private Context context;


    public MyAdapter(List<Item> dataSet, Context c) {
        this.dataSet = dataSet;
        this.context = c;
    }


    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {

        final Item item = dataSet.get(position);


        holder.nameTextView.setText(item.getName());
        holder.eanTextView.setText(item.getEan());

        holder.layoutHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(item.getEan());
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("ean", item.getEan());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView eanTextView;
        public ConstraintLayout layoutHolder;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.itemTextView);
            eanTextView = itemView.findViewById(R.id.eanTextView);
            layoutHolder = itemView.findViewById(R.id.recyclerLayout);
        }
    }


}
