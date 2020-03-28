package dev.aban.visible.view.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import dev.aban.visible.R;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.Helper;

public class SplashFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        Handler handler = new Handler();
        Runnable runnable = () -> openFragment(!Helper.isLogin() ? new LoginFragment() : new MainPageFragment());
        handler.postDelayed(runnable, Constants.SPLASH_DURATION_TIME);
        return view;
    }

    private void openFragment(Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_container, fragment)
                .commitAllowingStateLoss();
    }

}
