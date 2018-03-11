package com.example.mshah1.notifications;

/**
 * Created by mshah1 on 3/11/2018.
 */
import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Notifications extends Fragment{
    public Context context;
    private LayoutInflater inflater;
    private ViewGroup container;
    private LinearLayout notifList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        View rootView = inflater.inflate(R.layout.notifications, container, false);
        this.notifList = (LinearLayout) rootView.findViewById(R.id.notification_list);

        Button refresh = (Button) rootView.findViewById(R.id.refresh_button);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNotifications();
            }
        });

        getNotifications();
        return rootView;
    }

    private void getNotifications(){
        RequestQueue queue = Volley.newRequestQueue(this.context);
        String url ="http://notifs4sidra.herokuapp.com/notifs/get_notifs";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("Response is: ", response);
                        updateNotificationsFromJSON(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("","That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void updateNotificationsFromJSON(String json){
        InputStream in = new ByteArrayInputStream(json.getBytes());
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        notifList.removeAllViews();
        try {
            reader.beginArray();
            while (reader.hasNext()){
                CardView notif = createNotificationFromJSON(reader);
                notifList.addView(notif);
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CardView createNotificationFromJSON(JsonReader reader){
        String name="", roomNum="", category="", comments="";
        try {
            reader.beginObject();
            while (reader.hasNext()){
                while (!reader.nextName().equals("fields")) reader.skipValue();
                reader.beginObject();
                while (reader.hasNext()){
                    String n = reader.nextName();
                    Log.d("name", n);
                    switch (n){
                        case "name":
                            name = reader.nextString();
                            break;
                        case "room_num":
                            roomNum = reader.nextString();
                            break;
                        case "category":
                            category = reader.nextString();
                            break;
                        case "comments":
                            comments = reader.nextString();
                            break;
                    }
                }
                reader.endObject();
            }
            reader.endObject();
            return createNotification(name, roomNum, category, comments);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String mapCategory(String cat){
        switch (cat){
            case "pw":
                return "Paperwork";
            case "pr":
                return "Printing";
            case "rf":
                return "Refreshments";
            default:
                return "";
        }
    }
    private CardView createNotification(String name, String roomNum, String category, String comments){
        CardView notif = (CardView) inflater.inflate(R.layout.notif_card,container,false);
        ((TextView)notif.findViewById(R.id.notif_name)).setText(name);
        ((TextView)notif.findViewById(R.id.notif_room_num)).setText(roomNum);
        ((TextView)notif.findViewById(R.id.notif_cat)).setText(mapCategory(category));
        ((TextView)notif.findViewById(R.id.notif_comment)).setText(comments);
        notif.setVisibility(View.VISIBLE);
        return notif;
    }
}
