package com.ndhunju.folderpicker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ControlsLayout consist of three parts/views
 * 1. New Folder Button (By default allows users to create a new folder and has an icon)
 * 2. Current Path TextView (By default it displays absolute path of current folder)
 * 3. Back Button (By default it takes back to parent folder and has an icon)
 */
public class ControlsLayout extends RelativeLayout {

    //Member variables
    private ImageButton mBackImgBtn;
    private ImageButton mNewDirImgBtn;
    private TextView mPathTextView;

    //Constructor
    public ControlsLayout(@NonNull Context context){
        super(context);

        mBackImgBtn = new ImageButton(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mBackImgBtn.setLayoutParams(params);
        mBackImgBtn.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_revert));

        mPathTextView = new TextView(context);
        LayoutParams params1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);
        params1.addRule(RelativeLayout.RIGHT_OF, mBackImgBtn.getId());
        mPathTextView.setLayoutParams(params1);
        mPathTextView.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault);
        mPathTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mPathTextView.setSingleLine();
        mPathTextView.setEllipsize(TextUtils.TruncateAt.START);

        mNewDirImgBtn = new ImageButton(context);
        LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params2.addRule(RelativeLayout.RIGHT_OF, mPathTextView.getId());
        mNewDirImgBtn.setLayoutParams(params2);
        mNewDirImgBtn.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));

        this.addView(mBackImgBtn);
        this.addView(mPathTextView);
        this.addView(mNewDirImgBtn);

    }

    /**
     * This method sets a callback which will be invoked when {@Link mBackImgBtn} is clicked
     * @param onBackPressedListener
     */
    public void setOnBackPressedListener(OnClickListener onBackPressedListener){
        if(onBackPressedListener != null) mBackImgBtn.setOnClickListener(onBackPressedListener);
    }

    /**
     * This method sets a callback which will be called when {@Link mNewDirImgBtn} is clicked
     * @param onNewDirPressedListener
     */
    public void setOnNewDirPressedListener(OnClickListener onNewDirPressedListener){
        if(onNewDirPressedListener != null) mNewDirImgBtn.setOnClickListener(onNewDirPressedListener);
    }

    /**
     * This method can be used to set custom icon for Back Button
     * @param icon
     */
    public void setBackBtnImg(Drawable icon){
        mBackImgBtn.setImageDrawable(icon);
    }

    /**
     * * This method can be used to set custom icon for New Button
     * @param icon
     */
    public void setNewBtnImg(Drawable icon){
        mNewDirImgBtn.setImageDrawable(icon);
    }

    /**
     * This method returns instance of TextView used to display current path.
     * It can be used to apply custom appearance
     * @return
     */
    public TextView getPathTextView(){
        return mPathTextView;
    }

    /**
     * This method sets current path with passed path
     * @param path
     */
    public void setCurrentPath(String path){
        mPathTextView.setText(path);
    }

}
