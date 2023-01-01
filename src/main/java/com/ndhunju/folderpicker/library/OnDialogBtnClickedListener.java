package com.ndhunju.folderpicker.library;

import android.content.Intent;
import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This an interface needed to pass back data to calling class
 */
public interface OnDialogBtnClickedListener {

    int BUTTON_POSITIVE = 1;
    int BUTTON_NEUTRAL = 0;
    int BUTTON_NEGATIVE = -1;

    //Define the list of accepted constants
    @IntDef({BUTTON_POSITIVE, BUTTON_NEUTRAL, BUTTON_NEGATIVE})

    //Tell the compiler not to store annotation data in the .class file
    //TODO This way, as opposed to using enum, is it more efficient in terms of memory??
    @Retention(RetentionPolicy.SOURCE)

    //Declare the ButtonType annotation
    public @interface ButtonType {}

    /**
     * This a callback which can be invoked in a DialogFragment to pass data back to
     * the calling class mainly Activity and Fragment
     * @param data : data to pass
     * @param whichBtn : button that was clicked
     * @param result : was it a success
     * @param requestCode : request code
     */
    void onDialogBtnClicked(Intent data,@ButtonType int whichBtn, int result, int requestCode);

}
