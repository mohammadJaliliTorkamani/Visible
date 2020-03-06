package dev.aban.visible.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ixuea.android.downloader.DownloadService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.LinkedList;
import java.util.List;

import dev.aban.visible.R;
import dev.aban.visible.adapter.BubbleImageAdapter;
import dev.aban.visible.adapter.StoreCurrentBubbleAdapter;
import dev.aban.visible.adapter.StoreSellBubbleAdapter;
import dev.aban.visible.listener.OnExecuteSavedPayment;
import dev.aban.visible.model.BubbleItem;
import dev.aban.visible.repository.network.ClientApi;
import dev.aban.visible.repository.network.ServiceGenerator;
import dev.aban.visible.utils.BazaarPurchase;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.Constants.DownloadMode;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.utils.custom_view.TextViewPlus;
import info.abdolahi.CircularMusicProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketFragment extends Fragment {
    private View view;

    private List<BubbleItem> currentBubbles = new LinkedList<>();
    private List<BubbleItem> toSellBubbles = new LinkedList<>();

    private ImageView back;
    private TextViewPlus currentBubbles_rv_empty;
    private RecyclerView currentBubbles_rv;
    private RecyclerView.Adapter currentBubbles_adapter;
    private RecyclerView.LayoutManager currentBubbles_layout_manager;
    private RecyclerView sellBubbles_rv;
    private TextViewPlus sellBubbles_rv_empty;
    private RecyclerView.Adapter sellBubbles_adapter;
    private RecyclerView.LayoutManager sellBubbles_layout_manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_market, container, false);
        findViews();
        initialize();
        manageListeners();
        return view;
    }

    private void manageListeners() {
        Helper.initializeBackListener(this, back);
    }

    private void initialize() {
        Helper.recordEventView("MarketFragment");

        currentBubbles_layout_manager = new LinearLayoutManager(ContextHelper.retrieveContext(), RecyclerView.HORIZONTAL, false);
        sellBubbles_layout_manager = new GridLayoutManager(ContextHelper.retrieveContext(), 3, RecyclerView.VERTICAL, false);

        currentBubbles_rv.setHasFixedSize(true);
        sellBubbles_rv.setHasFixedSize(true);

        currentBubbles_rv.setLayoutManager(currentBubbles_layout_manager);
        sellBubbles_rv.setLayoutManager(sellBubbles_layout_manager);

        currentBubbles_adapter = new StoreCurrentBubbleAdapter(currentBubbles, item -> showItemInfoDialog(item, false));
        sellBubbles_adapter = new StoreSellBubbleAdapter(toSellBubbles, item -> showItemInfoDialog(item, true));

        currentBubbles_rv.setAdapter(currentBubbles_adapter);
        sellBubbles_rv.setAdapter(sellBubbles_adapter);

        ServiceGenerator.getInstance().createService(ClientApi.class).getCurrentItems().enqueue(new Callback<List<BubbleItem>>() {
            @Override
            public void onResponse(Call<List<BubbleItem>> call, Response<List<BubbleItem>> response) {
                if (response.body() != null) {
                    currentBubbles.clear();
                    currentBubbles.addAll(response.body());
                    currentBubbles_adapter.notifyDataSetChanged();
                    displayEmptyCurrentContent(currentBubbles.isEmpty());
                } else
                    Log.d(Constants.TAG, "error in getting current bubbles");
            }

            @Override
            public void onFailure(Call<List<BubbleItem>> call, Throwable t) {
                Log.d(Constants.TAG, "Error while getting bubble market sell items");
                displayEmptyCurrentContent(true);
            }
        });

        ServiceGenerator.getInstance().createService(ClientApi.class).getMarketItems().enqueue(new Callback<List<BubbleItem>>() {
            @Override
            public void onResponse(Call<List<BubbleItem>> call, Response<List<BubbleItem>> response) {
                if (response.body() != null) {
                    toSellBubbles.clear();
                    toSellBubbles.addAll(response.body());
                    sellBubbles_adapter.notifyDataSetChanged();
                    displayEmptySellContent(toSellBubbles.isEmpty());
                } else
                    Log.d(Constants.TAG, "error in getting sell bubbles");

            }

            @Override
            public void onFailure(Call<List<BubbleItem>> call, Throwable t) {
                Log.d(Constants.TAG, "Error while getting bubble market sell items");
                displayEmptySellContent(true);
            }
        });
    }

    private void showItemInfoDialog(BubbleItem item, boolean mustPurchaseFirst) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(ContextHelper.retrieveContext()).inflate(R.layout.dialog_bubble_info, null, false);
        List<String> images = new LinkedList<>();

        CircularMusicProgressBar logo = view.findViewById(R.id.dialog_bubble_info_logo);
        ProgressBar progressBar = view.findViewById(R.id.dialog_bubble_info_pb);
        ConstraintLayout constraintLayout = view.findViewById(R.id.dialog_bubble_info_container);
        TextViewPlus title = view.findViewById(R.id.dialog_bubble_info_title);
        TextViewPlus price = view.findViewById(R.id.dialog_bubble_info_price);
        TextViewPlus description = view.findViewById(R.id.dialog_bubble_info_description);
        TextViewPlus buy = view.findViewById(R.id.dialog_bubble_info_get);

        progressBar.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.GONE);
        RecyclerView recyclerView = view.findViewById(R.id.dialog_bubble_info_rv);

        RecyclerView.Adapter adapter = new BubbleImageAdapter(images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ContextHelper.retrieveContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        builder.setView(view);
        builder.setCancelable(true);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnDismissListener(dialog1 -> DownloadService.getDownloadManager(ContextHelper.retrieveContext().getApplicationContext()).getDownloadDBController().pauseAllDownloading());
        dialog.setOnShowListener(dialog1 -> {
            progressBar.setVisibility(View.VISIBLE);
            ServiceGenerator.getInstance().createService(ClientApi.class).getBubbleInfo(item.getId()).enqueue(new Callback<BubbleItem>() {
                @Override
                public void onResponse(Call<BubbleItem> call, Response<BubbleItem> response) {

                    if (response.body() != null) {
                        BubbleItem bubbleItem = response.body();

                        boolean freezeExists = Helper.bubbleExists(bubbleItem.getId() + Constants.WEIGHT_FILE_SUFFIX);
                        boolean labelsExists = Helper.bubbleExists(bubbleItem.getId() + Constants.LABEL_FILE_SUFFIX);

                        if (!freezeExists && !labelsExists) {
                            if (mustPurchaseFirst) {
                                buy.setVisibility(View.VISIBLE);
                                buy.setOnClickListener(v -> {
                                    buy.setClickable(false);
                                    dialog.dismiss();
                                    Helper.bubblePurchase(getActivity(), item.getId(), new OnExecuteSavedPayment() {
                                        @Override
                                        public void onSuccessPayment(String ITN) {
                                            Log.d(Constants.TAG, ITN);
                                            downloadItem(bubbleItem, logo, buy, DownloadMode.BOTH_FILES);
                                        }

                                        @Override
                                        public void onFailedPayment(String error) {
                                            Log.d(Constants.TAG, error);
                                            buy.setClickable(true);
                                        }
                                    });
                                });
                            } else {
                                buy.setVisibility(View.VISIBLE);
                                buy.setOnClickListener(v -> {
                                    buy.setClickable(false);
                                    downloadItem(bubbleItem, logo, buy, DownloadMode.BOTH_FILES);
                                });
                            }
                        } else if (!freezeExists && labelsExists) {
                            if (mustPurchaseFirst) {
                                Helper.deleteIllegalBubbleFile(bubbleItem);
                                buy.setVisibility(View.VISIBLE);
                                buy.setOnClickListener(v -> {
                                    buy.setClickable(false);
                                    downloadItem(bubbleItem, logo, buy, DownloadMode.BOTH_FILES);
                                });
                            } else {
                                buy.setVisibility(View.VISIBLE);
                                buy.setOnClickListener(v -> {
                                    buy.setClickable(false);
                                    downloadItem(bubbleItem, logo, buy, DownloadMode.JUST_FROZEN_WEIGHT);
                                });
                            }
                        } else if (freezeExists && !labelsExists) {
                            if (mustPurchaseFirst) {
                                buy.setVisibility(View.VISIBLE);
                                buy.setOnClickListener(v -> {
                                    buy.setClickable(false);
                                    downloadItem(bubbleItem, logo, buy, DownloadMode.BOTH_FILES);
                                });
                            } else {
                                buy.setVisibility(View.VISIBLE);
                                buy.setOnClickListener(v -> {
                                    buy.setClickable(false);
                                    downloadItem(bubbleItem, logo, buy, DownloadMode.JUST_LABELS);
                                });
                            }
                        } else {
                            if (mustPurchaseFirst) {
                                Helper.deleteIllegalBubbleFile(bubbleItem);
                                buy.setVisibility(View.VISIBLE);
                                buy.setOnClickListener(v -> {
                                    buy.setClickable(false);
                                    dialog.dismiss();
                                    Helper.bubblePurchase(getActivity(), item.getId(), new OnExecuteSavedPayment() {
                                        @Override
                                        public void onSuccessPayment(String ITN) {
                                            Log.d(Constants.TAG, ITN);
                                            downloadItem(bubbleItem, logo, buy, DownloadMode.BOTH_FILES);
                                        }

                                        @Override
                                        public void onFailedPayment(String error) {
                                            Log.d(Constants.TAG, error);
                                            buy.setClickable(false);
                                        }
                                    });
                                });
                            } else {
                                buy.setVisibility(View.GONE);
                            }
                        }


                        progressBar.setVisibility(View.GONE);
                        constraintLayout.setVisibility(View.VISIBLE);
                        title.setText(bubbleItem.getTitle());
                        price.setText(bubbleItem.getPrice() + " " + Helper.getLocalPriceUnit());
                        description.setText(bubbleItem.getDescription());
                        Picasso.get().load(bubbleItem.getImageURL()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                logo.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                Log.d(Constants.TAG, e.getMessage());
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

                        images.clear();
                        images.addAll(bubbleItem.getPictures());
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(Constants.TAG, "Error in bubble info!");
                        dialog1.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<BubbleItem> call, Throwable t) {
                    Log.d(Constants.TAG, t.getMessage());
                }
            });
        });

        dialog.show();
    }

    private void downloadItem(BubbleItem item, CircularMusicProgressBar logo, TextViewPlus buy, DownloadMode downloadMode) {
        Helper.recordEventClick("ProfileFragment", "Download Item " + item.getTitle());
        Helper.showToast(getActivity(), "دانلود شروع شد");
        Helper.downloadItem(item, downloadMode, (progressPercentage) -> {
            Log.d(Constants.TAG, "" + progressPercentage);
            logo.setValue(progressPercentage * 100);
        }, () -> {
            buy.setClickable(false);
            buy.setVisibility(View.GONE);
            logo.setValue(0);
            Helper.showToast(getActivity(), "حباب '" + item.getTitle() + "' دانلود شد !");
            item.setPermittedToUse(true);
            if (!currentBubbles.contains(item)) {
                currentBubbles.add(item);
                currentBubbles_adapter.notifyDataSetChanged();
                displayEmptyCurrentContent(currentBubbles.isEmpty());
            }

            if (toSellBubbles.contains(item)) {
                toSellBubbles.remove(item);
                sellBubbles_adapter.notifyDataSetChanged();
                displayEmptySellContent(toSellBubbles.isEmpty());
            }
        });
    }

    private void findViews() {
        back = view.findViewById(R.id.fragment_market_back);
        currentBubbles_rv = view.findViewById(R.id.fragment_market_current_bubbles_rv);
        currentBubbles_rv_empty = view.findViewById(R.id.fragment_market_current_bubbles_rv_empty);
        sellBubbles_rv = view.findViewById(R.id.fragment_market_sell_bubbles_rv);
        sellBubbles_rv_empty = view.findViewById(R.id.fragment_market_sell_bubbles_rv_empty);
    }

    private void displayEmptyCurrentContent(boolean displayEmpty) {
        currentBubbles_rv.setVisibility(!displayEmpty ? View.VISIBLE : View.GONE);
        currentBubbles_rv_empty.setVisibility(displayEmpty ? View.VISIBLE : View.GONE);
    }

    private void displayEmptySellContent(boolean displayEmpty) {
        sellBubbles_rv.setVisibility(!displayEmpty ? View.VISIBLE : View.GONE);
        sellBubbles_rv_empty.setVisibility(displayEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Constants.TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data + ")");
        // Pass on the activity result to the helper for handling
        if (!BazaarPurchase.getInstance().getHelper().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(Constants.TAG, "onActivityResult handled by IABUtil.");
        }
    }
}
