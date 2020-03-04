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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import dev.aban.visible.R;
import dev.aban.visible.model.RegisterResponse;
import dev.aban.visible.repository.network.ClientApi;
import dev.aban.visible.repository.network.ServiceGenerator;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.utils.custom_view.EditTextPlus;
import dev.aban.visible.utils.custom_view.TextViewPlus;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpCodeVerificationFragment extends Fragment implements View.OnClickListener {
    private View view;

    private ImageView back;
    private EditTextPlus code;
    private CoordinatorLayout enterContainer;
    private TextViewPlus enterText;
    private ProgressBar enterPB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_otp_code_verification, container, false);

        findViews();
        initialize();
        manageListeners();
        startAnimations();
        return view;
    }

    private void enterHandler() {
        if (Helper.isCorrectInput(Constants.InputType.OTP, code)) {
            if (getArguments() == null)
                return;

            boolean _isMale = getArguments().getBoolean("is_male");
            String _fullName = getArguments().getString("full_name");
            String _username = getArguments().getString("username");
            String _password = getArguments().getString("password");
            String _day = getArguments().getString("day");
            String _month = getArguments().getString("month");
            String _year = getArguments().getString("year");
            String _phone = getArguments().getString("phone");
            String _imageFilePath = getArguments().getString("image");

            if (_fullName != null && _username != null && _password != null &&
                    _day != null && _month != null && _year != null &&
                    _phone != null && _imageFilePath != null) {
                displayEnter(false);
                RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), new File(_imageFilePath));
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", _username + ".jpg", requestBody);
                Map<String, RequestBody> params = new HashMap<>();
                params.put("code", RequestBody.create(MediaType.get("text/txt"), code.getText().toString()));
                params.put("full_name", RequestBody.create(MediaType.get("text/txt"), _fullName));
                params.put("username", RequestBody.create(MediaType.get("text/txt"), _username));
                params.put("password", RequestBody.create(MediaType.get("text/txt"), _password));
                params.put("phone", RequestBody.create(MediaType.get("text/txt"), _phone));
                params.put("day", RequestBody.create(MediaType.get("text/txt"), _day));
                params.put("month", RequestBody.create(MediaType.get("text/txt"), _month));
                params.put("year", RequestBody.create(MediaType.get("text/txt"), _year));
                params.put("is_male", RequestBody.create(MediaType.get("text/txt"), String.valueOf(_isMale)));

                ServiceGenerator.getInstance().createService(ClientApi.class).register(fileToUpload, params)
                        .enqueue(new Callback<RegisterResponse>() {
                            @Override
                            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                                if (response.body() != null) {
                                    if (response.body().isRegistered()) {
                                        Helper.saveSetting(Constants._TABLE_USER, Constants._KEY_LOGIN_STATE, "true");
                                        Helper.saveSetting(Constants._TABLE_PROFILE, Constants._KEY_TOKEN, response.body().getToken());
                                        getFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.activity_main_container, new MainPageFragment())
                                                .commit();
                                    } else {
                                        Helper.showToast(getActivity(), "user exists ");
                                        displayEnter(true);
                                    }
                                } else {
                                    Log.d(Constants.TAG, "error in complete register");
                                    displayEnter(true);
                                }
                            }

                            @Override
                            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                                Log.d(Constants.TAG, "Error while register the user, " + t.getMessage());
                                displayEnter(true);
                            }
                        });
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_otp_code_verification_back:
                backHandler();
                break;
            case R.id.fragment_otp_code_verification_enter_container:
                enterHandler();
                break;
        }
    }

    private void findViews() {
        back = view.findViewById(R.id.fragment_otp_code_verification_back);
        code = view.findViewById(R.id.fragment_otp_code_verification_et);
        enterContainer = view.findViewById(R.id.fragment_otp_code_verification_enter_container);
        enterPB = view.findViewById(R.id.fragment_otp_code_verification_enter_pb);
        enterText = view.findViewById(R.id.fragment_otp_code_verification_enter_text);
    }

    private void initialize() {
        displayEnter(true);
    }

    private void startAnimations() {
        code.startAnimation(Constants.LTR_ANIMATION);
        enterContainer.startAnimation(Constants.RTL_ANIMATION);
    }

    private void manageListeners() {
        back.setOnClickListener(this);
        enterContainer.setOnClickListener(this);
    }

    private void backHandler() {
        getActivity().onBackPressed();
    }

    private void displayEnter(boolean display) {
        enterContainer.setClickable(display);
        enterText.setVisibility(display ? View.VISIBLE : View.GONE);
        enterPB.setVisibility(!display ? View.VISIBLE : View.GONE);
    }
}
