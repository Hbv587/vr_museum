package be.kuleuven.softdev.cbhuo.vr_museum;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MyImageTextViewNew extends LinearLayout {

    private ImageView mImageView = null;
    private TextView mTextView = null;
    private int imageId;
    private int textId, textColorId;

    public MyImageTextViewNew(Context context) {
        this(context, null);
    }

    public MyImageTextViewNew(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyImageTextViewNew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOrientation(LinearLayout.VERTICAL);//vertical sequence
        this.setGravity(Gravity.CENTER);//center
        if (mImageView == null) {
            mImageView = new ImageView(context);
        }
        if (mTextView == null) {
            mTextView = new TextView(context);
        }
        if (attrs == null)
            return;
        int count = attrs.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String attrName = attrs.getAttributeName(i);//get attribute name
            //get ID of resource based on attribute
            switch (attrName) {
                //image shown
                case "image":
                    imageId = attrs.getAttributeResourceValue(i, 0);
                    break;
                //text shown
                case "text":
                    textId = attrs.getAttributeResourceValue(i, 0);
                    break;
                //color of text
                case "textColor":
                    textColorId = attrs.getAttributeResourceValue(i, 0);
                    break;
            }
        }
        init();
    }

    /**
     * initialization
     */
    private void init() {
        this.setText(textId);
        mTextView.setGravity(Gravity.CENTER);//center
        this.setTextColor(textColorId);
        this.setImgResource(imageId);
        addView(mImageView);//add image widget to layout
        addView(mTextView);//add text widget
    }

    /**
     * set the picture shown
     *
     * @param resourceID
     */
    private void setImgResource(int resourceID) {
        if (resourceID == 0) {
            this.mImageView.setImageResource(0);
        } else {
            this.mImageView.setImageResource(resourceID);
        }
    }

    /**
     * set the text shown
     *
     * @param text
     */
    public void setText(int text) {
        this.mTextView.setText(text);
    }

    /**
     * set the color of text (black as default)
     *
     * @param color
     */
    private void setTextColor(int color) {
        if (color == 0) {
            this.mTextView.setTextColor(Color.BLACK);
        } else {
            this.mTextView.setTextColor(getResources().getColor(color));
        }
    }

}