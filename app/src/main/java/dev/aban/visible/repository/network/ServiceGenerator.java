package dev.aban.visible.repository.network;


import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import dev.aban.visible.repository.network.interceptor.CustomCacheInterceptor;
import dev.aban.visible.repository.network.interceptor.Interceptor_PusheID;
import dev.aban.visible.repository.network.interceptor.Interceptor_Token;
import dev.aban.visible.repository.network.interceptor.Interceptor_VersionCode;
import dev.aban.visible.repository.network.interceptor.OfflineCacheInterceptor;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.ContextHelper;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static Retrofit mRetrofit;
    private static OkHttpClient mOkHttpClient;
    private static ServiceGenerator instance;


    private ServiceGenerator() {
    }

    public static ServiceGenerator getInstance() {
        if (instance == null)
            instance = new ServiceGenerator();
        return instance;
    }

    public <S> S createService(Class<S> serviceClass) {
        if (mRetrofit == null) {
            File httpCacheDirectory = new File(ContextHelper.retrieveContext().getCacheDir(), "responses");
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(Constants.CONNECT_TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(Constants.READ_TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(Constants.WRITE_TIME_OUT, TimeUnit.SECONDS)
                    .addInterceptor(OfflineCacheInterceptor.getInstance())
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(Interceptor_Token.getInstance(ContextHelper.retrieveContext()))
                    .addInterceptor(Interceptor_VersionCode.getInstance(ContextHelper.retrieveContext()))
                    .addInterceptor(Interceptor_PusheID.getInstance(ContextHelper.retrieveContext()))
                    .addNetworkInterceptor(CustomCacheInterceptor.getInstance())
                    .cache(new Cache(httpCacheDirectory, Constants.CACHE_SIZE));

            mOkHttpClient = httpClient.build();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_REQUESTS_URL)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().disableHtmlEscaping().create()))
                    .client(mOkHttpClient)
                    .build();
        }
        return mRetrofit.create(serviceClass);
    }
}
