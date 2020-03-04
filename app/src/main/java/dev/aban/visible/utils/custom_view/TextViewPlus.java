package dev.aban.visible.utils.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;

import com.daasuu.cat.CountAnimationTextView;

import dev.aban.visible.R;
import dev.aban.visible.utils.Constants;

public class TextViewPlus extends CountAnimationTextView {
    private static final String TAG = "TextView";


    public TextViewPlus(Context context) {
        super(context);
    }

    public TextViewPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public TextViewPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        String customFont = a.getString(R.styleable.TextViewPlus_customFont);
        boolean underline = a.getBoolean(R.styleable.TextViewPlus_underline, false);
        String text = getText().toString();
        if (underline) {
            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            setText(content);
        }
        setCustomFont(ctx, attrs, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, AttributeSet attrs, String asset) {
        Typeface typeface;
        try {
            typeface = Typeface.createFromAsset(ctx.getAssets(), asset == null ? Constants.DFEAULT_FONT_ADDRESS : "fonts/" + asset);
            setTypeface(typeface, ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus).getBoolean(R.styleable.TextViewPlus_bold, false) ? Typeface.BOLD : Typeface.NORMAL);
        } catch (Exception e) {
            Log.e(TAG, "Unable to load typeface: " + e.getMessage());
            return false;
        }
        return true;
    }
}