package com.example.singbike.Utilities;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Executes Runnable Tasks (Tasks which will not be executed on Main Threads (UI Threads).
 */
public class AppExecutor {

    private static final Object LOCK = new Object();
    private static AppExecutor instance;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkOps;

    private AppExecutor (Executor diskIO, Executor mainThread, Executor networkOps) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
        this.networkOps = networkOps;
    }

    public static AppExecutor getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new AppExecutor (
                        Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool (3),
                        new MainThreadExecutor()
                    );
            }
        }

        return instance;
    }

    public Executor getDiskIO() {
        return this.diskIO;
    }

    public Executor getMainThread() {
        return this.mainThread;
    }

    public Executor getNetworkOps() {
        return this.networkOps;
    }

    private static class MainThreadExecutor implements Executor {

        private final Handler mainThreadHandler = new Handler (Looper.getMainLooper());

        @Override
        public void execute (@NonNull Runnable runnable) {
            mainThreadHandler.post (runnable);
        }
    }

}
