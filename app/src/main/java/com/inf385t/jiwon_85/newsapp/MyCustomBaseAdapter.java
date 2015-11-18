package com.inf385t.jiwon_85.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by jiwon_85 on 11/5/15.
 */
public class MyCustomBaseAdapter extends BaseAdapter {
    private static ArrayList<PostResults> searchArrayList;
    private LayoutInflater mInflater;

    public MyCustomBaseAdapter(Context context, ArrayList<PostResults> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_row_view, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.txtDate = (TextView) convertView.findViewById(R.id.date);
//            holder.txtUser = (TextView) convertView.findViewById(R.id.user);
            holder.buttonUpvote = (Button) convertView.findViewById(R.id.upvote);
            holder.buttonDownvote = (Button) convertView.findViewById(R.id.downvote);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ParseObject p = searchArrayList.get(position).getParseObject();
        //TODO: format date to Day mm/dd/yy am/pm
        holder.txtDate.setText(p.getCreatedAt().toString()+ " by " + p.getString("user"));
        holder.txtTitle.setText(p.getString("title"));
//        holder.txtUser.setText(p.getString("user"));

        return convertView;
    }

    static class ViewHolder {
        TextView txtTitle;
        TextView txtDate;
//        TextView txtUser;
        Button buttonUpvote;
        Button buttonDownvote;

    }
}
