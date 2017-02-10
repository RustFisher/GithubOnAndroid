package com.rustfisher.githubonandroid.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rustfisher.githubonandroid.R;

public class InputField extends RelativeLayout {

    private RelativeLayout reLayout;
    private TextView leftTv;
    private EditText middleEt;
    private Button rightBtn;

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
        rightBtn = (Button) reLayout.findViewById(R.id.rightBtn);
    }

    public void setText(CharSequence leftText, CharSequence etHint, CharSequence btnText) {
        leftTv.setText(leftText);
        middleEt.setHint(etHint);
        rightBtn.setText(btnText);
    }

    public void setRightBtnOnclickListener(OnClickListener listener) {
        rightBtn.setOnClickListener(listener);
    }

    public void setEtText(CharSequence etText) {
        middleEt.setText(etText);
    }

    public String getEtText() {
        return middleEt.getText().toString();
    }

    public TextView getLeftTv() {
        return leftTv;
    }

    public EditText getMiddleEt() {
        return middleEt;
    }

    public Button getRightBtn() {
        return rightBtn;
    }
}
