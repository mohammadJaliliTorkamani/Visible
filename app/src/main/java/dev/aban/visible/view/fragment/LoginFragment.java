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
import dev.aban.visible.model.LoginResponse;
import dev.aban.visible.repository.network.ClientApi;
import dev.aban.visible.repository.network.ServiceGenerator;
import dev.aban.visible.utils.BazaarPurchase;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.utils.custom_view.EditTextPlus;
import dev.aban.visible.utils.custom_view.TextViewPlus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private View view;

    private ImageView icon;
    private EditTextPlus username;
    private EditTextPlus password;
    private CoordinatorLayout login;
    private ProgressBar loginPB;
    private TextViewPlus loginText;
    private TextViewPlus register;
    private TextViewPlus registerQuestion;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_login, container, false);
        findViews();
        initialize();
        manageListeners();
        startAnimations();
        return view;
    }

    private void startAnimations() {
        icon.startAnimation(Constants.NORMAL_SCALE_ANIMATION);
        icon.startAnimation(Constants.NORMAL_SCALE_ANIMATION);
        username.startAnimation(Constants.LTR_ANIMATION);
        password.startAnimation(Constants.RTL_ANIMATION);
        registerQuestion.startAnimation(Constants.SLIDE_UP_ANIMATION);
        register.startAnimation(Constants.SLIDE_UP_ANIMATION);
    }

    private void findViews() {
        icon = view.findViewById(R.id.fragment_login_icon);
        username = view.findViewById(R.id.fragment_login_username);
        password = view.findViewById(R.id.fragment_login_password);
        login = view.findViewById(R.id.fragment_login_btn_container);
        loginPB = view.findViewById(R.id.fragment_login_login_pb);
        loginText = view.findViewById(R.id.fragment_login_btn_text);
        register = view.findViewById(R.id.fragment_login_create_account);
        registerQuestion = view.findViewById(R.id.fragment_login_create_account_question);
    }

    private void initialize() {
        visibleLoginText(true);
    }

    private void manageListeners() {
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_login_btn_container:
                loginHandler();
                break;
            case R.id.fragment_login_create_account:
                createAccountHandler();
                break;

        }
    }

    private void createAccountHandler() {
        Helper.simpleAddFragment(getFragmentManager(), new RegisterFragment());
    }

    private void loginHandler() {
        if (Helper.isCorrectInput(Constants.InputType.USERNAME, username) && Helper.isCorrectInput(Constants.InputType.PASSWORD, password)) {
            visibleLoginText(false);

            String _username = username.getText().toString();
            String _password = password.getText().toString();
            ServiceGenerator.getInstance().createService(ClientApi.class).login(_username, _password).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.body() != null) {
                        if (response.body().isSuccessful()) {
                            Helper.saveSetting(Constants._TABLE_USER, Constants._KEY_LOGIN_STATE, "true");
                            Helper.saveSetting(Constants._TABLE_PROFILE, Constants._KEY_TOKEN, response.body().getToken());
                            try {
                                BazaarPurchase.getInstance().getHelper().startSetup(result -> {
                                    Log.d(Constants.TAG, "Setup finished.");
                                    if (result.isSuccess()) {
                                        getFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.activity_main_container, new MainPageFragment())
                                                .commitAllowingStateLoss();
                                    } else {
                                        Log.d(Constants.TAG, "Problem setting up In-app Billing: " + result);
                                        Helper.showToast(getActivity(), R.string.purchase_not_supported);
                                    }

                                });
                            } catch (Exception e) {
                                Log.d(Constants.TAG, e.getMessage());
                            }
                        } else {
                            visibleLoginText(true);
                            Helper.showToast(getActivity(), R.string.wrong_information);
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    visibleLoginText(true);
                }
            });
        } else {
            Helper.showToast(getActivity(), R.string.control_input_fields);
        }
    }

    private void visibleLoginText(boolean visible) {
        loginText.setVisibility(visible ? View.VISIBLE : View.GONE);
        loginPB.setVisibility(!visible ? View.VISIBLE : View.GONE);
    }
}
