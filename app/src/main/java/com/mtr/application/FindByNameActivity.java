package com.mtr.application;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import application.R;

public class FindByNameActivity extends AppCompatActivity {

    private TextView textView;
    private MaterialSearchBar materialSearchBar;
    private List<String> suggestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_by_name);
        setToolbar();

        suggestions.add("Aristokratka a vlna zločinnosti na zámku Kostka");
        suggestions.add("Ari");
        suggestions.add("Aris");
        suggestions.add("Nezmar");
        suggestions.add("Nez");
        suggestions.add("Ne");
        suggestions.add("Missile (pískající raketa)");
        suggestions.add("Vteřinu poté");

//        for(int i = 0; i < 100000; i++){
//            suggestions.add("test" + i);
//            if(i < 100){
//                suggestions.add("te" +i);
//            }
//
//            if(i > 100){
//                suggestions.add("testovani" + i);
//            }
//        }

//        suggestions.addAll(Model.getInstance().getMapOfNames().keySet());
        Model.getInstance().getSuggestions().addAll(suggestions);
        this.textView = (TextView) findViewById(R.id.textView);

        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setLastSuggestions(Model.getInstance().getSuggestions());



        materialSearchBar.addTextChangeListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayList<String> suggest = new ArrayList<>();
                for (String s : Model.getInstance().getSuggestions()) {
//                    if (s.toString().contains(materialSearchBar.getText().toLowerCase())) {
//                        suggest.add(s);
//                    }

//                    if (s.toString().toLowerCase().trim().contains(charSequence.toString().toLowerCase().trim())) {
//                        suggest.add(s);
//                    }
                    SearchAsyncTask search = new SearchAsyncTask(materialSearchBar, Model.getInstance().getSuggestions());
                    search.execute(charSequence.toString());
                }
//                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {


            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                String name = (String) text.toString();
                DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
                db.open();
                String ean = db.getBookByName(name);
                db.close();
                intent.putExtra("ean", ean);
                startActivity(intent);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }


    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        toolbar.setTitle("SP");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
