package dev.aban.visible.listener;

public interface OnExecutePayment {
    void onSuccessPayment(String ITN);

    void onFailedPayment(String error);
}