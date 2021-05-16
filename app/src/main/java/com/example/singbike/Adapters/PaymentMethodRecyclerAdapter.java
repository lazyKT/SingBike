package com.example.singbike.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.Models.PaymentMethod;
import com.example.singbike.R;

import java.util.ArrayList;

public class PaymentMethodRecyclerAdapter extends RecyclerView.Adapter<PaymentMethodRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView cardNumberTV;
        final ImageView cardImageView;
        final RadioButton cardSelectedRb;

        public ViewHolder (View v) {
            super(v);
            cardImageView = v.findViewById (R.id.paymentCardImage);
            cardNumberTV = v.findViewById (R.id.cardTextView);
            cardSelectedRb = v.findViewById (R.id.paymentMethodRadioButton);
        }

        public void bind (PaymentMethod paymentMethod) {

            /* set card info */
            this.cardNumberTV.setText(paymentMethod.getCardNum());
            /* set card type image */
            switch (paymentMethod.getType()) {
                case "Master":
                    this.cardImageView.setImageResource(R.drawable.mastercard);
                    break;
                case "Visa":
                    this.cardImageView.setImageResource(R.drawable.visa);
                    break;
                default:
                    this.cardImageView.setImageResource(R.drawable.creditcard);
                    break;
            }
        }
    }

    private ArrayList<PaymentMethod> paymentMethods;

    public PaymentMethodRecyclerAdapter (ArrayList<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
//        this.paymentMethods.add (new PaymentMethod("Credit Card", "Master", "5264 7210 3456 6789", "12/25", "ALEX SU", "007"));
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from (parent.getContext())
                .inflate (R.layout.payment_method_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder viewHolder, final int position) {

        viewHolder.bind (this.paymentMethods.get(position));
    }

    @Override
    public int getItemCount () {
        return this.paymentMethods.size();
    }

    /* add new card */
    public void addNewCard (PaymentMethod paymentMethod) {
        this.paymentMethods.add(paymentMethod);
    }

    /* get all cards */
    public ArrayList<PaymentMethod> getPaymentMethods () {
        return this.paymentMethods;
    }
}
