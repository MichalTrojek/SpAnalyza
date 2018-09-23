package com.mtr.application;

import android.os.AsyncTask;

import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class SearchAsyncTask extends AsyncTask<String, Void, List<String>> {

    private List<String> suggestions = new ArrayList<String>();
    private MaterialSearchBar searchBar;


    public SearchAsyncTask(MaterialSearchBar searchBar,  List<String> suggestions ) {
        this.searchBar = searchBar;
        this.suggestions = suggestions;
    }





    @Override
    protected List<String> doInBackground(String... input) {
        ArrayList<String> suggest = new ArrayList<>();
        for (String s : suggestions) {
            if (s.toString().toLowerCase().trim().contains(input.toString().toLowerCase().trim())) {
                suggest.add(s);
            }
        }
        return suggest;
    }

    @Override
    protected void onPostExecute(List<String> list) {
        Model.getInstance().getSuggestions().addAll(list);
        searchBar.setLastSuggestions(list);
    }


}
