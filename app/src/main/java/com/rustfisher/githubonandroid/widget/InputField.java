package com.rustfisher.githubonandroid.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rustfisher.githubonandroid.R;

public class InputField extends RelativeLayout {

    private RelativeLayout reLayout;
    private TextView leftTv;
    private EditText middleEt;
    private ImageView farRightIv;
    private ImageView rightIv;

    public InputField(Context context) {
        this(context, null);
    }

    public InputField(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (null == reLayout) {
            reLayout = (RelativeLayout) inflater.inflate(R.layout.input_layout, this);
        }
        leftTv = (TextView) reLayout.findViewById(R.id.leftTv);
        middleEt = (EditText) reLayout.findViewById(R.id.middleEt);
        farRightIv = (ImageView) reLayout.findViewById(R.id.farRightIv);
        rightIv = (ImageView) reLayout.findViewById(R.id.rightIv);
    }

    public void setRightIvDrawable(Drawable drawable) {
        rightIv.setImageDrawable(drawable);
        rightIv.setVisibility(VISIBLE);
    }

    public void setFarRightDrawable(Drawable drawable) {
        farRightIv.setImageDrawable(drawable);
        farRightIv.setVisibility(VISIBLE);
    }

    public void setText(CharSequence leftText, CharSequence etHint) {
        leftTv.setText(leftText);
        middleEt.setHint(etHint);
    }

    public void setRightOnclickListener(OnClickListener listener) {
        rightIv.setOnClickListener(listener);
    }

    public void setFarRightOnclickListener(OnClickListener listener) {
        farRightIv.setOnClickListener(listener);
    }

    public void setEtText(CharSequence etText) {
        middleEt.setText(etText);
    }

    public String getEtText() {
        return middleEt.getText().toString();
    }

    public void clearEtFocus() {
        middleEt.clearFocus();
    }

    public TextView getLeftTv() {
        return leftTv;
    }

    public EditText getMiddleEt() {
        return middleEt;
    }

    public ImageView getRightBtn() {
        return farRightIv;
    }
}
