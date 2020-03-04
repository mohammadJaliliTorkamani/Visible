package dev.aban.visible.repository.network.interceptor;

import android.content.Context;

import java.io.IOException;

import dev.aban.visible.utils.Helper;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class Interceptor_Token implements Interceptor {
    private static Interceptor_Token instance;
    private Context context;

    private Interceptor_Token(Context context) {
        this.context = context;
    }

    public static Interceptor_Token getInstance(Context context) {
        if (instance == null)
            instance = new Interceptor_Token(context);
        return instance;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = Helper.getToken();
        Request request = chain.request();
        if (token != null)
            request = request.newBuilder().header("token", token).build();
        return chain.proceed(request);
    }
}
