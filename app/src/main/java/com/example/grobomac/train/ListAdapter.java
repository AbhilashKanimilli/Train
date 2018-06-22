package com.example.grobomac.train;

/**
 * Created by abhi on 5/30/2018.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<Status>
{
    String name, id;
    Context context;
    List<Status> valueList;

    public ArrayList<Status> MainList;

    public ArrayList<Status> SubjectListTemp;
    public ListAdapter.SubjectDataFilter subjectDataFilter ;
    public ListAdapter(Context context, int id, ArrayList<Status> subjectArrayList) {

        super(context, id, subjectArrayList);

        this.SubjectListTemp = new ArrayList<Status>();

        this.SubjectListTemp.addAll(subjectArrayList);

        this.MainList = new ArrayList<Status>();

        this.MainList.addAll(subjectArrayList);
    }
    @Override
    public Filter getFilter() {

        if (subjectDataFilter == null){

            subjectDataFilter  = new ListAdapter.SubjectDataFilter();
        }
        return subjectDataFilter;
    }
   /* public ListAdapter(ArrayList<Status> listValue, Context context)
    {
        super(listValue, context);
        this.context = context;
        this.valueList = listValue;
    }
    */

    /*@Override
    public int getCount()
    {
        return MainList.size();
    }

    /*@Override
    public Object getItem(int position)
    {
        return this.valueList.get(position);
    }
*/
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewItem viewItem = null;
        if(convertView == null)
        {
            viewItem = new ViewItem();
            LayoutInflater layoutInfiater = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            //LayoutInflater layoutInfiater = LayoutInflater.from(context);
            convertView = layoutInfiater.inflate(R.layout.list_adapter_view, null);

            viewItem.txtTitle = (TextView)convertView.findViewById(R.id.adapter_text_title);
            viewItem.txtDescription = (TextView)convertView.findViewById(R.id.adapter_text_description);
            viewItem.txtid=(TextView)convertView.findViewById(R.id.adapter_text_status);
            convertView.setTag(viewItem);
        }
        else
        {
            viewItem = (ViewItem) convertView.getTag();
        }
        Status status = SubjectListTemp.get(position);
        viewItem.txtTitle.setText(status.getD_name());
        viewItem.txtid.setText(status.getD_id());
        //viewItem.txtTitle.setText(valueList.get(position).d_name);
        //viewItem.txtid.setText(valueList.get(position).d_id);
        if((status.getT_status()).equals("-1")){
            name= status.getD_name();
            id = status.getD_id();
            viewItem.txtDescription.setText("Partially Completed\n(Click to Train again)");
            viewItem.txtDescription.setTextColor(Color.parseColor("#FF0000"));

        }else{
            viewItem.txtDescription.setText("Successfully Completed");
            viewItem.txtDescription.setTextColor(Color.parseColor("#008000"));
        }




        return convertView;
    }
    private class SubjectDataFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            charSequence = charSequence.toString().toLowerCase();

            FilterResults filterResults = new FilterResults();

            if(charSequence != null && charSequence.toString().length() > 0)
            {
                ArrayList<Status> arrayList1 = new ArrayList<Status>();

                for(int i = 0, l = MainList.size(); i < l; i++)
                {
                    Status subject = MainList.get(i);

                    if(subject.toString().toLowerCase().contains(charSequence))

                        arrayList1.add(subject);
                }
                filterResults.count = arrayList1.size();

                filterResults.values = arrayList1;
            }
            else
            {
                synchronized(this)
                {
                    filterResults.values = MainList;

                    filterResults.count = MainList.size();
                }
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            SubjectListTemp = (ArrayList<Status>)filterResults.values;

            notifyDataSetChanged();

            clear();

            for(int i = 0, l = SubjectListTemp.size(); i < l; i++)
                add(SubjectListTemp.get(i));

            notifyDataSetInvalidated();
        }
    }

}

class ViewItem
{
    TextView txtTitle;
    TextView txtDescription;
    TextView txtid;
}
