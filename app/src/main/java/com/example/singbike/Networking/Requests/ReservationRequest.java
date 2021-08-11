package com.example.singbike.Networking.Requests;

public class ReservationRequest {

    public static class CreateReservationRequest {
        private final int customer;
        private final String bike;

        public CreateReservationRequest (int customer, String bike) {
            this.bike = bike;
            this.customer = customer;
        }

        public String getBike () {
            return this.bike;
        }

        public int getCustomer () {
            return this.customer;
        }
    }

    public static class EditReservationRequest {
        private final int cust_id;
        private final String bike_id;
        private final String status;

        public EditReservationRequest (int customer, String bike, String status) {
            this.bike_id = bike;
            this.cust_id = customer;
            this.status = status;
        }

        public String getBike () {
            return this.bike_id;
        }

        public int getCustomer () {
            return this.cust_id;
        }

        public String getStatus () {
            return this.status;
        }
    }

}
