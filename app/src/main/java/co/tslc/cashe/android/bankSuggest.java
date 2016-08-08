package co.tslc.cashe.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Filter;

/**
 * 30/12/15.
 */
public class bankSuggest extends ArrayAdapter<String> {

    private List<ListBanks> suggestions;
    private List<String> listBanksFiltered;
    public bankSuggest(Activity context, String nameFilter, List<ListBanks> listBanks) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        suggestions = listBanks;
        listBanksFiltered = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return listBanksFiltered.size();
    }

    @Override
    public String getItem(int index) {
        try {
            return index < listBanksFiltered.size() ? listBanksFiltered.get(index) : "";
        }catch(Exception e){
            return "";
        }
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                try {
                    if (constraint != null) {
                        listBanksFiltered.clear();
                        listBanksFiltered = new ArrayList<String>();
                        if (null != suggestions) {
                            for (int i = 0; i < suggestions.size(); i++) {
                                if (suggestions.get(i).getName().contains(constraint)) {
                                    listBanksFiltered.add(suggestions.get(i).getName());
                                }
                            }
                        }
                        filterResults.values = listBanksFiltered;
                        filterResults.count = listBanksFiltered.size();
                    }
                }catch(Exception e){
                    listBanksFiltered = new ArrayList<String>();
                    filterResults.values = listBanksFiltered;
                    filterResults.count = listBanksFiltered.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }

}