package dev.aban.visible.utils;

import android.util.Size;
import android.view.animation.Animation;

import dev.aban.visible.detector.Logger;

public abstract class Constants {
    public static final String TAG = "APPLICATION_PRIMARY_TAG";
    public static final String _TABLE_USER = "User_Table";
    public static final String _TABLE_PROFILE = "Profile_Table";
    public static final String _KEY_LOGIN_STATE = "_key_login_state";

    //NETWORK TIMEOUTS
    public static final long CONNECT_TIME_OUT = 8;//unit : second
    public static final String REQUEST_HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String REQUEST_HEADER_PRAGMA = "Pragma";
    public static final long WRITE_TIME_OUT = 8;//unit : s
    public static final long READ_TIME_OUT = 8;//unit : s

    //NETWORK REQUEST VALUES
    public static final int CACHE_MAX_STALE = 60 * 60 * 24 * 28; // tolerate 4-weeks stale;
    public static final int CACHE_SIZE = 8 * 1024 * 1024; //unit : MB
    public static final int CACHE_MAX_AGE = 5; //seconds

    public static final String BASE_REQUESTS_URL = "https://aban.dev/visible_fa/api/";
    public static final String BASE_FILES_URL = "https://aban.dev/visible_fa/repository";
    public static final int PHONE_NMBER_LENGTH = 11;
    public static final String PHONE_NMBER_PREFIX = "09";
    public static final String PRICE_UNIT = "ریال";
    public static final String _KEY_TOKEN = "_key_token";
    public static final String LINKEDIN_LINK = "https://www.linkedin.com/in/mohammad-jalili-torkamani/";


    //
    public static final Logger LOGGER = new Logger();
    public static final int TF_OD_API_INPUT_SIZE = 200;
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.6f;
    public static final float TEXT_SIZE_DIP = 10;
    public static final Size INPUT_IMAGE_SIZE = new Size(640, 480);
    public static final int MINIMUM_PREVIEW_SIZE = 320;
    public static final String _KEY_BUBBLE_SOUND = "_key_bubble_sound";
    public static final String DFEAULT_FONT_ADDRESS = "fonts/syekan.otf";
    public static final String WEIGHT_FILE_SUFFIX = "_freeze_graph.pb";
    public static final String LABEL_FILE_SUFFIX = "_labels.txt";
    public static final String APP_DIRECTORY_NAME = "visible";

    public static final int NAG_THRESHOLD = 3;
    public static final String _KEY_NAG_COUNTER = "_key_nag_counter";

    public static Animation SLIDE_UP_ANIMATION;
    public static Animation NORMAL_SCALE_ANIMATION;
    public static Animation FAST_SCALE_ANIMATION;
    public static Animation LTR_ANIMATION;
    public static Animation RTL_ANIMATION;

    private Constants() {
    }

    public static enum DownloadMode {
        BOTH_FILES, JUST_FROZEN_WEIGHT, JUST_LABELS
    }

    public enum InputType {
        USERNAME, PASSWORD, FULL_NAME, PHONE, YEAR, MONTH, DAY, OTP
    }
}
