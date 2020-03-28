package dev.aban.visible.view.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import dev.aban.visible.R;
import dev.aban.visible.model.PasswordRecoverResponse;
import dev.aban.visible.repository.network.ClientApi;
import dev.aban.visible.repository.network.ServiceGenerator;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.utils.custom_view.EditTextPlus;
import dev.aban.visible.utils.custom_view.TextViewPlus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoverPasswordFragment extends Fragment implements View.OnClickListener {
    private View view;

    private ImageView back;
    private EditTextPlus phone;
    private CoordinatorLayout recoverContainer;
    private TextViewPlus recoverText;
    private ProgressBar recoverPB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null)
            view = inflater.inflate(R.layout.fragment_recover_password, container, false);

        findViews();
        initialize();
        manageListeners();
        startAnimations();
        return view;
    }

    private void startAnimations() {
    }

    private void manageListeners() {
        Helper.initializeBackListener(this, back);
        recoverContainer.setOnClickListener(this);
    }

    private void initialize() {
        phone.startAnimation(Constants.LTR_ANIMATION);
        recoverContainer.startAnimation(Constants.RTL_ANIMATION);

    }

    private void findViews() {
        back = view.findViewById(R.id.fragment_recover_password_back);
        phone = view.findViewById(R.id.fragment_recover_password_et);
        recoverContainer = view.findViewById(R.id.fragment_recover_password_recover_container);
        recoverText = view.findViewById(R.id.fragment_recover_password_recover_text);
        recoverPB = view.findViewById(R.id.fragment_recover_password_recover_pb);
    }

    private void displayRecover(boolean display) {
        recoverContainer.setClickable(display);
        recoverText.setVisibility(display ? View.VISIBLE : View.GONE);
        recoverPB.setVisibility(!display ? View.VISIBLE : View.GONE);
    }

    private void recoverContainerHandler() {
        if (Helper.isCorrectInput(Constants.InputType.PHONE, phone)) {
            displayRecover(false);
            String _phone = phone.getText().toString();
            ServiceGenerator.getInstance().createService(ClientApi.class).recoverPassword(_phone)
                    .enqueue(new Callback<PasswordRecoverResponse>() {
                        @Override
                        public void onResponse(Call<PasswordRecoverResponse> call, Response<PasswordRecoverResponse> response) {
                            if (response.body() != null) {
                                if (response.body().isSent()) {
                                    Helper.showToast(getActivity(), "Username and password successfully sent to phone");
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.activity_main_container, new LoginFragment())
                                            .commit();
                                } else {
                                    Helper.showToast(getActivity(), response.body().getMessage());
                                    displayRecover(true);
                                }
                            } else {
                                Log.d(Constants.TAG, "error in recover password");
                                displayRecover(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<PasswordRecoverResponse> call, Throwable t) {
                            Log.d(Constants.TAG, "Error while recover password, " + t.getMessage());
                            displayRecover(true);
                        }
                    });
        } else
            Helper.showToast(getActivity(), "phone is not valid");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_recover_password_recover_container:
                recoverContainerHandler();
        }
    }
}
