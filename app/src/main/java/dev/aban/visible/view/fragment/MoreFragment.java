package dev.aban.visible.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import dev.aban.visible.R;
import dev.aban.visible.adapter.MoreAppsAdapter;
import dev.aban.visible.model.MoreApp;
import dev.aban.visible.repository.network.ClientApi;
import dev.aban.visible.repository.network.ServiceGenerator;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.utils.Helper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoreFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<MoreApp> list = new LinkedList<>();
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_more, container, false);
        findViews();
        initialize();
        return view;
    }

    private void findViews() {
        recyclerView = view.findViewById(R.id.more_apps_rv);
        progressBar = view.findViewById(R.id.more_apps_pb);
        constraintLayout = view.findViewById(R.id.more_apps_view);
    }

    private void initialize() {
        Helper.recordEventView("Fragment_More");
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(ContextHelper.retrieveContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MoreAppsAdapter(list);
        recyclerView.setAdapter(adapter);

        ServiceGenerator.getInstance().createService(ClientApi.class).getMoreAppList().enqueue(new Callback<List<MoreApp>>() {
            @Override
            public void onResponse(Call<List<MoreApp>> call, Response<List<MoreApp>> response) {
                if (response.body() != null) {
                    constraintLayout.setBackgroundColor(Color.parseColor(response.body().get(0).getContainerColor()));
                    list.clear();
                    list.addAll(response.body());
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(Constants.TAG, "null");
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<MoreApp>> call, Throwable t) {
                Log.d(Constants.TAG, t.getMessage());
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }
}


