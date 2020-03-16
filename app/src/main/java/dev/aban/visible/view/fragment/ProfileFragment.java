package dev.aban.visible.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import dev.aban.visible.R;
import dev.aban.visible.model.User;
import dev.aban.visible.repository.network.ClientApi;
import dev.aban.visible.repository.network.ServiceGenerator;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.utils.custom_view.EditTextPlus;
import dev.aban.visible.utils.custom_view.TextViewPlus;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private View view;

    private ImageView back;
    private ImageView image;
    private TextViewPlus save;
    private TextViewPlus topName;
    private TextViewPlus phone;
    private EditTextPlus fullName;
    private EditTextPlus year;
    private EditTextPlus month;
    private EditTextPlus day;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_profile, container, false);
        findViews();
        initialize();
        manageListeners();
        return view;
    }

    private void initialize() {
        ServiceGenerator.getInstance().createService(ClientApi.class).getUserProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.body() != null) {
                    initFields(response.body());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(Constants.TAG, "Error while getting profile");
            }
        });
    }

    private void initFields(User user) {
        phone.setText(user.getPhone());
        topName.setText(user.getName());
        fullName.setText(user.getName());
        year.setText(String.valueOf(user.getBd_y()));
        month.setText(String.valueOf(user.getBd_m()));
        day.setText(String.valueOf(user.getBd_d()));
        Picasso.get().load(user.getImageURL()).into(image);
    }

    private void manageListeners() {
        Helper.initializeBackListener(this, back);
        save.setOnClickListener(this);
    }

    private void findViews() {
        back = view.findViewById(R.id.fragment_profile_back);
        save = view.findViewById(R.id.fragment_profile_save);
        image = view.findViewById(R.id.fragment_profile_image);
        phone = view.findViewById(R.id.fragment_profile_phone_value);
        fullName = view.findViewById(R.id.fragment_profile_full_name_value);
        topName = view.findViewById(R.id.fragment_profile_top_name);
        year = view.findViewById(R.id.fragment_profile_birthday_year);
        month = view.findViewById(R.id.fragment_profile_birthday_month);
        day = view.findViewById(R.id.fragment_profile_birthday_day);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_profile_save:
                saveHandle();
                break;
        }
    }

    private void saveHandle() {
        if (Helper.isCorrectInput(Constants.InputType.FULL_NAME, fullName) &&
                Helper.isCorrectInput(Constants.InputType.YEAR, year) &&
                Helper.isCorrectInput(Constants.InputType.MONTH, month) &&
                Helper.isCorrectInput(Constants.InputType.DAY, day)) {

            String _fullname = fullName.getText().toString();
            String _year = year.getText().toString();
            String _month = month.getText().toString();
            String _day = day.getText().toString();

            ServiceGenerator.getInstance().createService(ClientApi.class)
                    .updateProfile(
                            _fullname,
                            Integer.parseInt(_year),
                            Integer.parseInt(_month),
                            Integer.parseInt(_day)).enqueue(new Callback<RequestBody>() {
                @Override
                public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                    Helper.showToast(getActivity(), R.string.saved);
                    getActivity().onBackPressed();
                }

                @Override
                public void onFailure(Call<RequestBody> call, Throwable t) {
                    Log.d(Constants.TAG, "Error while update profile");
                }
            });
        } else
            Helper.showToast(getActivity(), R.string.control_input_fields);
    }
}
