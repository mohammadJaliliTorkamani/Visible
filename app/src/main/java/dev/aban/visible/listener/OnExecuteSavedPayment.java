package dev.aban.visible.listener;

public interface OnExecuteSavedPayment {
    void onSuccessPayment(String ITN);

    void onFailedPayment(String error);
}