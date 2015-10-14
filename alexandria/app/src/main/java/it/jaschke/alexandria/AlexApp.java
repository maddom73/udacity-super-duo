package it.jaschke.alexandria;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by maddom73 on 04/10/15.
 */
public class AlexApp extends Application {

    private RefWatcher aRefWatcher;

    public static AlexApp get(Context context) {
        return (AlexApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        aRefWatcher = installLeakCanary();
    }

    public RefWatcher getRefWatcher() {
        return aRefWatcher;
    }

    protected RefWatcher installLeakCanary() {
        return LeakCanary.install(this);

    }
}
