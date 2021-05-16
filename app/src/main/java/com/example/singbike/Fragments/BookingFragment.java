package com.example.singbike.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Adapters.BookingHistoryAdapter;
import com.example.singbike.Models.Booking;
import com.example.singbike.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class BookingFragment extends Fragment {

    private static final String DEBUG_BOOKINGHISTROY = "DEBUG_BOOKINGHISTORY";

    private ArrayList<Booking> bookings;
    private Booking currentBooking;

    public BookingFragment () {
        super(R.layout.fragment_booking);
    }

    @Override
    public void onCreate (Bundle savedInstaceState) {
        super.onCreate (savedInstaceState);

        TransitionInflater inflater = TransitionInflater.from (requireActivity());
        setEnterTransition (inflater.inflateTransition (R.transition.slide_right));
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
        bookings.add (new Booking("Cancelled", "2021 Jul 10", "19:20"));
        bookings.add (new Booking("Trip Completed", "2021 Jul 12", "19:20"));

        // set current Booking
        currentBooking = new Booking("Active", "2021 May 12", "19:20");
    }

    @Override
    public void onViewCreated (@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);

        /* Current Booking */
        final TextView currentBookingStatusTV = view.findViewById (R.id.currentBookingStatusTV);
        final CardView currentBookingCardView = view.findViewById (R.id.currentBookingCardView);
        final TextView currentBookingDateTimeTV = view.findViewById (R.id.currentBookingDateTime);
        final TextView currentBookingBikeID = view.findViewById (R.id.bikeID_currentBooking);

        currentBookingBikeID.setText("189863261");
        currentBookingDateTimeTV.setText(currentBooking.getBookingDateTime());
//        currentBookingStatusTV.setText(currentBooking.getBookingStatus());

        /* on longPress (hold) Event on currentBooking CardView */
        currentBookingCardView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick (View v) {

                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog (
                                requireActivity(),
                                R.style.BottomSheetDialogTheme);
                        View bottomSheetView = LayoutInflater.from (requireActivity())
                                .inflate (R.layout.booking_details, (LinearLayout) view.findViewById(R.id.BookingDetailsBottomSheet));

                        final TextView singleBookingDateTV = bottomSheetView.findViewById (R.id.bookingDateDetailsTV);
                        final TextView singleBookingTimeTV = bottomSheetView.findViewById (R.id.bookingTimeDetailsTV);
                        final TextView singleBookingStatusTV = bottomSheetView.findViewById (R.id.bookingStatusDetailsTV);
                        final TextView bikeIDBookingDetailsTV = bottomSheetView.findViewById (R.id.bikeIDBookingDetailsTV);

                        singleBookingDateTV.setText (currentBooking.getBookingDate());
                        singleBookingTimeTV.setText (currentBooking.getBookingTime());
                        singleBookingStatusTV.setText (currentBooking.getBookingStatus());
                        bikeIDBookingDetailsTV.setText ("189863261");

                        /* cancel booking */
                        final Button cancelButton = bottomSheetView.findViewById (R.id.cancelButtonBookingDetail);
                        cancelButton.setOnClickListener(
                                new View.OnClickListener () {
                                    @Override
                                    public void onClick (View v) {
                                        bottomSheetDialog.dismiss();
                                    }
                                }
                        );

                        bottomSheetDialog.setContentView (bottomSheetView);
                        bottomSheetDialog.show();

                        return true;
                    }
                }
        );

        /* Booking History */
        final RecyclerView bookingHistoryRecyclerView = view.findViewById (R.id.bookingHistoryRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager (requireActivity());
        bookingHistoryRecyclerView.setLayoutManager(layoutManager);

        BookingHistoryAdapter bookingHistoryAdapter = new BookingHistoryAdapter(requireActivity(), bookings);
        bookingHistoryRecyclerView.setAdapter (bookingHistoryAdapter);

        Log.d (DEBUG_BOOKINGHISTROY, "Number of bookings made: " + bookingHistoryAdapter.getItemCount());
    }
}
