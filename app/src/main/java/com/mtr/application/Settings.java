package com.mtr.application;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mtr.application.shared.ArticleRow;
import com.mtr.application.shared.ExportArticle;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class Settings {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    public Settings(Context context) {
        prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getIp() {
        return prefs.getString("ip", "");
    }

    public void setIP(String ip) {
        editor = prefs.edit();
        editor.putString("ip", ip);
        editor.apply();
    }

    public void setDatabaseId(int databaseId) {
        editor = prefs.edit();
        editor.putInt("databaseId", databaseId);
        editor.apply();
    }

    public int getDatabaseId() {
        return prefs.getInt("databaseId", 1);
    }


    public void setAnalysis(HashMap<String, ArticleRow> analysis) {
        editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(analysis);
        editor.putString("analysis", json);
        editor.apply();
    }


    public HashMap<String, ArticleRow> getAnalysis() {
        Gson gson = new Gson();
        String json = prefs.getString("analysis", null);
        Type type = new TypeToken<HashMap<String, ArticleRow>>() {
        }.getType();
        HashMap<String, ArticleRow> analysis;
        analysis = gson.fromJson(json, type);
        if (analysis == null) {
            analysis = new HashMap<>();
        }
        return analysis;
    }

    public void setOrders(ArrayList<ExportArticle> orders) {
        editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(orders);
        editor.putString("orders", json);
        editor.apply();
    }

    public void setReturns(ArrayList<ExportArticle> returns) {
        editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(returns);
        editor.putString("returns", json);
        editor.apply();
    }

    public ArrayList<ExportArticle> getOrders() {
        Gson gson = new Gson();
        String json = prefs.getString("orders", null);
        Type type = new TypeToken<ArrayList<ExportArticle>>() {
        }.getType();
        ArrayList<ExportArticle> orders;
        orders = gson.fromJson(json, type);
        if (orders == null) {
            orders = new ArrayList<>();
        }
        return orders;
    }

    public ArrayList<ExportArticle> getReturns() {
        Gson gson = new Gson();
        String json = prefs.getString("returns", null);
        Type type = new TypeToken<ArrayList<ExportArticle>>() {
        }.getType();
        ArrayList<ExportArticle> returns;
        returns = gson.fromJson(json, type);
        if (returns == null) {
            returns = new ArrayList<>();
        }
        return returns;
    }


}
