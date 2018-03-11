package com.example.mshah1.notifications;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mshah1 on 3/11/2018.
 */

public class NewNotification extends Fragment{
    private LinearLayout notif_form;
    public Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_notification, container, false);
        this.notif_form = (LinearLayout) rootView.findViewById(R.id.new_notif_form);
        ((Button) notif_form.findViewById(R.id.submit_notif)).setOnClickListener(new SubmitNotificationOnClickListener(notif_form));
        return rootView;
    }

    class SubmitNotificationOnClickListener implements View.OnClickListener {
        private LinearLayout form;

        public SubmitNotificationOnClickListener(LinearLayout form){
            this.form = form;
        }

        private String reverseMapCategory(String cat){
            switch (cat){
                case "Paperwork":
                    return "pr";
                case "Printing":
                    return "pw";
                case "Refreshments":
                    return "rf";
                default:
                    return "";
            }
        }

        @Override
        public void onClick(View v) {
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = "http://notifs4sidra.herokuapp.com/notifs/add_notif";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response", error.getMessage());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("name", ((EditText) form.findViewById(R.id.new_name)).getText().toString());
                    params.put("room_num", ((EditText) form.findViewById(R.id.new_room_num)).getText().toString());
                    Spinner s = (Spinner) form.findViewById(R.id.new_cat);
                    params.put("category", reverseMapCategory((String) s.getSelectedItem()));
                    params.put("comments", ((EditText) form.findViewById(R.id.new_comments)).getText().toString());
                    Log.d("params", params.toString());
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("User-Agent","Sidra-Notifications-App");
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        }
    }
}
