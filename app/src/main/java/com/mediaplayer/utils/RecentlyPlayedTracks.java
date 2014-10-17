package com.mediaplayer.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by imishev on 26.9.2014 г..
 */
public class RecentlyPlayedTracks {

    public String readSongs() throws JSONException {
        JSONArray jsonArray = new JSONArray(""/*JSON array*/);
        final int JSON_LENGTH = jsonArray.length();

        for (int i = 0; i < JSON_LENGTH; i++) {
            JSONObject jObj = jsonArray.getJSONObject(i);
/*            if (jObj.getString("country").equals(country) && jObj.getString("city").equals(city) &&
                    jObj.getString("language").equals(language)) {
                return jObj.getString("filePath");
            }*/
        }
        return null;
    }
}