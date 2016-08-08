package co.tslc.cashe.android;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CasheSummary extends Base {

    protected CASHe app;
    private Tracker mTracker;

    private String name;
    private String email;
    private String custID;
    private String casheAmt;
    private String inHand;
    private String feeFlat;
    private String feeProc;
    private String feeProcLbl;
    private String feeProcAmt;
    private String dueDate;

    private static String bankName = "";
    private static String accNum = "";
    private static String ifsc = "";

    private static String accessToken = "";
    private static String refreshToken = "";
    private static String tokenType = "";

    private static final String urlConfInf = CASHe.webApiUrl + "api/cashe/customer/details/verifyOnLoanRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_cashe_summary);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_cashe_summary, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
      //  getLayoutInflater().inflate(R.layout.activity_cashe_summary, frameLayout, true);
        //typeFace(frameLayout);

        app = (CASHe)getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("CASHeSummary");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Intent i = getIntent();
        name = i.getStringExtra("name");
        email = i.getStringExtra("email");
        custID = i.getStringExtra("custID");
        accessToken = i.getStringExtra("accessToken");
        refreshToken = i.getStringExtra("refreshToken");
        tokenType = i.getStringExtra("tokenType");
        casheAmt = i.getStringExtra("casheAmt");
        inHand = i.getStringExtra("inHand");
        feeProc = i.getStringExtra("procFee");
        feeFlat = i.getStringExtra("flatFee");
        feeProcLbl = i.getStringExtra("procFeeLbl");
        feeProcAmt = i.getStringExtra("procFeeAmt");
        dueDate = i.getStringExtra("dueDate");

         bankName = i.getStringExtra("bankName");
         accNum = i.getStringExtra("accNum");
         ifsc = i.getStringExtra("ifsc");

        TextView tvCasheAmt = (TextView) findViewById(R.id.tvCasheAmt);
        TextView tvFeeFlat = (TextView) findViewById(R.id.tvFeeFlat);
        TextView tvPFee = (TextView) findViewById(R.id.tvPFee);
        TextView tvFeeProc = (TextView) findViewById(R.id.tvFeeProc);
        TextView tvInHand = (TextView) findViewById(R.id.tvInHand);
        TextView tvDueDate = (TextView) findViewById(R.id.tvDueDate);
        if(inHand!=null) tvInHand.setText("\u20B9" + inHand.replace("0.",""));
        TextView tvRepay = (TextView) findViewById(R.id.tvRepay);
        if(casheAmt!=null) tvRepay.setText("\u20B9" + casheAmt.replace(".0",""));
        if(casheAmt!=null) tvCasheAmt.setText("\u20B9" + casheAmt.replace(".0",""));
        if(feeFlat!=null) tvFeeFlat.setText("(-) \u20B9" + feeFlat.replace(".0",""));
        if(feeProcAmt!=null) tvFeeProc.setText("(-) \u20B9" + feeProcAmt.replace(".0",""));
        tvDueDate.setText(dueDate);
        tvPFee.setText(feeProcLbl);
        TableRow row = (TableRow)findViewById(R.id.rowOneTime);
        if(feeFlat.equalsIgnoreCase("0")) row.setVisibility(View.GONE);

        TableLayout tblFigures = (TableLayout) findViewById(R.id.tblFigures);
        //tblFigures.setStretchAllColumns(true);

        Switch tbRmdr = (Switch) findViewById(R.id.switchCal);
        tbRmdr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addEvent();
                }
            }
        });

        ImageButton ibProceed = (ImageButton) findViewById(R.id.ibDeposit);
        ibProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest reqCust = new StringRequest(com.android.volley.Request.Method.GET, urlConfInf,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("IPB", response);
                                try {
                                    JSONObject bankDetails = new JSONObject(response);
                                    JSONObject jsonEntity = new JSONObject(bankDetails.getString("entity"));
                                    if(!jsonEntity.isNull("bankDetailDto"))
                                    {
                                        Intent i = new Intent(CasheSummary.this, CasheSummaryConfirm.class);
                                        i.putExtra("name", name);
                                        i.putExtra("email", email);
                                        i.putExtra("custID", custID);
                                        i.putExtra("accessToken", accessToken);
                                        i.putExtra("refreshToken", refreshToken);
                                        i.putExtra("tokenType", tokenType);
                                        i.putExtra("casheAmt", String.valueOf(casheAmt));
                                        i.putExtra("procFee", String.valueOf(feeProc));
                                        i.putExtra("flatFee", String.valueOf(feeFlat));
                                        i.putExtra("inHand", String.valueOf(inHand));
                                        i.putExtra("bankName", bankName);
                                        i.putExtra("ifsc", ifsc);
                                        i.putExtra("accNum",accNum);
                                        startActivity(i);
                                        //finish();
                                    }
                                    else
                                    {
                                        Intent i = new Intent(CasheSummary.this, BankDetails.class);
                                        i.putExtra("name", name);
                                        i.putExtra("email", email);
                                        i.putExtra("custID", custID);
                                        i.putExtra("accessToken", accessToken);
                                        i.putExtra("refreshToken", refreshToken);
                                        i.putExtra("tokenType", tokenType);
                                        i.putExtra("casheAmt", String.valueOf(casheAmt));
                                        i.putExtra("procFee", String.valueOf(feeProc));
                                        i.putExtra("flatFee", String.valueOf(feeFlat));
                                        i.putExtra("inHand", String.valueOf(inHand));
                                        startActivity(i);
                                        //finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                if(error instanceof NoConnectionError) {
                                    Toast.makeText(CasheSummary.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                reqCust.setShouldCache(false);
                rQueue.getInstance(CasheSummary.this).queueRequest("",reqCust);
            }
        });
    }

    public void addEvent() {
        Calendar c = Calendar.getInstance();

        try {
            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, c.getTimeInMillis() + (14 * 24 * 60 *60) * 1000);
            values.put(CalendarContract.Events.DTEND, c.getTimeInMillis() + (15 * 24 * 60 *60) * 1000);
            values.put(CalendarContract.Events.TITLE, "CASHe - Repay Loan");
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                    .getTimeZone().getID());
            values.put(CalendarContract.Events._ID, custID);
            System.out.println(Calendar.getInstance().getTimeZone().getID());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // Save the eventId into the Task object for possible future delete.
            Long _eventId = Long.parseLong(uri.getLastPathSegment());
            // Add a 5 minute, 1 hour and 1 day reminders (3 reminders)
            setReminder(cr, _eventId, 5);
            setReminder(cr, _eventId, 60);
            setReminder(cr, _eventId, 1440);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // routine to add reminders with the event
    public void setReminder(ContentResolver cr, long eventID, int timeBefore) {
        try {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, timeBefore);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
            Cursor c = CalendarContract.Reminders.query(cr, eventID,
                    new String[]{CalendarContract.Reminders.MINUTES});
            if (c.moveToFirst()) {
                System.out.println("calendar"
                        + c.getInt(c.getColumnIndex(CalendarContract.Reminders.MINUTES)));
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // function to remove an event from the calendar using the eventId stored within the Task object.
    public void removeEvent(long eventID) {
        ContentResolver cr = getContentResolver();

        int iNumRowsDeleted = 0;

        Uri eventsUri = Uri.parse("content://calendar/events");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, eventID);
        iNumRowsDeleted = cr.delete(eventUri, null, null);

        Log.i("RR", "Deleted " + iNumRowsDeleted + " calendar entry.");
    }


    public int updateEvent(Long eventID) {
        int iNumRowsUpdated = 0;
        ContentValues event = new ContentValues();
        Calendar c = Calendar.getInstance();
        event.put(CalendarContract.Events.TITLE, "CASHe - Repay Loan");
        event.put("hasAlarm", 1); // 0 for false, 1 for true
        event.put(CalendarContract.Events.DTSTART, c.getTimeInMillis());
        event.put(CalendarContract.Events.DTEND, c.getTimeInMillis()+60*60*1000);

        Uri eventsUri = Uri.parse("content://calendar/events");
        Uri eventUri = ContentUris.withAppendedId(eventsUri, eventID);

        iNumRowsUpdated = getContentResolver().update(eventUri, event, null,
                null);

        // TODO put text into strings.xml
        Log.i("RR", "Updated " + iNumRowsUpdated + " calendar entry.");

        return iNumRowsUpdated;
    }
}
