package com.example.singbike.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.BookingHistoryAdapter;
import com.example.singbike.Models.Booking;
import com.example.singbike.R;
import java.util.ArrayList;

public class BookingFragment extends Fragment {

    private ArrayList<Booking> bookings;

    public BookingFragment () {
        super(R.layout.fragment_booking);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        /* this method will run as soon as the booking fragment is mounted */
        bookings = new ArrayList<>();
        bookings.add (new Booking("Trip Completed", "2021 Jul 3", "15:20"));
        bookings.add (new Booking("Cancelled", "2021 Jul 3", "15:10"));
        bookings.add (new Booking("Trip Completed", "2021 May 3", "10:20"));
        bookings.add (new Booking("Trip Completed", "2021 May 13", "8:20"));
        bookings.add (new Booking("Cancelled", "2021 Jul 10", "19:20"));
    }

    @Override
    public void onViewCreated (@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);

        final TextView currentBookingStatusTV = view.findViewById (R.id.currentBookingTextView);
        final RecyclerView currentBookingRecyclerView = view.findViewById (R.id.currentBookingRecyclerView);
        final RecyclerView bookingHistoryRecyclerView = view.findViewById (R.id.bookingHistoryRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager (requireActivity());
        bookingHistoryRecyclerView.setLayoutManager(layoutManager);

        BookingHistoryAdapter bookingHistoryAdapter = new BookingHistoryAdapter(requireActivity(), bookings);
        bookingHistoryRecyclerView.setAdapter (bookingHistoryAdapter);
    }
}
