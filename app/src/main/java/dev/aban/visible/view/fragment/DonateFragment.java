package dev.aban.visible.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import dev.aban.visible.R;
import dev.aban.visible.adapter.DonateAdapter;
import dev.aban.visible.listener.OnExecutePayment;
import dev.aban.visible.model.DonateItem;
import dev.aban.visible.repository.network.ClientApi;
import dev.aban.visible.repository.network.ServiceGenerator;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.utils.custom_view.TextViewPlus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonateFragment extends Fragment implements View.OnClickListener {
    private View view;

    private ImageView back;
    private TextViewPlus donate;
    private RecyclerView rv;
    private DonateAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<DonateItem> list = new LinkedList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null)
            view = inflater.inflate(R.layout.fragment_dontae, container, false);

        findViews();
        initialize();
        manageListeners();
        return view;
    }

    private void manageListeners() {
        Helper.initializeBackListener(this, back);
        donate.setOnClickListener(this);
    }

    private void initialize() {
        Helper.recordEventView("DonateFragment");
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(ContextHelper.retrieveContext(), RecyclerView.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        adapter = new DonateAdapter(list);
        rv.setAdapter(adapter);

        ServiceGenerator.getInstance().createService(ClientApi.class).getDonateList().enqueue(new Callback<List<DonateItem>>() {
            @Override
            public void onResponse(Call<List<DonateItem>> call, Response<List<DonateItem>> response) {
                if (response.body() != null) {
                    list.clear();
                    list.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else
                    Log.d(Constants.TAG, "error at donate list");
            }

            @Override
            public void onFailure(Call<List<DonateItem>> call, Throwable t) {
                Log.d(Constants.TAG, "Error while getting donate list, " + t.getMessage());
            }
        });
    }

    private void findViews() {
        back = view.findViewById(R.id.fragment_donate_back);
        rv = view.findViewById(R.id.fragment_donate_rv);
        donate = view.findViewById(R.id.fragment_donate_btn);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_donate_btn:
                donateHandle();
                break;
        }
    }

    private void donateHandle() {
        Helper.recordEventClick("DonateFragment", "Donate Button");
        Helper.donatePurchase(adapter.getSelectedPrice(), new OnExecutePayment() {
            @Override
            public void onSuccessPayment(String ITN) {
                Log.d(Constants.TAG, "donate save succeed");
            }

            @Override
            public void onFailedPayment(String error) {
                Log.d(Constants.TAG, "payment failure : " + error);
                Helper.showToast(getActivity(), "payment failure " + error);
            }
        });
    }
}
