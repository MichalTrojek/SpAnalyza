package com.mtr.application;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.mtr.application.adapters.MyAdapter;

import java.util.ArrayList;

import application.R;

public class FindByNameActivity extends AppCompatActivity {

    private TextView searchInput;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Item> dataSet = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_by_name);
        setToolbar();

        this.searchInput = (TextView) findViewById(R.id.searchInput);
        this.recyclerView = (RecyclerView) findViewById(R.id.RecyclerViewSearch);
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);
        this.adapter = new MyAdapter(dataSet, this);
        this.recyclerView.setAdapter(adapter);


        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
                db.open();
                dataSet.clear();
                dataSet.addAll(db.fullTextSearch(editable.toString()));
                adapter.notifyDataSetChanged();
                db.close();
            }
        });


    }


    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        toolbar.setTitle("Vyhledávání podle názvu");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
