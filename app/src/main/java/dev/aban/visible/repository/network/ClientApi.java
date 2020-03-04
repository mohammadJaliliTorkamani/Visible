package dev.aban.visible.repository.network;

import java.util.List;
import java.util.Map;

import dev.aban.visible.model.BubbleItem;
import dev.aban.visible.model.DonateItem;
import dev.aban.visible.model.LoginResponse;
import dev.aban.visible.model.MoreApp;
import dev.aban.visible.model.NagScreen;
import dev.aban.visible.model.RegisterResponse;
import dev.aban.visible.model.SMSResult;
import dev.aban.visible.model.User;
import dev.aban.visible.model.WeightInfo;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface ClientApi {

    @GET("login.php")
    Call<LoginResponse> login(@Query("username") String username, @Query("password") String password);

    @Multipart
    @POST("register.php")
    Call<RegisterResponse> register(@Part MultipartBody.Part picture, @PartMap Map<String, RequestBody> params);

    @GET("drawer.php")
    Call<User> getProfilePictures();

    @GET("donate_items.php")
    Call<List<DonateItem>> getDonateList();

    @GET("market_sell_items.php")
    Call<List<BubbleItem>> getMarketItems();

    @GET("market_current_items.php")
    Call<List<BubbleItem>> getCurrentItems();

    @POST("send_sms_code.php")
    @FormUrlEncoded
    Call<SMSResult> sendSMSCode(@Field("phone") String phone);

    @GET("user_profile.php")
    Call<User> getUserProfile();

    @FormUrlEncoded
    @POST("update.php")
    Call<RequestBody> updateProfile(@Field("name") String name,
                                    @Field("bd_y") int year,
                                    @Field("bd_m") int month,
                                    @Field("bd_d") int day
    );

    @GET("pb_weight_url.php")
    Call<WeightInfo> getWeightInfos(@Query("bubble_id") int id);

    @POST("save_ITN.php")
    @FormUrlEncoded
    Call<ResponseBody> saveITN(@Field("itn") String itn, @Field("bubble_id") int bubbleID, float price);

    @GET("bubble.php")
    Call<BubbleItem> getBubbleInfo(@Query("bubble_id") int bubbleID);

    @GET("save_donate.php")
    Call<ResponseBody> saveDonationPayment(@Query("itn") String itn, @Query("selected_price") float selectedPrice);

    @GET("nag_screen_fa.php")
    Call<NagScreen> getNagScreen();

    @GET("more_apps_fa.php")
    Call<List<MoreApp>> getMoreAppList();

    @GET("is_user_exists.php")
    Call<RegisterResponse> isUserExists(@Query("phone") String phone, @Query("username") String username);
}