package com.sdsmdg.cognizance2017.adapters;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sdsmdg.cognizance2017.FavReceiver;
import com.sdsmdg.cognizance2017.R;
import com.sdsmdg.cognizance2017.activities.MainActivity;
import com.sdsmdg.cognizance2017.models.EventModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;

import static android.content.Context.ALARM_SERVICE;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Context ctx;
    private List<EventModel> eventsList;
    private boolean isInFav;
    private List<EventModel> normalEventList;
    private Realm realm;

    public RecyclerAdapter(Context ctx, List<EventModel> eventsList, boolean isInFav) {
        this.ctx = ctx;
        this.eventsList = eventsList;
        this.isInFav = isInFav;
        normalEventList = new ArrayList<EventModel>();
        try {
            realm = Realm.getDefaultInstance();
            normalEventList.addAll(realm.copyFromRealm(eventsList));
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView clockIcon, markerIcon;
        public TextView titleText, locationText, timeText;
        public ToggleButton checkBox;
        View divider;

        public MyViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.event_title);
            locationText = (TextView) itemView.findViewById(R.id.event_location);
            timeText = (TextView) itemView.findViewById(R.id.event_time);
            checkBox = (ToggleButton) itemView.findViewById(R.id.toggle);
            clockIcon = (ImageView) itemView.findViewById(R.id.clock);
            markerIcon = (ImageView) itemView.findViewById(R.id.marker);
            divider = itemView.findViewById(R.id.divider);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ((MainActivity) ctx).showEvent();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(ctx).inflate(R.layout.event_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final EventModel currentEvent = normalEventList.get(position);
        holder.titleText.setText(currentEvent.getName());
        if (currentEvent.getTime().equals(""))
            holder.timeText.setText("Time");
        else {
            holder.timeText.setText(currentEvent.getTime());
        }
        if (currentEvent.getVenue().equals("")) {
            holder.locationText.setText("Venue");
        } else {
            holder.locationText.setText(currentEvent.getVenue());
        }
        if (currentEvent.isFav()) {
            holder.locationText.setTextColor(ctx.getResources().getColor(R.color.colorPrimarySelected));
            holder.timeText.setTextColor(ctx.getResources().getColor(R.color.colorPrimarySelected));
            holder.titleText.setTextColor(ctx.getResources().getColor(R.color.colorPrimarySelected));
            holder.clockIcon.setColorFilter(ctx.getResources().getColor(R.color.colorPrimarySelected));
            holder.markerIcon.setColorFilter(ctx.getResources().getColor(R.color.colorPrimarySelected));
            holder.divider.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimarySelected));
        } else {
            holder.locationText.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
            holder.timeText.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
            holder.titleText.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
            holder.clockIcon.setColorFilter(ctx.getResources().getColor(R.color.colorPrimary));
            holder.markerIcon.setColorFilter(ctx.getResources().getColor(R.color.colorPrimary));
            holder.divider.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));

        }

        //in some cases, it will prevent unwanted situations
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(currentEvent.isFav());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Realm.init(ctx);
                realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                EventModel eventModel = realm.where(EventModel.class).equalTo("id", currentEvent.getId()).findFirst();
                eventModel.setFav(isChecked);
                realm.commitTransaction();
                currentEvent.setFav(isChecked);
                if (isChecked) {
                    //Calendar calendar = currentEvent.getNotificationTime();
                    //createNotification(calendar.getTimeInMillis());
                    //createNotification(System.currentTimeMillis());
                    holder.locationText.setTextColor(ctx.getResources().getColor(R.color.colorPrimarySelected));
                    holder.timeText.setTextColor(ctx.getResources().getColor(R.color.colorPrimarySelected));
                    holder.titleText.setTextColor(ctx.getResources().getColor(R.color.colorPrimarySelected));
                    holder.clockIcon.setColorFilter(ctx.getResources().getColor(R.color.colorPrimarySelected));
                    holder.markerIcon.setColorFilter(ctx.getResources().getColor(R.color.colorPrimarySelected));
                    holder.divider.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimarySelected));
                } else {
                    cancelNotification(0);
                    if (isInFav) {
                        deleteFromFav(holder.getAdapterPosition());
                    } else {
                        holder.locationText.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                        holder.timeText.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                        holder.titleText.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                        holder.clockIcon.setColorFilter(ctx.getResources().getColor(R.color.colorPrimary));
                        holder.markerIcon.setColorFilter(ctx.getResources().getColor(R.color.colorPrimary));
                        holder.divider.setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return normalEventList.size();
    }


    private void createNotification(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 47);
        Intent intent = new Intent(ctx, FavReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 12, intent, 0);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(ctx, "" + time, Toast.LENGTH_LONG).show();
    }

    private void cancelNotification(int id) {

        Intent intent = new Intent(ctx, FavReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 12, intent, 0);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(ctx, "Alarm has been cancelled" + System.currentTimeMillis() + 60 * 1000, Toast.LENGTH_LONG).show();
    }

    private void deleteFromFav(int index) {
        normalEventList.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, normalEventList.size());
    }
}
