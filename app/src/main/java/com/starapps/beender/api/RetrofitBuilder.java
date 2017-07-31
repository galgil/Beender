package com.starapps.beender.api;

import com.starapps.beender.BuildConfig;
import com.starapps.beender.api.foursqaure.FoursquareApi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public final class RetrofitBuilder {

    private RetrofitBuilder (){}

    private static Retrofit retrofit;

    private static Retrofit getRetrofitBuilder(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(FoursquareApi.BASE_URL)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static FoursquareApi api = getRetrofitBuilder().create(FoursquareApi.class);

    public static FoursquareApi getApi() {
        return api;
    }

    private static OkHttpClient getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        else interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor).build();
    }
}
