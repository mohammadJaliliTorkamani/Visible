package dev.aban.visible.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.aban.visible.R;
import dev.aban.visible.model.NagScreen;
import dev.aban.visible.repository.network.ClientApi;
import dev.aban.visible.repository.network.ServiceGenerator;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.utils.custom_view.TextViewPlus;
import dev.aban.visible.view.fragment.LoginFragment;
import dev.aban.visible.view.fragment.MainPageFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        handleNagScreen();
        if (null == savedInstanceState) {
            openFragment(!Helper.isLogin() ? new LoginFragment() : new MainPageFragment());
        }
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_container, fragment)
                .commit();
    }

    private void handleNagScreen() {
        int counter = Integer.parseInt(Helper.loadSetting(Constants._TABLE_USER, Constants._KEY_NAG_COUNTER, "0"));
        Log.d(Constants.TAG, "Nag Counter : " + counter);
        if (counter < Constants.NAG_THRESHOLD) {
            Helper.saveSetting(Constants._TABLE_USER, Constants._KEY_NAG_COUNTER, String.valueOf(counter + 1));
        } else {
            Helper.saveSetting(Constants._TABLE_USER, Constants._KEY_NAG_COUNTER, String.valueOf(0));
            ServiceGenerator.getInstance().createService(ClientApi.class).getNagScreen().enqueue(new Callback<NagScreen>() {
                @Override
                public void onResponse(Call<NagScreen> call, Response<NagScreen> response) {
                    if (response.body() != null) {
                        showNagScreen(response.body());
                    } else
                        Log.d(Constants.TAG, "null");
                }

                @Override
                public void onFailure(Call<NagScreen> call, Throwable t) {
                    Log.d(Constants.TAG, t.getMessage());
                }
            });

        }
    }

    private void showNagScreen(NagScreen nag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_nag_screen, null, false);
        builder.setCancelable(false);
        builder.setView(view);
        TextViewPlus title = view.findViewById(R.id.nag_title);
        TextViewPlus description = view.findViewById(R.id.nag_description);
        CircleImageView image = view.findViewById(R.id.nag_image);
        TextViewPlus showMe = view.findViewById(R.id.nag_show_em);
        TextViewPlus no = view.findViewById(R.id.nag_no);
        title.setText(nag.getTitle());
        description.setText(nag.getDescription());
        Picasso.get().load(nag.getImageURL()).into(image);
        Dialog dialog = builder.create();
        showMe.setOnClickListener(v -> {
            Helper.recordEventClick("MainActivity", "NAG yes option");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(nag.getYesLink()));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            dialog.dismiss();
            startActivity(browserIntent);
        });

        no.setOnClickListener(v -> {
            Helper.recordEventClick("MainActivity", "NAG no option");
            dialog.dismiss();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
