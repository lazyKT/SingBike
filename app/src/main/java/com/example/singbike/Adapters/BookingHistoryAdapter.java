package com.example.singbike.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Models.Booking;
import com.example.singbike.R;

import java.util.ArrayList;

public class BookingHistoryAdapter extends RecyclerView.Adapter <BookingHistoryAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView bookingTimeTV, bookingStatusTV, bookingDateTV;

        public ViewHolder (View v) {
            super(v);
            this.bookingStatusTV = v.findViewById (R.id.bookingStatusTextView);
            this.bookingTimeTV = v.findViewById (R.id.bookingTimeTextView);
            this.bookingDateTV = v.findViewById (R.id.bookingDateTextView);
        }

        public void bind (Booking booking) {
            this.bookingTimeTV.setText (booking.getBookingTime());
            this.bookingDateTV.setText (booking.getBookingDate());
            if (booking.getBookingStatus().equals("Cancelled"))
                this.bookingStatusTV.setTextColor(Color.RED);
            else
                this.bookingStatusTV.setTextColor(Color.DKGRAY);
            this.bookingStatusTV.setText (booking.getBookingStatus());
        }
    }

    private ArrayList<Booking> bookings;
    private Context context;

    public BookingHistoryAdapter (Context context, ArrayList<Booking> bookings) {
        this.context = context;
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup viewGroup, final int position) {

        View v = LayoutInflater.from (viewGroup.getContext())
                .inflate (R.layout.booking_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.bind (this.bookings.get(position));
    }


    @Override
    public int getItemCount()
    {
        return bookings.size();
    }
}
