package co.tslc.cashe.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pratap on 09-05-2016.
 */
public class BlockedUser extends Base {

    private String name;
    private String email;
    private String custID;

    private static String accessToken = "";
    private static String refreshToken = "" ;
    private static String tokenType = "";

    private static final String urlStatus = CASHe.webApiUrl + "api/cashe/customer/status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_blocked_user, frameLayout, true);
        typeFace(frameLayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Intent i = getIntent();
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        custID = i.getStringExtra("custID");
        accessToken = i.getStringExtra("accessToken");
        refreshToken = i.getStringExtra("refreshToken");
        tokenType = i.getStringExtra("tokenType");

        StringRequest reqAuth = new StringRequest(Request.Method.GET, urlStatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonStatus = new JSONObject(response.toString());
                            JSONObject jsonEntity = new JSONObject(jsonStatus.getString("entity"));
                            if(!jsonEntity.isNull("messageList")) {
                                JSONArray msgList = jsonEntity.getJSONArray("messageList");
                                String msg = msgList.getString(0);
                                TextView tvInEligible = (TextView)findViewById(R.id.blockMsg);
                                tvInEligible.setText(msg.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if(error instanceof NoConnectionError) {
                            Toast.makeText(BlockedUser.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                }


        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", tokenType + " " + accessToken);
                return headers;
            }
        };

        reqAuth.setShouldCache(false);
        rQueue.getInstance(BlockedUser.this).queueRequest("",reqAuth);

    }


    @Override
    protected boolean isBlocked() {
        CASHe.blocked = 2;
        return true;
    }
}
