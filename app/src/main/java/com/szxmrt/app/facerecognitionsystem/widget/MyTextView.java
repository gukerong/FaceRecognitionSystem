package com.szxmrt.app.facerecognitionsystem.widget;


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * 自定义textView
 * Created by Administrator on 2018-06-01
 */

public class MyTextView extends android.support.v7.widget.AppCompatTextView {
    public MyTextView(Context context) {
        this(context,null);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context,attrs);
	    initFont(context);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    private void initFont(Context context){
        AssetManager assetManager = context.getAssets();
        Typeface typeface = Typeface.createFromAsset(assetManager,"font/fangzhengzhengyuan.ttf");
        setTypeface(typeface);
    }
}
