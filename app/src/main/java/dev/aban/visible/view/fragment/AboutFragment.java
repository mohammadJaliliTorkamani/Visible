package dev.aban.visible.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import dev.aban.visible.R;
import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.ContextHelper;
import dev.aban.visible.utils.Helper;
import dev.aban.visible.utils.custom_view.TextViewPlus;

public class AboutFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextViewPlus text;
    private ImageView back;
    private ImageView gmail_image;
    private TextViewPlus gmail_label;
    private TextViewPlus gmail_value;
    private ImageView linkedin_image;
    private TextViewPlus linkedin_label;
    private TextViewPlus linkedin_value;
    private LinearLayout topLogoInfos;
    private Animation SLIDE_UP_animation;
    private Animation SCALE_animation;
    private Animation LTR_animation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_about, container, false);
        findViews();
        initialize();
        startAnimations();
        manageListeners();
        return view;
    }

    private void startAnimations() {
        gmail_image.startAnimation(SLIDE_UP_animation);
        gmail_label.startAnimation(SLIDE_UP_animation);
        gmail_value.startAnimation(SLIDE_UP_animation);
        linkedin_image.startAnimation(SLIDE_UP_animation);
        linkedin_label.startAnimation(SLIDE_UP_animation);
        linkedin_value.startAnimation(SLIDE_UP_animation);
        topLogoInfos.startAnimation(SCALE_animation);
        text.startAnimation(LTR_animation);
    }

    private void initialize() {
        SLIDE_UP_animation = AnimationUtils.loadAnimation(ContextHelper.retrieveContext(), R.anim.slide_up);
        SCALE_animation = AnimationUtils.loadAnimation(ContextHelper.retrieveContext(), R.anim.normal_scale);
        LTR_animation = AnimationUtils.loadAnimation(ContextHelper.retrieveContext(), R.anim.slide_ltr);
    }

    private void manageListeners() {
        Helper.initializeBackListener(this, back);
        gmail_value.setOnClickListener(this);
        linkedin_value.setOnClickListener(this);
    }

    private void findViews() {
        back = view.findViewById(R.id.fragment_about_back);
        gmail_image = view.findViewById(R.id.fragment_about_google_image);
        gmail_label = view.findViewById(R.id.fragment_about_google_label);
        gmail_value = view.findViewById(R.id.fragment_about_gmail_value);
        linkedin_image = view.findViewById(R.id.fragment_about_linkedin_image);
        linkedin_label = view.findViewById(R.id.fragment_about_linkedin_label);
        linkedin_value = view.findViewById(R.id.fragment_about_linkedin_value);
        topLogoInfos = view.findViewById(R.id.fragment_about_top_logo_infos);
        text = view.findViewById(R.id.fragment_about_text);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_about_gmail_value:
                gmailHandle();
                break;
            case R.id.fragment_about_linkedin_value:
                linkedInHandle();
                break;
        }
    }

    private void linkedInHandle() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LINKEDIN_LINK));
        startActivity(browserIntent);
    }

    private void gmailHandle() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"m.jalili@aban.dev"});
        i.putExtra(Intent.EXTRA_SUBJECT, ContextHelper.retrieveContext().getString(R.string.contact_developer));
        i.putExtra(Intent.EXTRA_TEXT, "");
        try {
            startActivity(Intent.createChooser(i, ContextHelper.retrieveContext().getString(R.string.send_via)));
        } catch (android.content.ActivityNotFoundException ex) {
            Helper.showToast(getActivity(), R.string.install_email_client);
        }
    }
}
