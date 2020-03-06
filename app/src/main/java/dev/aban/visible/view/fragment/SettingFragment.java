package dev.aban.visible.view.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import dev.aban.visible.R;
import dev.aban.visible.adapter.Adapter_OpenSource;
import dev.aban.visible.model.License;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.utils.custom_view.TextViewPlus;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private View view;

    private ImageView back;
    private CoordinatorLayout bubbleSoundSwitchContainer;
    private Switch bubbleSoundSwitch;
    private TextViewPlus openSource;
    private TextViewPlus contactUs;
    private TextViewPlus signOut;

    private List<License> licenses = new LinkedList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null)
            view = inflater.inflate(R.layout.fragment_setting, container, false);
        findViews();
        initialize();
        manageListeners();
        return view;
    }

    private void initialize() {
        Helper.recordEventView("SettingFragment");
        bubbleSoundSwitch.setClickable(false);
        bubbleSoundSwitch.setChecked(Helper.loadSetting(Constants._TABLE_USER, Constants._KEY_BUBBLE_SOUND, "true").equals("true"));
        initLicenses();
    }

    private void initLicenses() {
        try {
            String[] fileNames = ContextHelper.retrieveContext().getAssets().list("licenses");
            for (String fileName : fileNames)
                licenses.add(new License(fileName, Helper.readAssetFile(fileName)));

        } catch (IOException e) {
            Log.d(Constants.TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void manageListeners() {
        Helper.initializeBackListener(this, back);
        bubbleSoundSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                Helper.saveSetting(Constants._TABLE_USER, Constants._KEY_BUBBLE_SOUND, String.valueOf(isChecked)));
        signOut.setOnClickListener(this);
        contactUs.setOnClickListener(this);
        openSource.setOnClickListener(this);
        bubbleSoundSwitchContainer.setOnClickListener(this);
    }

    private void findViews() {
        back = view.findViewById(R.id.fragment_setting_back);
        contactUs = view.findViewById(R.id.setting_authority_contact_us);
        signOut = view.findViewById(R.id.setting_authority_log_out);
        openSource = view.findViewById(R.id.setting_legal_open_source_licenses);
        bubbleSoundSwitchContainer = view.findViewById(R.id.setting_general_bubble_sound_container);
        bubbleSoundSwitch = view.findViewById(R.id.setting_general_bubble_sound);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_legal_open_source_licenses:
                Helper.recordEventClick("SettingFragment", "OpenSourceLibraries");
                shoeLicenseDialog();
                break;
            case R.id.setting_authority_log_out:
                Helper.recordEventClick("SettingFragment", "LogOut");
                displayLogOut();
                break;
            case R.id.setting_authority_contact_us:
                Helper.recordEventClick("SettingFragment", "ContactUs");
                Helper.emailToDeveloper(getActivity());
                break;
            case R.id.setting_general_bubble_sound_container:
                bubbleSoundSwitch.setChecked(!bubbleSoundSwitch.isChecked());
                break;

        }
    }

    private void shoeLicenseDialog() {
        RecyclerView open_source_layout_recyclerview;
        RecyclerView.Adapter open_source_recyclerview_adapter;
        RecyclerView.LayoutManager open_source_layout_recyclerview_layout_manager;

        View inflateView = LayoutInflater.from(ContextHelper.retrieveContext()).inflate(R.layout.layout_open_source, null, false);

        open_source_layout_recyclerview = inflateView.findViewById(R.id.open_source_layout_recycler);
        open_source_layout_recyclerview.setHasFixedSize(true);
        open_source_layout_recyclerview_layout_manager = new LinearLayoutManager(ContextHelper.retrieveContext(), RecyclerView.VERTICAL, false);
        open_source_layout_recyclerview.setLayoutManager(open_source_layout_recyclerview_layout_manager);
        open_source_recyclerview_adapter = new Adapter_OpenSource(licenses);
        open_source_layout_recyclerview.setAdapter(open_source_recyclerview_adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inflateView);
        builder.setCancelable(true);
        builder.create().show();
    }

    private void displayLogOut() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(getString(R.string.are_you_sure_to_loog_out));
        builder1.setPositiveButton(R.string.yes, (dialog, which) -> Helper.logout());
        builder1.setNeutralButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder1.create().show();
    }
}
