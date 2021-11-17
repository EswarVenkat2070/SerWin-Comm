package com.ase.serwincomm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.PlaceViewHolder> {

    List<Places> places;
    Context context;
    CustomAdapter(List<Places> places, Context context){
        this.places = places;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomAdapter.PlaceViewHolder  onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.place_detail_list_item, viewGroup, false);
        //  v.setOnClickListener(individualType.myOnClickListener);
        PlaceViewHolder  pvh = new PlaceViewHolder (v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.PlaceViewHolder  holder, final int position) {
        holder.placeName.setText(places.get(position).getName());
        holder.placeAddr.setText(places.get(position).getAddress());
        String dist = String.format("%.2f km away", places.get(position).getDistance());
        holder.placeDist.setText(dist);

        if (places.get(position).getOpen()) {
            holder.placeOpen.setTextColor(context.getResources().getColor(R.color.colorOpenNow));
            holder.placeOpen.setText(context.getResources().getString(R.string.open));
        } else {
            holder.placeOpen.setTextColor(context.getResources().getColor(R.color.colorClosed));
            holder.placeOpen.setText(context.getResources().getString(R.string.closed));
        }

        holder.fab.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              //   new individualType.myOnClickListener(places.get(position).getAddress())
                                              String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", places.get(holder.getAdapterPosition()).getAddress());

                                              Uri gmmIntentUri = Uri.parse(uri);
                                              Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                              Intent intent = mapIntent.setPackage("com.google.android.apps.maps");
                                              context.startActivity(intent);

                                          }
                                      }
        );

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView placeName;
        TextView placeAddr;
        TextView placeDist;
        TextView placeOpen;
        FloatingActionButton fab;

        PlaceViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.card_view);
            placeName = itemView.findViewById(R.id.placeName);
            placeAddr = itemView.findViewById(R.id.placeAddr);
            placeDist = itemView.findViewById(R.id.distance);
            placeOpen = itemView.findViewById(R.id.placeOpen);
            fab = itemView.findViewById(R.id.getDirections);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
