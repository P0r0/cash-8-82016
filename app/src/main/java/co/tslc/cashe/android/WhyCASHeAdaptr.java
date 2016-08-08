package co.tslc.cashe.android;

import android.view.LayoutInflater;
import android.support.v4.view.PagerAdapter;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WhyCASHeAdaptr extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;

    int[] mResources = {
            R.drawable.why_cashe_01,
            R.drawable.why_cashe_02,
            //R.drawable.why_cashe_03,
            //R.drawable.why_cashe_04,
            //R.drawable.why_cashe_05
    };

    String[] vResources;

    public WhyCASHeAdaptr(Context context, String[] vResources) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.vResources = vResources;
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_why_cashe, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        imageView.setImageResource(mResources[position]);
        TextView textView = (TextView) itemView.findViewById(R.id.textView);
        textView.setText(vResources[position]);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}