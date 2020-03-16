package dev.aban.visible.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import dev.aban.visible.R;
import dev.aban.visible.utils.Helper;

public class DonateFragment extends Fragment {
    private View view;

    private ImageView back;

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
    }

    private void initialize() {
    }

    private void findViews() {
        back = view.findViewById(R.id.fragment_donate_back);
    }
}
