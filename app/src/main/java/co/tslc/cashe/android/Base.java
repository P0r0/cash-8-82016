package co.tslc.cashe.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Base extends AppCompatActivity {

    protected CASHe app;

    protected static String name;
    protected static String email;
    protected static String custID;
    protected static String customerStatusName="";

    protected static String accessToken;
    protected static String refreshToken;
    protected static String tokenType;

    protected FrameLayout frameLayout;
    private DrawerLayout mDrawerLayout;
    protected ListView mDrawerList;

    protected static int position;

    private ActionBarDrawerToggle mDrawerToggle;

    private static final String urlStatus = CASHe.webApiUrl + "api/cashe/customer/status";
    private static final String faqURL = "https://cashe.co.in/faqs.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        app = (CASHe)getApplication();
        app.isInForeground = true;

        frameLayout = (FrameLayout)findViewById(R.id.nav_frame);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.nav_drawer);

        navItem[] navItems;

        if(isVerified()) {

            if(isBlocked()) {
                navItems = new navItem[9];
                navItems[0] = new navItem(R.drawable.icon_home, "HOME");
                navItems[1] = new navItem(R.drawable.icon_history, "HISTORY");
                navItems[2] = new navItem(R.drawable.icon_why_cashe, "WHY CASHe?");
                navItems[3] = new navItem(R.drawable.icon_repay, "REPAY");
                navItems[4] = new navItem(R.drawable.icon_about, "ABOUT");
                navItems[5] = new navItem(R.drawable.icon_faqs, "FAQs");
                navItems[6] = new navItem(R.drawable.icon_privacy_policy, "PRIVACY POLICY");
                navItems[7] = new navItem(R.drawable.icon_tc, "T&C");
                navItems[8] = new navItem(R.drawable.icon_logout, "LOG OUT");
            }
            else {
                navItems = new navItem[10];
                navItems[0] = new navItem(R.drawable.icon_home, "HOME");
                navItems[1] = new navItem(R.drawable.icon_profile, "PROFILE");
                navItems[2] = new navItem(R.drawable.icon_repay, "REPAY");
                navItems[3] = new navItem(R.drawable.icon_history, "HISTORY");
                navItems[4] = new navItem(R.drawable.icon_why_cashe, "WHY CASHe?");
                navItems[5] = new navItem(R.drawable.icon_about, "ABOUT");
                navItems[6] = new navItem(R.drawable.icon_faqs, "FAQs");
                navItems[7] = new navItem(R.drawable.icon_privacy_policy, "PRIVACY POLICY");
                navItems[8] = new navItem(R.drawable.icon_tc, "T&C");
                navItems[9] = new navItem(R.drawable.icon_logout, "LOG OUT");
            }
        }
        else
        {
            navItems = new navItem[7];
            navItems[0] = new navItem(R.drawable.icon_why_cashe, "WHY CASHe?");
            navItems[1] = new navItem(R.drawable.icon_repay, "REPAY");
            navItems[2] = new navItem(R.drawable.icon_about, "ABOUT");
            navItems[3] = new navItem(R.drawable.icon_faqs, "FAQs");
            navItems[4] = new navItem(R.drawable.icon_privacy_policy, "PRIVACY POLICY");
            navItems[5] = new navItem(R.drawable.icon_tc, "T&C");
            navItems[6] = new navItem(R.drawable.icon_logout, "LOG OUT");
        }

        NavItemAdaptr navItemAdaptr = new NavItemAdaptr(this, R.layout.nav, navItems);
        mDrawerList.setAdapter(navItemAdaptr);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i;
                try{
                    if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    }}catch (Exception e){}
                if (isVerified()) {


                    if(isBlocked()){
                        switch (position) {

                            case 0:
                                i = new Intent(Base.this, BlockedUser.class);
                                i.putExtra("name", name);
                                i.putExtra("email", email);
                                i.putExtra("custID", custID);
                                i.putExtra("blocked" ,"1");
                                i.putExtra("accessToken", accessToken);
                                i.putExtra("refreshToken", refreshToken);
                                i.putExtra("tokenType", tokenType);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                //finish();
                                break;

                            case 1:
                                i = new Intent(Base.this, CasheHistory.class);
                                i.putExtra("name", name);
                                i.putExtra("email", email);
                                i.putExtra("custID", custID);
                                i.putExtra("blocked" ,"1");
                                i.putExtra("accessToken", accessToken);
                                i.putExtra("refreshToken", refreshToken);
                                i.putExtra("tokenType", tokenType);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                //finish();
                                break;

                            case 2:
                                i = new Intent(Base.this, WhyCASHe.class);
                                i.putExtra("name", name);
                                i.putExtra("email", email);
                                i.putExtra("custID", custID);
                                i.putExtra("accessToken", accessToken);
                                i.putExtra("refreshToken", refreshToken);
                                i.putExtra("tokenType", tokenType);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                break;
                            case 3:
                                i = new Intent(Base.this, RepayScreen.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                startActivity(i);
                                //finish();
                                break;
                            case 4:
                                i = new Intent(Base.this, About.class);
                                i.putExtra("name", name);
                                i.putExtra("email", email);
                                i.putExtra("custID", custID);
                                i.putExtra("accessToken", accessToken);
                                i.putExtra("refreshToken", refreshToken);
                                i.putExtra("tokenType", tokenType);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                break;
                            case 5:
                                //FAQs
                                try {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(faqURL));
                                    startActivity(browserIntent);
                                }catch (Exception e){}
                                break;
                            case 6:
                                i = new Intent(Base.this, Privacy.class);
                                i.putExtra("name", name);
                                i.putExtra("email", email);
                                i.putExtra("custID", custID);
                                i.putExtra("accessToken", accessToken);
                                i.putExtra("refreshToken", refreshToken);
                                i.putExtra("tokenType", tokenType);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                break;
                            case 7:
                                i = new Intent(Base.this, Terms.class);
                                i.putExtra("name", name);
                                i.putExtra("email", email);
                                i.putExtra("custID", custID);
                                i.putExtra("accessToken", accessToken);
                                i.putExtra("refreshToken", refreshToken);
                                i.putExtra("tokenType", tokenType);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                break;
                            case 8:
                                name = "";
                                email = "";
                                custID = "";
                                accessToken = "";
                                refreshToken = "";
                                tokenType = "";
                                app.sessionExpired = false;
                                CASHe.blocked = 1;
                                CASHe.oneRequest = true;
                                i = new Intent(Base.this, SignIn.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                                break;
                        }

                    }
                    else {
                        switch (position) {
                            case 0:
                                if (customerStatusName.equalsIgnoreCase("Data Verification Failed") ||
                                        customerStatusName.equalsIgnoreCase("Credit Declined")) {
                                    i = new Intent(Base.this, Verification.class);
                                    i.putExtra("name", name);
                                    i.putExtra("email", email);
                                    i.putExtra("custID", custID);
                                    i.putExtra("accessToken", accessToken);
                                    i.putExtra("refreshToken", refreshToken);
                                    i.putExtra("tokenType", tokenType);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(i);
                                    //finish();
                                } else {
                                    i = new Intent(Base.this, Dashboard.class);
                                    i.putExtra("name", name);
                                    i.putExtra("email", email);
                                    i.putExtra("custID", custID);
                                    i.putExtra("accessToken", accessToken);
                                    i.putExtra("refreshToken", refreshToken);
                                    i.putExtra("tokenType", tokenType);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);

                                }

                                //finish();
                                break;
                            case 1:
                                StringRequest reqAuth = new StringRequest(com.android.volley.Request.Method.GET, urlStatus,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonStatus = new JSONObject(response.toString());
                                                    String statusType = jsonStatus.getString("statusType");
                                                    if (statusType.equalsIgnoreCase("OK")) {
                                                        JSONObject jsonEntity = new JSONObject(jsonStatus.getString("entity"));
                                                        String customerStatusName = jsonEntity.getString("customerStatusName");
                                                        if (customerStatusName.equalsIgnoreCase("Defaulted") || customerStatusName.equalsIgnoreCase("Payment Overdue")
                                                                || customerStatusName.equalsIgnoreCase("Payment Overdue") ||
                                                                customerStatusName.equalsIgnoreCase("Credit Declined") ||
                                                                customerStatusName.equalsIgnoreCase("Permanent Block") ||
                                                                customerStatusName.equalsIgnoreCase("Defaulted Block") ||
                                                                customerStatusName.equalsIgnoreCase("Temporary Block")) {
                                                            Toast.makeText(Base.this, "You are not allowed to edit your profile.", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Intent i = new Intent(Base.this, EditProfile.class);
                                                            i.putExtra("name", name);
                                                            i.putExtra("email", email);
                                                            i.putExtra("custID", custID);
                                                            i.putExtra("accessToken", accessToken);
                                                            i.putExtra("refreshToken", refreshToken);
                                                            i.putExtra("tokenType", tokenType);
                                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                            startActivity(i);
                                                        }
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
                                                if (error instanceof NoConnectionError) {
                                                    Toast.makeText(Base.this, "No Internet access. Please check your connection.", Toast.LENGTH_LONG).show();
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
                                rQueue.getInstance(Base.this).queueRequest("EditProfile", reqAuth);
                                //finish();
                                break;
                            case 2:
                                i = new Intent(Base.this, RepayScreen.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                startActivity(i);
                                //finish();
                                break;
                            case 3:
                                i = new Intent(Base.this, CasheHistory.class);
                                i.putExtra("name", name);
                                i.putExtra("email", email);
                                i.putExtra("custID", custID);
                                i.putExtra("accessToken", accessToken);
                                i.putExtra("refreshToken", refreshToken);
                                i.putExtra("tokenType", tokenType);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                startActivity(i);
                                //finish();
                                break;
                            case 4:
                                i = new Intent(Base.this, WhyCASHe.class);
                                i.putExtra("name", name);
                                i.putExtra("email", email);
                                i.putExtra("custID", custID);
                                i.putExtra("accessToken", accessToken);
                                i.putExtra("refreshToken", refreshToken);
                                i.putExtra("tokenType", tokenType);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                break;
                            case 5:
                                i = new Intent(Base.this, About.class);
                                i.putExtra("name", name);
                                i.putExtra("email", email);
                                i.putExtra("custID", custID);
                                i.putExtra("accessToken", accessToken);
                                i.putExtra("refreshToken", refreshToken);
                                i.putExtra("tokenType", tokenType);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                break;
                            case 6:
                                //FAQs
                                try {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(faqURL));
                                    startActivity(browserIntent);
                                }catch (Exception e){}
                                break;
                            case 7:
                                i = new Intent(Base.this, Privacy.class);
                                i.putExtra("name", name);
                                i.putExtra("email", email);
                                i.putExtra("custID", custID);
                                i.putExtra("accessToken", accessToken);
                                i.putExtra("refreshToken", refreshToken);
                                i.putExtra("tokenType", tokenType);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                break;
                            case 8:
                                i = new Intent(Base.this, Terms.class);
                                i.putExtra("name", name);
                                i.putExtra("email", email);
                                i.putExtra("custID", custID);
                                i.putExtra("accessToken", accessToken);
                                i.putExtra("refreshToken", refreshToken);
                                i.putExtra("tokenType", tokenType);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(i);
                                break;
                            case 9:
                                name = "";
                                email = "";
                                custID = "";
                                accessToken = "";
                                refreshToken = "";
                                tokenType = "";
                                app.sessionExpired = false;
                                CASHe.oneRequest = true;
                                i = new Intent(Base.this, SignIn.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                                break;
                        }

                    }
                } else {

                    switch (position) {
                        case 0:
                            i = new Intent(Base.this, WhyCASHe.class);
                            i.putExtra("name", name);
                            i.putExtra("email", email);
                            i.putExtra("custID", custID);
                            i.putExtra("accessToken", accessToken);
                            i.putExtra("refreshToken", refreshToken);
                            i.putExtra("tokenType", tokenType);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                            break;
                        case 1:
                            i = new Intent(Base.this, RepayScreen.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                            startActivity(i);
                            //finish();
                            break;
                        case 2:
                            i = new Intent(Base.this, About.class);
                            i.putExtra("name", name);
                            i.putExtra("email", email);
                            i.putExtra("custID", custID);
                            i.putExtra("accessToken", accessToken);
                            i.putExtra("refreshToken", refreshToken);
                            i.putExtra("tokenType", tokenType);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                            break;
                        case 3:
                            //FAQs
                            try {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(faqURL));
                                startActivity(browserIntent);
                            }catch (Exception e){}
                            break;
                        case 4:
                            i = new Intent(Base.this, Privacy.class);
                            i.putExtra("name", name);
                            i.putExtra("email", email);
                            i.putExtra("custID", custID);
                            i.putExtra("accessToken", accessToken);
                            i.putExtra("refreshToken", refreshToken);
                            i.putExtra("tokenType", tokenType);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                            break;
                        case 5:
                            i = new Intent(Base.this, Terms.class);
                            i.putExtra("name", name);
                            i.putExtra("email", email);
                            i.putExtra("custID", custID);
                            i.putExtra("accessToken", accessToken);
                            i.putExtra("refreshToken", refreshToken);
                            i.putExtra("tokenType", tokenType);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                            break;
                        case 6:
                            name = "";
                            email = "";
                            custID = "";
                            accessToken = "";
                            refreshToken = "";
                            tokenType = "";
                            CASHe.oneRequest = true;
                            app.sessionExpired = false;
                            i = new Intent(Base.this, SignIn.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                            break;

                    }
                }
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                }
            }
        });

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(null);
        getSupportActionBar().setElevation(0);

        typeFace(mDrawerLayout);
    }

    protected void typeFace(ViewGroup root) {

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/HUM521N_1.TTF");

        for(int i = 0; i <root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            if(v instanceof TextView ) {
                ((TextView)v).setTypeface(tf);
            /*} else if(v instanceof Button) {
                ((Button)v).setTypeface(tf);*/
            } else if(v instanceof EditText) {
                ((EditText) v).setTypeface(tf);
            } else if(v instanceof TextInputLayout) {
                ((TextInputLayout)v).setTypeface(tf);
            } else if(v instanceof ViewGroup) {
                typeFace((ViewGroup)v);
            }
        }
    }

    protected void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView ) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/HUM521L_1.TTF"));
            }
        } catch (Exception e) {
        }
    }

    protected boolean isVerified() {return true;}
    protected boolean isBlocked() {return false;}

    protected boolean isDefaulted() {return false;}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        //mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            if(mDrawerToggle != null)
                mDrawerToggle.onConfigurationChanged(newConfig);
        }catch(Exception e){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item != null && id == R.id.navMenu) {
            try {
                if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    item.setIcon(R.drawable.ic_menu_white_24dp);
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                    item.setIcon(R.drawable.ic_clear_white_24dp);
                }
            }catch (Exception e){}
            return true;
        }
        if (item != null && id == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        /*if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen)
            if(menu.findItem(R.id.navMenu)!=null) menu.findItem(R.id.navMenu).setIcon(R.drawable.ic_clear_white_24dp);
            else
            if(menu.findItem(R.id.navMenu)!=null) menu.findItem(R.id.navMenu).setIcon(R.drawable.ic_menu_white_24dp);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setMessage("Do you want to exit CASHe?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else { finish(); }
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.isInForeground = true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        app.isInForeground = false;
    }

    public boolean chkstr(String str){
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");

        Matcher matcher = pattern.matcher(str);

        if (!matcher.matches()) {
            return  false;
        } else {
            return true;
        }

    }
}
