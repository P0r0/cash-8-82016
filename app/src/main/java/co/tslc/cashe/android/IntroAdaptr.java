package co.tslc.cashe.android;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 23/01/16.
 */
public class IntroAdaptr extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;

    int[] mResources = {
            R.drawable.intro_screens_01,
            R.drawable.intro_screens_02,
            R.drawable.intro_screens_03,
    };

    public IntroAdaptr(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View itemView = mLayoutInflater.inflate(R.layout.pager_intro, container, false);
        try {
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setBackgroundResource(mResources[position]);

        } catch (OutOfMemoryError outOfMemoryError ) {
            Log.e("IFB", "outOfMemoryError while reading file for sampleSize ");
        }
        catch (Throwable thrw ) {
            Log.e("IFB", "outOfMemoryError Throwable while reading file for sampleSize ");
        }
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}