package com.ndhunju.folderpicker.library;

import android.content.Intent;

/**
 * This an interface needed to pass back data to calling class
 */
public interface OnDialogBtnClickedListener {

    public final static int BUTTON_POSITIVE = 1;
    public final static int BUTTON_NEUTRAL = 0;
    public final static int BUTTON_NEGATIVE = -1;

    /**
     * This a callback which can be invoked in a DialogFragment to pass data back to
     * the calling class mainly Activity and Fragment
     * @param data : data to pass
     * @param whichBtn : button that was clicked
     * @param result : was it a success
     * @param requestCode
     */
    public void onDialogBtnClicked(Intent data, int whichBtn, int result, int requestCode);

}
