package co.tslc.cashe.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class CasheSummaryConfirm extends Base {
    protected CASHe app;
    private Tracker mTracker;

    private String name;
    private String email;
    private String custID;
    private String casheAmt;
    private String inHand,bankName,accNum,ifsc;
    private String feeFlat;
    private String feeProc;
    private String feeProcLbl;
    private String feeProcAmt;

    private static String accessToken = "";
    private static String refreshToken = "" ;
    private static String tokenType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_cashe_summary_confirm);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_cashe_summary_confirm, frameLayout, true);
        typeFace(frameLayout);
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch(Exception e){}
       // getLayoutInflater().inflate(R.layout.activity_cashe_summary_confirm, frameLayout, true);
       // typeFace(frameLayout);

        app = (CASHe)getApplication();
        mTracker = app.getDefaultTracker();
        mTracker.setScreenName("CASHeSummaryConfirm");
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

        bankName = i.getStringExtra("bankName");
        accNum = i.getStringExtra("accNum");
        ifsc = i.getStringExtra("ifsc");
        TextView tvInHand = (TextView)findViewById(R.id.tvInHand);
        tvInHand.setText("\u20B9"+inHand.replace(".0",""));
        TextView tvBankName = (TextView)findViewById(R.id.tvBankName);
        tvBankName.setText(bankName);
        TextView tvAccNum = (TextView)findViewById(R.id.tvAccNum);
        tvAccNum.setText(accNum);
        TextView tvIFSC = (TextView)findViewById(R.id.tvIFSC);
        tvIFSC.setText(ifsc);

        ImageButton btnDone = (ImageButton)findViewById(R.id.ibDeposit);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CasheSummaryConfirm.this, Consent.class);
                i.putExtra("name", name);
                i.putExtra("email", email);
                i.putExtra("custID", custID);
                i.putExtra("accessToken", accessToken);
                i.putExtra("refreshToken", refreshToken);
                i.putExtra("tokenType", tokenType);
                i.putExtra("casheAmt",String.valueOf(casheAmt));
                i.putExtra("procFee",String.valueOf(feeProc));
                i.putExtra("flatFee",String.valueOf(feeFlat));
                i.putExtra("inHand",String.valueOf(inHand));
                startActivity(i);
                //finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tvInHand = (TextView)findViewById(R.id.tvInHand);
        tvInHand.setText("\u20B9" + inHand.replace(".0", ""));
        TextView tvBankName = (TextView)findViewById(R.id.tvBankName);
        tvBankName.setText(bankName);
        TextView tvAccNum = (TextView)findViewById(R.id.tvAccNum);
        tvAccNum.setText(accNum);
        TextView tvIFSC = (TextView)findViewById(R.id.tvIFSC);
        tvIFSC.setText(ifsc);
    }
}
