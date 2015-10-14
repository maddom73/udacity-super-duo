package barqsoft.footballscores;

import android.app.Application;
import android.content.Context;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by maddom73 on 06/10/15.
 */

public final class FootballApplication extends Application {

    private RefWatcher mRefWatcher;

    public static FootballApplication get(Context context) {
        return (FootballApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRefWatcher = installLeakCanary();
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    protected RefWatcher installLeakCanary() {
        return LeakCanary.install(this);

    }

}
