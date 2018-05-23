package com.rpsc.app.rpsc_jsoup;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.rpsc.app.rpsc_jsoup.R;

/**
 * Created by Mahendra on 11/27/2015.
 */
public class ListViewAdapter extends BaseAdapter implements Filterable {

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

    PressNoteActivity pna_instance = new PressNoteActivity();

    public ListViewAdapter(Context context,
                           ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;
        data_temp = arraylist;
//        imageLoader = new ImageLoader(context);
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
        TextView papernotetitle;
//        ImageView flag;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        // Get the position
        resultp = data.get(position);

        // Locate the TextViews in listview_item.xml
        sno = (TextView) itemView.findViewById(R.id.sno);
        date = (TextView) itemView.findViewById(R.id.date);
        examname = (TextView) itemView.findViewById(R.id.examname);
        papernotetitle = (TextView) itemView.findViewById(R.id.papernotetitle);

        // Locate the ImageView in listview_item.xml
//        flag = (ImageView) itemView.findViewById(R.id.flag);

        // Capture position and set results to the TextViews
        sno.setText(resultp.get(PressNoteActivity.SNO));
        date.setText(resultp.get(PressNoteActivity.DATE));
        examname.setText(resultp.get(PressNoteActivity.EXAMNAME));
        papernotetitle.setText(resultp.get(PressNoteActivity.PAPERNOTETITLE));

        // Capture position and set results to the ImageView
        // Passes flag images URL into ImageLoader.class
//        imageLoader.DisplayImage(resultp.get(MainActivity.FLAG), flag);



        // Capture ListView item click
        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                resultp = data.get(position);
                Intent intent = new Intent(context, SingleItemView.class);
                // Pass all data sno
                intent.putExtra("sno", resultp.get(PressNoteActivity.SNO));
                // Pass all data date
                intent.putExtra("date", resultp.get(PressNoteActivity.DATE));
                // Pass all data exam name
                intent.putExtra("examname",resultp.get(PressNoteActivity.EXAMNAME));
                // Pass all data paper note title
                intent.putExtra("papernotetitle",resultp.get(PressNoteActivity.PAPERNOTETITLE));
                intent.putExtra("fileurl",resultp.get(PressNoteActivity.FILEURL));
                // Pass all data flag
//                intent.putExtra("flag", resultp.get(PressNoteActivity.FLAG));
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
                //ArrayList<Country> filterList = new ArrayList<Country>();
                //List<data> nPlanetList = new ArrayList<data>();
                ArrayList<HashMap<String, String>> FilterList = new ArrayList<HashMap<String, String>>();

                for (int i = 0; i < data_temp.size(); i++) {
                    if ( (data_temp.get(i).get(PressNoteActivity.EXAMNAME).toUpperCase() )
                            .contains(constraint.toString().toUpperCase())) {
                        FilterList.add(data_temp.get(i));
                    }
                }
                results.count = FilterList.size();
                results.values = FilterList;
            } else {
                results.count = data_temp.size();
                results.values = data_temp;
                Log.d("Size of results",String.valueOf(results.count));
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            //countrylist = (ArrayList<Country>) results.values;
            data = (ArrayList<HashMap<String,String>>) results.values;
            notifyDataSetChanged();

            pna_instance.updateCount(results.count);
        }

    }

}