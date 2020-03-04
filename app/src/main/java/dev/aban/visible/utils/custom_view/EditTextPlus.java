package dev.aban.visible.utils.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import dev.aban.visible.R;
import dev.aban.visible.utils.Constants;

public class EditTextPlus extends AppCompatEditText {
    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    public EditTextPlus(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public EditTextPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        init();
    }

    public EditTextPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.attrs = attrs;
        this.defStyle = defStyle;
        init();
    }

    private void init() {
        try {
            String customFont = getCustomFont(context, attrs);
            Typeface face = Typeface.createFromAsset(context.getAssets(), customFont == null ? Constants.DFEAULT_FONT_ADDRESS :
                    ("fonts/" + customFont));
            setTypeface(face, context.obtainStyledAttributes(attrs, R.styleable.EditTextPlus).getBoolean(R.styleable.EditTextPlus_input_bold, false) ? Typeface.BOLD : Typeface.NORMAL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getCustomFont(Context context, AttributeSet attrs) {
        TypedArray ta = null;

        try {
            ta = context.obtainStyledAttributes(attrs, R.styleable.EditTextPlus, 0, 0);
            String fontName = ta.getString(R.styleable.EditTextPlus_customFont_et);
            return fontName;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            ta.recycle();
        }
        return null;
    }
}
