package co.tslc.cashe.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Filter;

/**
 * 30/12/15.
 */
public class companySuggest extends ArrayAdapter<String> {

    private List<ListCompanies> suggestions;
    private List<String> listCompaniesFiltered;
    public companySuggest(Activity context, String nameFilter, List<ListCompanies> listCompanies) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        suggestions = listCompanies;
        listCompaniesFiltered = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return listCompaniesFiltered.size();
    }

    @Override
    public String getItem(int index) { return listCompaniesFiltered.get(index); }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {

                    filterResults.values = listCompaniesFiltered;
                    filterResults.count = listCompaniesFiltered.size();
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

    private void fetchCompanies(String pfx)
    {
        /*if(pfx.length()<3) return;
        OkHttpClient client = new OkHttpClient();
        com.squareup.okhttp.Response response = null;
        final Request request = new Request.Builder()
                .url(urlCompanies+pfx)
                .addHeader("Authorization","bearer "+ accessToken)
                .build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonCompanies = new JSONObject(response.body().string());
            JSONArray arrCompanies = jsonCompanies.getJSONArray("entity");
            for (int i = 0; i < arrCompanies.length(); i++) {
                String companyName = arrCompanies.getJSONObject(i).getString("name");
                String companyID = arrCompanies.getJSONObject(i).getString("id");
                listCompanies.add(new ListCompanies(companyID, companyName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        /*listCompanies.add(new ListCompanies("1","aaa"));
        listCompanies.add(new ListCompanies("2","aab"));
        listCompanies.add(new ListCompanies("3","aac"));
        listCompanies.add(new ListCompanies("4","aad"));
        final AutoCompleteTextView actvCompany = (AutoCompleteTextView)findViewById(R.id.actvEmpName);
        companySuggest companySuggest = new companySuggest(SignUp.this, actvCompany.getText().toString(),listCompanies);
        companySuggest.notifyDataSetChanged();
        actvCompany.setAdapter(companySuggest);*/

    }

}