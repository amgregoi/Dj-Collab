package com.teioh08.djcollab.UI.Main.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.teioh08.djcollab.Party;
import com.teioh08.djcollab.R;

import java.util.ArrayList;
import java.util.List;

public class SessionListAdapter extends ArrayAdapter {

    public final static String TAG = SessionListAdapter.class.getSimpleName();

    private ArrayList<Party> mSessions;
    private LayoutInflater mInflater;
    private Context context;
    private int layoutResource;

    public SessionListAdapter(Context context, int resource, List<Party> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mSessions = new ArrayList<>(objects);
        this.mInflater = LayoutInflater.from(context);
        this.layoutResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ChapterHolder holder;

        if (row == null) {
            row = mInflater.inflate(layoutResource, null);

            holder = new ChapterHolder();
            holder.mTitle = (TextView) row.findViewById(R.id.list_item);
            row.setTag(R.string.SessionListHolder, holder);
        } else {
            holder = (ChapterHolder) row.getTag(R.string.SessionListHolder);
        }

        holder.mTitle.setText(mSessions.get(position).getName());
        return row;
    }

    static class ChapterHolder {
        TextView mTitle;
    }
}