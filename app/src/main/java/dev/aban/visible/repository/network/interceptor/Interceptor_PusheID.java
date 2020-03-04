package dev.aban.visible.repository.network.interceptor;

import android.content.Context;

import com.pushpole.sdk.PushPole;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class Interceptor_PusheID implements Interceptor {
    private static Interceptor_PusheID instance;
    private Context context;

    private Interceptor_PusheID(Context context) {
        this.context = context;
    }

    public static Interceptor_PusheID getInstance(Context context) {
        if (instance == null)
            instance = new Interceptor_PusheID(context);
        return instance;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String pusheId = PushPole.getId(context);
        Request request = chain.request();
        request = request.newBuilder().header("Pushe", pusheId).build();
        return chain.proceed(request);
    }
}
