package co.tslc.cashe.android;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 08/01/16.
 */
public class NavItemAdaptr extends ArrayAdapter<navItem> {
    Context mContext;
    int layoutResourceId;
    navItem data[] = null;

    public NavItemAdaptr(Context mContext, int layoutResourceId, navItem[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.ivItemIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.tvItemText);
        if(data != null) {
            navItem folder = data[position];

            imageViewIcon.setImageResource(folder.icon);
            textViewName.setText(folder.text);
        }

        return listItem;
    }
}