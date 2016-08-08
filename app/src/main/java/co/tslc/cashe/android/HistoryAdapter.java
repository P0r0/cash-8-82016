package co.tslc.cashe.android;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 30/12/15.
 */
public class HistoryAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Loan> loanItems;

    public HistoryAdapter(Activity activity, List<Loan> loanItems) {
        this.activity = activity;
        this.loanItems = loanItems;
    }

    @Override
    public int getCount() {
        return loanItems.size();
    }

    @Override
    public Object getItem(int location) {
        return loanItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.cashe_history, null);
        }

        TextView tvTCA = (TextView) convertView.findViewById(R.id.tvTCA);
        TextView tvCasheAmt = (TextView) convertView.findViewById(R.id.tvCasheAmt);
        TextView tvDaysLeft = (TextView) convertView.findViewById(R.id.tvDaysLeft);
        TextView tvCAmt = (TextView) convertView.findViewById(R.id.tvCAmt);
        TextView tvIntAmt = (TextView) convertView.findViewById(R.id.tvIntAmt);
        TextView tvDueDate = (TextView) convertView.findViewById(R.id.tvDueDate);
        TextView tvShowHide = (TextView)convertView.findViewById(R.id.tvToggle);
        TextView tvPenalty = (TextView)convertView.findViewById(R.id.tvPenalty);
        TextView tvPenaltyAmt = (TextView)convertView.findViewById(R.id.tvPenaltyAmt);


        final RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.rlDetails);

        Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/DINMedium.ttf");
        tvCasheAmt.setTypeface(tf);
        tf = Typeface.createFromAsset(activity.getAssets(), "fonts/HUM521N_1.TTF");
        tvTCA.setTypeface(tf);
        tvShowHide.setTypeface(tf);
        TextView tvCA = (TextView) convertView.findViewById(R.id.tvCA);
        tvCA.setTypeface(tf);
        TextView tvInt = (TextView) convertView.findViewById(R.id.tvInt);
        tvInt.setTypeface(tf);
        TextView tvDueDt = (TextView) convertView.findViewById(R.id.tvDueDt);
        tvDueDt.setTypeface(tf);
        ImageView icn = (ImageView) convertView.findViewById(R.id.icn);
        tvTCA.setTypeface(tf);
        tvDaysLeft.setTypeface(tf);
        tvCAmt.setTypeface(tf);
        tvIntAmt.setTypeface(tf);
        tvDueDate.setTypeface(tf);
        tvPenalty.setTypeface(tf);
        tvPenaltyAmt.setTypeface(tf);

        Loan l = loanItems.get(position);

        //String lStatus = l.getLoanStatus();
        //Log.d("LS", lStatus);

        tvCasheAmt.setText("\u20B9" + l.getTotalDueAmount());
        if(l.getDaysLeftMsg()!=null) {
            if (!l.getDaysLeftMsg().equalsIgnoreCase("null"))
                //tvDaysLeft.setText(l.getLoanStatus() + "\n" + l.getDaysLeftMsg());
                tvDaysLeft.setText(l.getDaysLeftMsg());
        }
        tvCAmt.setText("\u20B9" + l.getLoanAmount());
        tvIntAmt.setText("\u20B9" + l.getProcessingFee());
        tvDueDate.setText(l.getDueDate());
        if(Double.parseDouble(l.getPenaltyAmount())>0)
        {
            tvCasheAmt.setTextColor(Color.parseColor("#F76456"));
            tvDaysLeft.setTextColor(Color.parseColor("#F76456"));
            tvPenalty.setVisibility(View.VISIBLE);
            tvPenaltyAmt.setText("\u20B9" + l.getPenaltyAmount());
            tvPenalty.setTextColor(Color.parseColor("#F76456"));
            tvPenaltyAmt.setTextColor(Color.parseColor("#F76456"));
            tvPenaltyAmt.setVisibility(View.VISIBLE);
        }
        else if(l.getLoanStatus().equalsIgnoreCase("Processing") || l.getLoanStatus().equalsIgnoreCase("Cash Transfer Wait Insufficient Funds") || l.getLoanStatus().equalsIgnoreCase("Cash Transfer Failed") || l.getLoanStatus().equalsIgnoreCase("Cash Transfer with Bank") || l.getLoanStatus().equalsIgnoreCase("Cash Transfer Retry"))
        {
            tvDaysLeft.setText("Processing");
            icn.setImageResource(R.drawable.graphic_processing);
            icn.setVisibility(View.VISIBLE);
        }
        else if(l.getLoanStatus().equalsIgnoreCase("Cash Repaid"))
        {
            tvDaysLeft.setTextColor(Color.parseColor("#89BA27"));
            tvDaysLeft.setText("Paid");
            icn.setImageResource(R.drawable.graphic_paid);
            icn.setVisibility(View.VISIBLE);
        }
        else
        {
            tvDaysLeft.setText(l.getLoanStatus());
        }

        /*tvShowHide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View cnvVue = null;
                if (cnvVue == null)
                {
                    cnvVue = inflater.inflate(R.layout.cashe_history, null);
                }
                RelativeLayout rlDetails = (RelativeLayout)cnvVue.findViewById(R.id.rlDetails);
                if(rl.getVisibility()==View.GONE)
                {
                    TextView tvShowHide = (TextView)cnvVue.findViewById(R.id.tvToggle);
                    tvShowHide.setText("SHOW DETAILS");
                    rl.setAlpha(0);
                    rl.setVisibility(View.VISIBLE);
                    rl.animate().alpha(1).setDuration(500);
                }
                else {
                    TextView tvShowHide = (TextView)cnvVue.findViewById(R.id.tvToggle);
                    tvShowHide.setText("HIDE DETAILS");
                    rl.animate().alpha(0).setDuration(3000);
                    rl.setVisibility(View.GONE);
                }
            }
        });*/

        return convertView;
    }
}