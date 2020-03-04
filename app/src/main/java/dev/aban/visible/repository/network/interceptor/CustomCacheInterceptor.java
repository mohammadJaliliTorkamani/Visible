package dev.aban.visible.repository.network.interceptor;

import android.content.Context;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.ContextHelper;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author mohammad
 * @version : 10
 * @since 29 JAN 2020
 */
public class CustomCacheInterceptor implements Interceptor {
    private static CustomCacheInterceptor instance;


    public static CustomCacheInterceptor getInstance() {
        if (instance == null)
            instance = new CustomCacheInterceptor();
        return instance;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (isConnected()) {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=" + Constants.CACHE_MAX_AGE)
                    .build();
        } else {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + Constants.CACHE_MAX_STALE)
                    .build();
        }
    }

    private boolean isConnected() {
        try {
            android.net.ConnectivityManager e = (android.net.ConnectivityManager)
                    ContextHelper.retrieveContext().getSystemService(
                            Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = e.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.d(Constants.TAG, e.getMessage());
        }

        return false;
    }
}
