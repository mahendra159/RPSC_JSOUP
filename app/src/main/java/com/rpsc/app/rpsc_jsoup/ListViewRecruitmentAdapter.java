package com.rpsc.app.rpsc_jsoup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.rpsc.app.rpsc_jsoup.R;

/**
 * Created by Mahendra on 11/28/2015.
 */
public class ListViewRecruitmentAdapter extends BaseAdapter implements Filterable {

    ValueFilter valueFilter;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;
    ArrayList<HashMap<String, String>> data_temp;
    //    ImageLoader imageLoader;
    HashMap<String, String> resultp = new HashMap<String, String>();

    RecruitmentActivity ra_instance = new RecruitmentActivity();

    public ListViewRecruitmentAdapter(Context context,
                           ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;
        data_temp = arraylist;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView sno;
        TextView date;
        TextView examname;
        TextView advttitle;
        TextView advtlabel;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        // Get the position
        resultp = data.get(position);

        // Locate the TextViews in listview_item.xml
        sno = (TextView) itemView.findViewById(R.id.sno);
        date = (TextView) itemView.findViewById(R.id.date);
        examname = (TextView) itemView.findViewById(R.id.examname);
        advttitle = (TextView) itemView.findViewById(R.id.papernotetitle);

        // setting label acc to this activity
        advtlabel = (TextView) itemView.findViewById(R.id.papernotelabel);
        advtlabel.setText("Advertisement Title : ");

        // Capture position and set results to the TextViews
        sno.setText(resultp.get(RecruitmentActivity.SNO));
        date.setText(resultp.get(RecruitmentActivity.ADVDATE));
        examname.setText(resultp.get(RecruitmentActivity.EXAMNAME));
        advttitle.setText(resultp.get(RecruitmentActivity.ADVTTITLE));
        // Capture position and set results to the ImageView
        // Passes flag images URL into ImageLoader.class

        // Capture ListView item click
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                resultp = data.get(position);
                Intent intent = new Intent(context, RecruitmentItemView.class);
                // Pass all data sno
                intent.putExtra("sno", resultp.get(RecruitmentActivity.SNO));
                // Pass all data date
                intent.putExtra("advtdate", resultp.get(RecruitmentActivity.ADVDATE));
                // Pass all data exam name
                intent.putExtra("examname",resultp.get(RecruitmentActivity.EXAMNAME));
                // Pass all data paper note title
                intent.putExtra("advertisementtitle",resultp.get(RecruitmentActivity.ADVTTITLE));
                intent.putExtra("fileurl",resultp.get(RecruitmentActivity.FILEURL));

                // Start SingleItemView Class
                context.startActivity(intent);

            }
        });
        return itemView;


    }


    @Override
    public Filter getFilter() {
        //return null;
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<HashMap<String, String>> FilterList = new ArrayList<HashMap<String, String>>();

                for (int i = 0; i < data_temp.size(); i++) {
                    if ( (data_temp.get(i).get(RecruitmentActivity.EXAMNAME).toUpperCase() )
                            .contains(constraint.toString().toUpperCase())) {
                        FilterList.add(data_temp.get(i));
                    }
                }
                results.count = FilterList.size();
                results.values = FilterList;
            } else {
                results.count = data_temp.size();
                results.values = data_temp;
                Log.d("Size of results", String.valueOf(results.count));
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            //countrylist = (ArrayList<Country>) results.values;
            data = (ArrayList<HashMap<String,String>>) results.values;
            notifyDataSetChanged();

            ra_instance.updateCount(results.count);
        }

    }
}
