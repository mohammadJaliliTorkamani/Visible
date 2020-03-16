package dev.aban.visible.utils;

import android.app.Activity;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import dev.aban.visible.BuildConfig;
import dev.aban.visible.listener.OnExecuteSavedPayment;
import dev.aban.visible.model.BubbleItem;
import dev.aban.visible.repository.network.ClientApi;
import dev.aban.visible.repository.network.ServiceGenerator;
import dev.aban.visible.util.IabHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BazaarPurchase {
    private static BazaarPurchase instance;
    private IabHelper mHelper;

    private BazaarPurchase() {
        mHelper = new IabHelper(ContextHelper.retrieveContext(), BuildConfig.AIDL_KEY);
        mHelper.enableDebugLogging(true);// TODO: 3/5/20 set it to false before release
    }

    public static BazaarPurchase getInstance() {
        if (instance == null)
            instance = new BazaarPurchase();
        return instance;
    }

    public void purchaseBubbleItem(Activity activity, int bubbleID, OnExecuteSavedPayment onExecuteSavedPayment) {
        try {
            ServiceGenerator.getInstance().createService(ClientApi.class).getBubbleSKU(bubbleID).enqueue(new Callback<BubbleItem>() {
                @Override
                public void onResponse(Call<BubbleItem> call, Response<BubbleItem> response) {
                    if (response.body() != null) {
                        String sku = response.body().getSku();
                        Log.d(Constants.TAG, "SKU RECIVED, " + sku);


                        List<String> list = new LinkedList<>();
                        list.add(sku);
                        BazaarPurchase.getInstance().getHelper().queryInventoryAsync((result0, inv) -> {
                            if (inv.hasPurchase(sku)) {
                                onExecuteSavedPayment.onSuccessPayment(inv.getPurchase(sku).getOrderId());
                            } else {
                                try {
                                    mHelper.launchPurchaseFlow(activity, sku, bubbleID, (result, purchaseInfo) -> {
                                        if (result.isSuccess()) {
                                            ServiceGenerator.getInstance().createService(ClientApi.class)
                                                    .saveBubblePurchaseInfo(bubbleID, purchaseInfo.getPurchaseTime(), purchaseInfo.getToken(), purchaseInfo.getSignature(), purchaseInfo.getOriginalJson(), purchaseInfo.getOrderId()).enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if (response.body() != null) {
                                                        onExecuteSavedPayment.onSuccessPayment(purchaseInfo.getOrderId());
                                                    } else {
                                                        onExecuteSavedPayment.onFailedPayment("error in saving purchase item : " + purchaseInfo.getOrderId());
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    onExecuteSavedPayment.onFailedPayment("error in saving bubble purchase item saving : " + purchaseInfo.getOrderId() + " , " + t.getMessage());
                                                }
                                            });

                                        } else {
                                            onExecuteSavedPayment.onFailedPayment("payment failure, " + result.getResponse() + ", " + result.getMessage());
                                        }
                                    });
                                } catch (IllegalStateException e) {
                                    Log.d(Constants.TAG, e.getMessage());
                                    Helper.showToast(activity, "خطا در عملیات پرداخت");
                                }
                            }
                        });

                    } else {
                        onExecuteSavedPayment.onFailedPayment("null bubble SKU");
                    }
                }

                @Override
                public void onFailure(Call<BubbleItem> call, Throwable t) {
                    onExecuteSavedPayment.onFailedPayment("Error while get bubble SKU, " + t.getMessage());
                }
            });
        } catch (Throwable e) {
            Log.d(Constants.TAG, e.getMessage());
        }

    }

    public void disposePurchase() {
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    public IabHelper getHelper() {
        if (mHelper == null) {
            mHelper = new IabHelper(ContextHelper.retrieveContext(), BuildConfig.AIDL_KEY);
            mHelper.enableDebugLogging(true);// TODO: 3/5/20 set it to false before release
        }
        return mHelper;
    }
}