package com.szxmrt.app.facerecognitionsystem.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.szxmrt.app.facerecognitionsystem.R;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 自定义toolbar
 * Created by Administrator on 2018-06-01.
 */

public class MyToolbar extends Toolbar {
    //@BindView(R.id.toolbar_leftImage)
    ImageView leftImage;
    //@BindView(R.id.toolbar_title)
    ClockTextView textView;
    //@BindView(R.id.toolbar_rightImage)
    WifiStateView rightImage;
    private LayoutInflater layoutInflater;
    private View view;
    private Unbinder unbinder;
    public MyToolbar(Context context) {
        super(context);
        unbinder = ButterKnife.bind(context,this);
    }

    public MyToolbar(Context context, @Nullable AttributeSet attrs) {
	    this(context,attrs,R.attr.toolbarStyle);
    }
    public MyToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        if(attrs!=null){
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyToolbar, defStyleAttr, 0);
            final Drawable rightIcon = a.getDrawable(R.styleable.MyToolbar_rightIcon);
            final Drawable leftIcon = a.getDrawable(R.styleable.MyToolbar_leftIcon);
            final CharSequence msg = a.getText(R.styleable.MyToolbar_clock);
            if (leftIcon != null) setLeftIcon(leftIcon);
            a.recycle();
        }
    }

    public void initView(){
        if(view == null) {
            layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.toolbar, null);
            leftImage = view.findViewById(R.id.toolbar_leftImage);
            rightImage = view.findViewById(R.id.toolbar_rightImage);
            textView = view.findViewById(R.id.toolbar_clock);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.CENTER);
            addView(view, layoutParams);
        }
    }
    public void setLeftIcon(Drawable icon){
	    initView();
        if(leftImage!=null){
            leftImage.setImageDrawable(icon);
            showLeftIcon();
        }
    }
    public void showLeftIcon(){ if(leftImage!=null) leftImage.setVisibility(VISIBLE); }
    public void hideLeftIcon(){ if(leftImage!=null) leftImage.setVisibility(GONE); }
    public void showRightIcon(){ if(rightImage!=null) rightImage.setVisibility(VISIBLE); }
    public void hideRightIcon(){ if(rightImage!=null) rightImage.setVisibility(GONE); }
    public void showTitleView(){ if(textView!=null) textView.setVisibility(VISIBLE); }
    public void hideTitleView(){ if(textView!=null) textView.setVisibility(GONE); }

}
