package dev.aban.visible.repository.network.interceptor;

import android.content.Context;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.ContextHelper;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author mohammad
 * @version : 10
 * @since 29 JAN 2020
 */
public class OfflineCacheInterceptor implements Interceptor {
    private static OfflineCacheInterceptor instance;

    private OfflineCacheInterceptor() {
    }

    public static OfflineCacheInterceptor getInstance() {
        if (instance == null)
            instance = new OfflineCacheInterceptor();
        return instance;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (!isConnected()) {
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxStale(2, TimeUnit.DAYS)
                    .build();

            request = request.newBuilder()
                    .removeHeader(Constants.REQUEST_HEADER_PRAGMA)
                    .removeHeader(Constants.REQUEST_HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build();
        }

        return chain.proceed(request);
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
