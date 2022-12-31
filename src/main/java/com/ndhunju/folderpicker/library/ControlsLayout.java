package com.ndhunju.folderpicker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.*;

/**
 * ControlsLayout consist of three parts/views
 * 1. New Folder Button (By default allows users to create a new folder and has an icon)
 * 2. Current Path TextView (By default it displays absolute path of current folder)
 * 3. Back Button (By default it takes back to parent folder and has an icon)
 */
public class ControlsLayout extends RelativeLayout {

    // Member variables
    private final ImageButton mBackImgBtn;
    private final ImageButton mNewDirImgBtn;
    private final TextView mPathTextView;

    // Constructor
    public ControlsLayout(@NonNull Context context){
        super(context);

        // Create and add Back button
        mBackImgBtn = new ImageButton(context);
        mBackImgBtn.setBackground(null);
        mBackImgBtn.setId(View.generateViewId());
        LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mBackImgBtn.setLayoutParams(params);
        mBackImgBtn.setImageDrawable(ContextCompat.getDrawable(
                context,
                R.drawable.ic_baseline_arrow_back_24
        ));
        this.addView(mBackImgBtn);

        // Create and add New Folder button
        mNewDirImgBtn = new ImageButton(context);
        mNewDirImgBtn.setId(View.generateViewId());
        mNewDirImgBtn.setBackground(null);
        mNewDirImgBtn.setImageDrawable(ContextCompat.getDrawable(
                getContext(),
                R.drawable.ic_baseline_create_new_folder_24
        ));
        LayoutParams newDirBtnParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        newDirBtnParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        newDirBtnParams.addRule(RelativeLayout.CENTER_VERTICAL);
        mNewDirImgBtn.setLayoutParams(newDirBtnParams);
        this.addView(mNewDirImgBtn);

        // Create and add text view for showing path
        mPathTextView = new TextView(context);
        mPathTextView.setId(View.generateViewId());
        LayoutParams params1 = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);
        params1.addRule(RelativeLayout.END_OF, mBackImgBtn.getId());
        params1.addRule(RelativeLayout.START_OF, mNewDirImgBtn.getId());
        mPathTextView.setLayoutParams(params1);
        mPathTextView.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault);
        mPathTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mPathTextView.setSingleLine();
        mPathTextView.setEllipsize(TextUtils.TruncateAt.START);
        this.addView(mPathTextView);

    }

    /**
     * This method sets a callback which will be invoked when
     * {@link com.ndhunju.folderpicker.ControlsLayout#mBackImgBtn} is clicked
     */
    public void setOnBackPressedListener(OnClickListener onBackPressedListener) {
        if (onBackPressedListener != null){
            mBackImgBtn.setOnClickListener(onBackPressedListener);
        }
    }

    /**
     * This method sets a callback which will be called when
     * {@link com.ndhunju.folderpicker.ControlsLayout#mNewDirImgBtn} is clicked
     */
    public void setOnNewDirPressedListener(OnClickListener onNewDirPressedListener) {
        if (onNewDirPressedListener != null) {
            mNewDirImgBtn.setOnClickListener(onNewDirPressedListener);
        }
    }

    /**
     * This method can be used to set custom icon for Back Button
     */
    public void setBackBtnImg(Drawable icon){
        mBackImgBtn.setImageDrawable(icon);
    }

    /**
     * * This method can be used to set custom icon for New Button
     */
    public void setNewBtnImg(Drawable icon){
        mNewDirImgBtn.setImageDrawable(icon);
    }

    /**
     * This method returns instance of TextView used to display current path.
     * It can be used to apply custom appearance
     */
    public TextView getPathTextView(){
        return mPathTextView;
    }

    /**
     * This method sets current path with passed path
     */
    public void setCurrentPath(String path){
        mPathTextView.setText(path);
    }

}
