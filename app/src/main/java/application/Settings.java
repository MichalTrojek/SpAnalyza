package application;

import android.content.Context;
import android.content.SharedPreferences;

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


}
