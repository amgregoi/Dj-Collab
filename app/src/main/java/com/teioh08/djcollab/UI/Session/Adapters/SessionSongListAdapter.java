package com.teioh08.djcollab.UI.Session.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.teioh08.djcollab.R;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class SessionSongListAdapter extends RecyclerView.Adapter<SessionSongListAdapter.ViewHolder> {

    private final List<Track> mItems = new ArrayList<>();
    private final Context mContext;
    private final ItemSelectedListener mListener;
    private final boolean mIsPlaylist;



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView title;
        public final TextView subtitle;
        public final ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.entity_title);
            subtitle = (TextView) itemView.findViewById(R.id.entity_subtitle);
            image = (ImageView) itemView.findViewById(R.id.entity_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(getLayoutPosition());
            mListener.onItemSelected(v, mItems.get(getAdapterPosition()));
        }
    }

    public interface ItemSelectedListener {
        void onItemSelected(View itemView, Track item);
    }

    public SessionSongListAdapter(Context context, ItemSelectedListener listener, boolean isPlaylist) {
        mContext = context;
        mListener = listener;
        mIsPlaylist = isPlaylist;
    }

    public void clearData() {
        mItems.clear();
    }

    public void addData(List<Track> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void addSingleData(Track items) {
        mItems.add(items);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(mIsPlaylist) v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        else v = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_que_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Track item = mItems.get(position);
        holder.title.setText(item.name);

        List<String> names = new ArrayList<>();
        for (ArtistSimple i : item.artists) {
            names.add(i.name);
        }

        holder.subtitle.setText(names.toString());

        Image image = item.album.images.get(0);
        if (image != null) {
            Glide.with(mContext).load(image.url).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}