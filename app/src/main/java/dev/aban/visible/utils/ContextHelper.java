package dev.aban.visible.utils;

import android.app.Application;
import android.content.Context;
import android.view.animation.AnimationUtils;

import com.flurry.android.FlurryAgent;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.pushpole.sdk.PushPole;

import dev.aban.visible.BuildConfig;
import dev.aban.visible.R;
import ir.tapsell.sdk.Tapsell;

public class ContextHelper extends Application {
    private static Context context;

    /**
     * retrieves context
     *
     * @return context
     */
    public static Context retrieveContext() {
        return context;
    }

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        PushPole.initialize(this, false);
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(this, BuildConfig.FLURRY_KEY);
        FlurryAgent.setUserId(PushPole.getId(this));
        AppCenter.start(this, BuildConfig.APPCENTER, Analytics.class, Crashes.class);
        Tapsell.initialize(this, BuildConfig.TAPSELL_KEY);
        initConstantObjects();
    }

    private void initConstantObjects() {
        Constants.SLIDE_UP_ANIMATION = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Constants.NORMAL_SCALE_ANIMATION = AnimationUtils.loadAnimation(this, R.anim.normal_scale);
        Constants.FAST_SCALE_ANIMATION = AnimationUtils.loadAnimation(this, R.anim.fast_scale);
        Constants.LTR_ANIMATION = AnimationUtils.loadAnimation(this, R.anim.slide_ltr);
        Constants.RTL_ANIMATION = AnimationUtils.loadAnimation(this, R.anim.slide_rtl);
    }
}
