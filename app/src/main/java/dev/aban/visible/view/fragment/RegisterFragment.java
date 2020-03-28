package dev.aban.visible.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.aban.visible.R;
import dev.aban.visible.model.RegisterResponse;
import dev.aban.visible.model.SMSResult;
import dev.aban.visible.repository.network.ClientApi;
import dev.aban.visible.repository.network.ServiceGenerator;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.utils.custom_view.EditTextPlus;
import dev.aban.visible.utils.custom_view.TextViewPlus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class RegisterFragment extends Fragment implements View.OnClickListener {
    private View view;

    private EditTextPlus fullName;
    private EditTextPlus username;
    private EditTextPlus password;
    private TextViewPlus login;
    private TextViewPlus loginQuestion;
    private CoordinatorLayout signup;
    private TextViewPlus signupText;
    private ProgressBar signupPB;
    private TextViewPlus dateOfBirth;
    private TextViewPlus uploadImageText;
    private EditTextPlus day;
    private EditTextPlus month;
    private EditTextPlus year;
    private TextViewPlus genderText;
    private ConstraintLayout genderContainer;
    private ImageView male;
    private ImageView female;
    private EditTextPlus phone;
    private CircleImageView image;
    private Bitmap selectedImage;
    private String selectedImageFilePath;

    private boolean isMale = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_register, container, false);
        findViews();
        initialize();
        manageListeners();
        startAnimations();
        return view;
    }

    private void findViews() {
        uploadImageText = view.findViewById(R.id.fragment_register_image_text);
        fullName = view.findViewById(R.id.fragment_register_full_name);
        username = view.findViewById(R.id.fragment_register_username);
        password = view.findViewById(R.id.fragment_register_password);
        phone = view.findViewById(R.id.fragment_register_phone);
        login = view.findViewById(R.id.fragment_register_login_account);
        loginQuestion = view.findViewById(R.id.fragment_register_have_account_question);
        signup = view.findViewById(R.id.fragment_register_confirm_container);
        signupText = view.findViewById(R.id.fragment_register_confirm_text);
        signupPB = view.findViewById(R.id.fragment_register_confirm_pb);
        dateOfBirth = view.findViewById(R.id.fragment_register_birthday);
        genderText = view.findViewById(R.id.fragment_register_gender);
        genderContainer = view.findViewById(R.id.fragment_register_gender_container);
        day = view.findViewById(R.id.fragment_register_birthday_day);
        month = view.findViewById(R.id.fragment_register_birthday_month);
        year = view.findViewById(R.id.fragment_register_birthday_year);
        male = view.findViewById(R.id.fragment_register_gender_male);
        female = view.findViewById(R.id.fragment_register_gender_female);
        image = view.findViewById(R.id.fragment_register_image);
    }

    private void startAnimations() {
        image.startAnimation(Constants.NORMAL_SCALE_ANIMATION);
        fullName.startAnimation(Constants.LTR_ANIMATION);
        username.startAnimation(Constants.LTR_ANIMATION);
        dateOfBirth.startAnimation(Constants.LTR_ANIMATION);
        day.startAnimation(Constants.LTR_ANIMATION);
        month.startAnimation(Constants.LTR_ANIMATION);
        year.startAnimation(Constants.LTR_ANIMATION);
        phone.startAnimation(Constants.RTL_ANIMATION);
        password.startAnimation(Constants.RTL_ANIMATION);
        genderContainer.startAnimation(Constants.RTL_ANIMATION);
        genderText.startAnimation(Constants.RTL_ANIMATION);
        login.startAnimation(Constants.SLIDE_UP_ANIMATION);
        loginQuestion.startAnimation(Constants.SLIDE_UP_ANIMATION);

    }

    private void initialize() {
        makeConfirmVisible(true);
    }

    private void manageListeners() {
        signup.setOnClickListener(this);
        male.setOnClickListener(this);
        female.setOnClickListener(this);
        login.setOnClickListener(this);
        image.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_register_confirm_container:
                registerHandler();
                break;
            case R.id.fragment_register_gender_male:
                genderHandler(true);
                break;
            case R.id.fragment_register_gender_female:
                genderHandler(false);
                break;
            case R.id.fragment_register_login_account:
                loginHandler();
                break;
            case R.id.fragment_register_image:
                imageChooseHandler();
                break;
        }
    }

    private void registerHandler() {
        if (selectedImageFilePath == null)
            Helper.showToast(getActivity(), "Select a profile photo");
        else if (Helper.isCorrectInput(Constants.InputType.FULL_NAME, fullName) &&
                Helper.isCorrectInput(Constants.InputType.USERNAME, username) &&
                Helper.isCorrectInput(Constants.InputType.PASSWORD, password) &&
                Helper.isCorrectInput(Constants.InputType.DAY, day) &&
                Helper.isCorrectInput(Constants.InputType.MONTH, month) &&
                Helper.isCorrectInput(Constants.InputType.YEAR, year) &&
                Helper.isCorrectInput(Constants.InputType.PHONE, phone)) {


            showTermsOfUseAldPolicyDialog(() -> {
                boolean _isMale = isMale;

                String _fullname = fullName.getText().toString();
                String _username = username.getText().toString();
                String _password = password.getText().toString();
                String _phone = phone.getText().toString();
                String _day = day.getText().toString();
                String _month = month.getText().toString();
                String _year = year.getText().toString();
                String _selectedImageFilePath = selectedImageFilePath;

                makeConfirmVisible(false);

                ServiceGenerator.getInstance().createService(ClientApi.class).isUserExists(_phone, _username).enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if (response.body() != null) {
                            if (!response.body().isRegistered()) {
                                ServiceGenerator.getInstance().createService(ClientApi.class).sendSMSCode(_phone).enqueue(new Callback<SMSResult>() {
                                    @Override
                                    public void onResponse(Call<SMSResult> call, Response<SMSResult> response) {
                                        if (response.body() != null) {
                                            if (response.body().getCode() == 101) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("full_name", _fullname);
                                                bundle.putString("username", _username);
                                                bundle.putString("password", _password);
                                                bundle.putString("phone", _phone);
                                                bundle.putString("day", _day);
                                                bundle.putString("month", _month);
                                                bundle.putString("year", _year);
                                                bundle.putString("image", _selectedImageFilePath);
                                                bundle.putBoolean("is_male", _isMale);
                                                Fragment fragment = new OtpCodeVerificationFragment();
                                                fragment.setArguments(bundle);
                                                makeConfirmVisible(true);
                                                Helper.simpleAddFragment(getFragmentManager(), fragment);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<SMSResult> call, Throwable t) {
                                        makeConfirmVisible(true);
                                        Log.d(Constants.TAG, "Error while sending sms");
                                    }
                                });
                            } else {
                                Helper.showToast(getActivity(), "user exists !");
                                makeConfirmVisible(true);
                            }
                        } else {
                            Log.d(Constants.TAG, "error while check user phone existence");
                            makeConfirmVisible(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        Log.d(Constants.TAG, "error while check user phon existence, " + t.getMessage());
                        makeConfirmVisible(true);
                    }
                });
            });
        } else {
            makeConfirmVisible(true);
            Helper.showToast(getActivity(), R.string.control_input_fields);
        }
    }

    private void showTermsOfUseAldPolicyDialog(Runnable onAccepted) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(ContextHelper.retrieveContext()).inflate(R.layout.dialog_policy, null, false);
        builder.setView(view);
        Dialog dialog = builder.create();
        TextViewPlus acceptAndContinue = view.findViewById(R.id.dialog_policy_accept);
        acceptAndContinue.setOnClickListener(v -> {
            dialog.dismiss();
            onAccepted.run();
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
                    ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
                    selectedImage = Helper.getBitmapFromFilePath(images.get(0).getPath());
                    image.setImageBitmap(selectedImage);
                    selectedImageFilePath = images.get(0).getPath();
                    uploadImageText.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.d(Constants.TAG, "Error while getting picture");
            }
        } else {
            Log.d(Constants.TAG, "Error while choosing image");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void genderHandler(boolean isMale) {
        this.isMale = !isMale;
        male.setImageResource(this.isMale ? R.drawable.ic_male_unselected : R.drawable.ic_male_selected);
        female.setImageResource(this.isMale ? R.drawable.ic_female_selected : R.drawable.ic_female_unselected);
    }

    private void makeConfirmVisible(boolean visible) {
        signupText.setVisibility(visible ? View.VISIBLE : View.GONE);
        signup.setClickable(visible);
        signupPB.setVisibility(!visible ? View.VISIBLE : View.GONE);
    }

    private void imageChooseHandler() {
        ImagePicker.with(RegisterFragment.this)                         //  Initialize ImagePicker with activity or fragment context
                .setMultipleMode(false)              //  Select multiple images or single image
                .setShowCamera(true)                //  Show camera button
                .setCameraOnly(false)               //  Camera mode
                .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
                .setRequestCode(100)                //  Set request code, default Config.RC_PICK_IMAGES
                .setMaxSize(128)
                .setKeepScreenOn(true)              //  Keep screen on when selecting images
                .start();
    }

    private void loginHandler() {
        Helper.simpleAddFragment(getFragmentManager(), new LoginFragment());
    }
}
