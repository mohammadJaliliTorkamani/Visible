package dev.aban.visible.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.flurry.android.FlurryAgent;
import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.DownloadListener;
import com.ixuea.android.downloader.callback.DownloadManager;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.android.downloader.exception.DownloadException;
import com.microsoft.appcenter.analytics.Analytics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dev.aban.visible.R;
import dev.aban.visible.listener.DownloadingListener;
import dev.aban.visible.listener.OnExecutePayment;
import dev.aban.visible.model.BubbleItem;
import dev.aban.visible.model.DrawerItem;
import dev.aban.visible.model.SettingOption;
import dev.aban.visible.model.WeightInfo;
import dev.aban.visible.repository.network.ClientApi;
import dev.aban.visible.repository.network.ServiceGenerator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class Helper {
    private static SharedPreferences preferences;
    private static int downloadedItemsOfaBubble = 0;

    public static void showToast(final Activity activity, final String text) {
        if (activity != null) {
            activity.runOnUiThread(() -> Toast.makeText(activity, text, Toast.LENGTH_SHORT).show());
        }
    }

    public static boolean isLogin() {
        String status = loadSetting(Constants._TABLE_USER, Constants._KEY_LOGIN_STATE, null);
        return status != null && status.equals("true");
    }

    public static void saveSetting(@NonNull String table, @NonNull String key, String value) {
        preferences = ContextHelper.retrieveContext().getSharedPreferences(table, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
        editor.apply();
    }

    public static String loadSetting(@NonNull String table, @NonNull String key, String defaultValue) {
        preferences = ContextHelper.retrieveContext().getSharedPreferences(table, MODE_PRIVATE);
        return preferences.getString(key, defaultValue);
    }

    public static void simpleAddFragment(FragmentManager fragmentManager, Fragment fragment) {
        fragmentManager
                .beginTransaction()
                .add(R.id.activity_main_container, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    public static List<DrawerItem> getDrawerItems() {
        List<DrawerItem> list = new LinkedList<>();
        list.add(new DrawerItem(0, R.string.bubble_menu, R.drawable.ic_markets));
        list.add(new DrawerItem(1, R.string.setting, R.drawable.ic_setting_gear));
        list.add(new DrawerItem(2, R.string.donate_us, R.drawable.ic_heart));
        list.add(new DrawerItem(3, R.string.about_us, R.drawable.ic_about_us));
        list.add(new DrawerItem(4, R.string.more_apps, R.drawable.ic_app));
        list.add(new DrawerItem(5, R.string.share_app, R.drawable.ic_sharee));
        return list;
    }

    public static void initializeBackListener(Fragment fragment, ImageView imageView) {
        imageView.setOnClickListener(v -> fragment.getActivity().onBackPressed());
    }

    public static String getLocalPriceUnit() {
        return Constants.PRICE_UNIT;
    }

    /**
     * includes purchase + save into server
     *
     * @param price
     * @param bubbleID
     * @param afterPurchase
     */
    public static void bubblePurchase(float price, int bubbleID, OnExecutePayment afterPurchase) {
        if (price > 0) {
            // TODO: 2/27/20  implement following statements in onSuccess of payment  and suppose moch ITN is '1234dd'.... DO NOT FORGET TO USE ONEXECUTEPAYMENT AGAIN....
        }

        String ITN = "1234566";
        ServiceGenerator.getInstance().createService(ClientApi.class).saveITN(ITN, bubbleID, price).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    afterPurchase.onSuccessPayment(ITN);
                } else {
                    afterPurchase.onFailedPayment("error in saving purchase item : " + ITN);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                afterPurchase.onFailedPayment("error in saving purchase item : " + ITN + " ," + t.getMessage());
            }
        });
    }

    /**
     * includes purchase + save into server
     *
     * @param selectedPrice
     * @param afterPurchase
     */
    public static void donatePurchase(float selectedPrice, OnExecutePayment afterPurchase) {
        // TODO: 2/28/20   do payment operation and save it in server and then execute @onExecutePayment.... DO NOT FORGET TO USE ONEXECUTEPAYMENT AGAIN....
        String ITN = "1234566";
        ServiceGenerator.getInstance().createService(ClientApi.class)
                .saveDonationPayment(ITN, selectedPrice).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    afterPurchase.onSuccessPayment(ITN);
                } else {
                    afterPurchase.onFailedPayment("error in saving purchase item : " + ITN);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                afterPurchase.onFailedPayment("error in saving donate purchase item saving : " + ITN + " , " + t.getMessage());
            }
        });
    }

    public static List<SettingOption> getAvailableLanguages() {
        List<SettingOption> list = new LinkedList<>();
        list.add(new SettingOption("English", "en"));
        list.add(new SettingOption("فارسی", "fa"));
        return list;
    }

    public static void logout() {
        Helper.saveSetting(Constants._TABLE_USER, Constants._KEY_LOGIN_STATE, "false");
        Helper.saveSetting(Constants._TABLE_PROFILE, Constants._KEY_TOKEN, null);
        Intent i = ContextHelper.retrieveContext().getPackageManager().
                getLaunchIntentForPackage(ContextHelper.retrieveContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextHelper.retrieveContext().startActivity(i);
    }

    public static String getToken() {
        return loadSetting(Constants._TABLE_PROFILE, Constants._KEY_TOKEN, null);
    }

    public static String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static Bitmap uriTiBitmap(Uri uri) throws Exception {
        return MediaStore.Images.Media.getBitmap(ContextHelper.retrieveContext().getContentResolver(), uri);
    }

    /*
     *Returns true if the device supports the required hardware level, or better.
     * */

    public static boolean isHardwareLevelSupported(
            CameraCharacteristics characteristics, int requiredLevel) {
        int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            return requiredLevel == deviceLevel;
        }
        // deviceLevel is not LEGACY, can use numerical sort
        return requiredLevel <= deviceLevel;
    }

    public static int getScreenOrientation(Activity activity) {
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }

    public static void showToast(final Activity activity, @StringRes final int text) {
        if (activity != null) {
            activity.runOnUiThread(() -> Toast.makeText(activity, text, Toast.LENGTH_SHORT).show());
        }
    }

    public static void downloadItem(BubbleItem item, Constants.DownloadMode downloadMode, DownloadingListener downloadingListener, Runnable onAfterStore) {
        downloadedItemsOfaBubble = 0;
        final DownloadManager downloadManager = DownloadService.getDownloadManager(ContextHelper.retrieveContext().getApplicationContext());

        if (downloadMode == Constants.DownloadMode.BOTH_FILES || downloadMode == Constants.DownloadMode.JUST_FREEZE) {
            ServiceGenerator.getInstance().createService(ClientApi.class).getWeightInfos(item.getId()).enqueue(new Callback<WeightInfo>() {
                @Override
                public void onResponse(Call<WeightInfo> call, Response<WeightInfo> response) {
                    if (response.body() != null) {
                        String LABEL_url = response.body().getLabelPath();
                        final DownloadInfo LABEL_downloadInfo = new DownloadInfo.Builder().setUrl(Constants.BASE_FILES_URL + LABEL_url)
                                .setPath(Environment.getExternalStorageDirectory().getPath() + File.separator + Constants.APP_DIRECTORY_NAME + File.separator + item.getId() + Constants.LABEL_FILE_SUFFIX)
                                .build();
                        LABEL_downloadInfo.setDownloadListener(new DownloadListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onWaited() {

                            }

                            @Override
                            public void onPaused() {

                            }

                            @Override
                            public void onDownloading(long progress, long size) {
                                downloadingListener.OnDownloading(1.0f * progress / size);
                            }

                            @Override
                            public void onRemoved() {

                            }

                            @Override
                            public void onDownloadSuccess() {
                                downloadedItemsOfaBubble++;
                                if ((downloadMode == Constants.DownloadMode.BOTH_FILES && downloadedItemsOfaBubble == 2) || (downloadMode == Constants.DownloadMode.JUST_FREEZE))
                                    onAfterStore.run();
                            }

                            @Override
                            public void onDownloadFailed(DownloadException e) {
                                Log.d(Constants.TAG, e.getMessage());
                            }
                        });
                        downloadManager.download(LABEL_downloadInfo);
                    }
                }

                @Override
                public void onFailure(Call<WeightInfo> call, Throwable t) {
                    Log.d(Constants.TAG, t.getMessage());
                }
            });
        }

        if (downloadMode == Constants.DownloadMode.BOTH_FILES || downloadMode == Constants.DownloadMode.JUST_LABELS) {
            ServiceGenerator.getInstance().createService(ClientApi.class).getWeightInfos(item.getId()).enqueue(new Callback<WeightInfo>() {
                @Override
                public void onResponse(Call<WeightInfo> call, Response<WeightInfo> response) {
                    if (response.body() != null) {
                        String PB_url = response.body().getWeightPath();
                        final DownloadInfo PB_downloadInfo = new DownloadInfo.Builder().setUrl(Constants.BASE_FILES_URL + PB_url)
                                .setPath(Environment.getExternalStorageDirectory().getPath() + File.separator + Constants.APP_DIRECTORY_NAME + File.separator + item.getId() + Constants.WEIGHT_FILE_SUFFIX)
                                .build();
                        PB_downloadInfo.setDownloadListener(new DownloadListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onWaited() {

                            }

                            @Override
                            public void onPaused() {

                            }

                            @Override
                            public void onDownloading(long progress, long size) {
                                downloadingListener.OnDownloading(1f * progress / size);
                            }

                            @Override
                            public void onRemoved() {

                            }

                            @Override
                            public void onDownloadSuccess() {
                                downloadedItemsOfaBubble++;
                                if ((downloadMode == Constants.DownloadMode.BOTH_FILES && downloadedItemsOfaBubble == 2) || (downloadMode == Constants.DownloadMode.JUST_LABELS))
                                    onAfterStore.run();
                            }

                            @Override
                            public void onDownloadFailed(DownloadException e) {
                                Log.d(Constants.TAG, e.getMessage());
                            }
                        });
                        downloadManager.download(PB_downloadInfo);
                    }
                }

                @Override
                public void onFailure(Call<WeightInfo> call, Throwable t) {
                    Log.d(Constants.TAG, t.getMessage());
                }
            });
        }
    }

    public static String readAssetFile(String fileName) {
        StringBuilder toReturn = new StringBuilder();
        try {
            InputStream is = ContextHelper.retrieveContext().getAssets().open("licenses/" + fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String text = new String(buffer);
            toReturn.append(text);
        } catch (IOException e) {
            Log.d(Constants.TAG, e.getMessage());
        }
        return toReturn.toString();
    }

    public static boolean bubbleExists(String name) {
        //if folder was not created, create and returrn false

        File f = new File(Environment.getExternalStorageDirectory() + "/visible/");
        if (!f.isDirectory()) {
            Log.d(Constants.TAG, "visible folder created !");
            Log.d(Constants.TAG, "Existence of " + name + " is " + false);
            Log.d(Constants.TAG, f.mkdirs() + " is result of craetion");
            return false;
        }

        //check file existence in it and return result
        File extStore = Environment.getExternalStorageDirectory();
        File myFile = new File(extStore.getAbsolutePath() + "/" + "visible/" + name);
        Log.d(Constants.TAG, "Existence of " + name + " is " + myFile.exists());
        return myFile.exists();
    }

    public static int getAntiCameraType(int cameraType) {
        return cameraType == CameraCharacteristics.LENS_FACING_BACK ? CameraCharacteristics.LENS_FACING_FRONT : CameraCharacteristics.LENS_FACING_BACK;
    }

    public static boolean deleteIllegalBubbleFile(BubbleItem bubbleItem) {
        String weightFileName = bubbleItem.getId() + Constants.WEIGHT_FILE_SUFFIX;
        String labelFileName = bubbleItem.getId() + Constants.LABEL_FILE_SUFFIX;
        String externalStoragePath = Environment.getExternalStorageDirectory().getPath();

        File file1 = new File(externalStoragePath + File.separator + Constants.APP_DIRECTORY_NAME + File.separator + weightFileName);
        File file2 = new File(externalStoragePath + File.separator + Constants.APP_DIRECTORY_NAME + File.separator + labelFileName);

        boolean b1 = true, b2 = true;
        if (file1.exists())
            b1 = file1.delete();
        if (file2.exists())
            b2 = file2.delete();

        return b1 && b2;
    }

    public static void shareApp() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, ContextHelper.retrieveContext().getString(R.string.share_app_prefix) +
                    "market://details?id=" + ContextHelper.retrieveContext().getPackageName());
            sendIntent.setType("text/plain");
            ContextHelper.retrieveContext().startActivity(sendIntent);
        } catch (Exception e) {
            Log.d(Constants.TAG, "Error");
        }
    }

    public static void recordEventClick(String containerPage, String clickedViewName) {
        Map<String, String> map = new HashMap<>();
        map.put(containerPage, clickedViewName + " clicked");
        FlurryAgent.logEvent("Click event", map);
        Analytics.trackEvent(clickedViewName + " clicked in " + containerPage);
    }

    public static void recordEventView(String pageName) {
        FlurryAgent.logEvent(pageName + " viewed");
        Analytics.trackEvent(pageName + " viewed");
    }

    public static void getProVersion() {
        final String appPackageName = ContextHelper.retrieveContext().getPackageName();
        try {
            ContextHelper.retrieveContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            ContextHelper.retrieveContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void emailToDeveloper(Activity activity) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"customers@aban.dev"});
        i.putExtra(Intent.EXTRA_SUBJECT, ContextHelper.retrieveContext().getString(R.string.contact_lexin));
        i.putExtra(Intent.EXTRA_TEXT, "");
        try {
            ContextHelper.retrieveContext().startActivity(Intent.createChooser(i, ContextHelper.retrieveContext().getString(R.string.send_via)));
        } catch (android.content.ActivityNotFoundException ex) {
            showToast(activity, R.string.install_email_client);
        }
    }

    public static boolean isCorrectInput(Constants.InputType inputType, EditText editText) {
        if (editText.getText() == null || editText.getText().length() == 0)
            return false;

        String value = editText.getText().toString();

        switch (inputType) {
            case PASSWORD:
                return value.length() > 7;
            case PHONE:
                return value.length() == Constants.PHONE_NMBER_LENGTH && value.startsWith(Constants.PHONE_NMBER_PREFIX);
            case YEAR:
                return Integer.parseInt(value) > 0 && Integer.parseInt(value) >= 1330;

            case MONTH:
                return Integer.parseInt(value) > 0 && Integer.parseInt(value) <= 12;
            case DAY:
                return Integer.parseInt(value) > 0 && Integer.parseInt(value) <= 31;
            case OTP:
                return value.length() == 4;
            case USERNAME:
            case FULL_NAME:
                return true;
        }
        throw new InputMismatchException();
    }

    public static Bitmap getBitmapFromFilePath(String path) {
        try {
            Bitmap bitmap = null;
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri bitmapToURI(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ContextHelper.retrieveContext().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}