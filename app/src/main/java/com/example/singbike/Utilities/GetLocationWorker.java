package com.example.singbike.Utilities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class GetLocationWorker extends Worker {

    public GetLocationWorker (@NonNull Context context, @NonNull WorkerParameters params) {
        super (context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        

        return Result.success();
    }
}
