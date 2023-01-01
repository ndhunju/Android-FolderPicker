package com.ndhunju.folderpicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by dhunju on 8/16/2015.
 * This class can be used to let the user
 * pick a folder to save a file. It returns select folder's
 * absolute path via callbacks
 */
public class FolderPickerDialogFragment extends DialogFragment {

    // Constants
    private static final String KEY_REQUEST_CODE = "keyRequestCode";
    public static final String KEY_CURRENT_DIR = "keyCurrentDirectory";
    private static final int REQUEST_CODE_MANAGE_FILES_PERMISSIONS = 3944;
    private static final int REQUEST_PERMISSIONS_WRITE_STORAGE = 2323;

    // Member variables
    private String mCurrentDir = "";
    private String mSdcardDir = "";
    private List<String> mSubDirs ;
    private LinearLayout mainLayout;
    private com.ndhunju.folderpicker.ControlsLayout mControlsLayout;
    private ArrayAdapter<String> mDirListAdapter;
    private com.ndhunju.folderpicker.OnDialogBtnClickedListener mOnDialogBtnPressedListener;

    /**
     * This method should be called to get a new instance of the class. This assures
     * that the state of the DialogFragment is retrieved by Android when there is
     * a configuration change or when the whole screen needs to be recreated
     * @param initAbsoluteDir : Initial directory to show
     * @param requestCode : Result is delivered with the same requestCode
     */
    @NonNull
    public static FolderPickerDialogFragment newInstance(
            String initAbsoluteDir,
            int requestCode
    ) {
        FolderPickerDialogFragment instance = new FolderPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_CURRENT_DIR, initAbsoluteDir);
        args.putInt(KEY_REQUEST_CODE, requestCode);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //make sure the calling activity or fragment implements the listener
        try {
            if (context instanceof com.ndhunju.folderpicker.OnDialogBtnClickedListener) {
                mOnDialogBtnPressedListener = (com.ndhunju.folderpicker.OnDialogBtnClickedListener) context;
            }
            else if (getTargetFragment() instanceof com.ndhunju.folderpicker.OnDialogBtnClickedListener) {
                mOnDialogBtnPressedListener = (com.ndhunju.folderpicker.OnDialogBtnClickedListener) getTargetFragment();
            } else {
                throw new ClassCastException();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement " +
                    com.ndhunju.folderpicker.OnDialogBtnClickedListener.class.getSimpleName());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create a main view container
        mainLayout = new LinearLayout(getActivity());
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        updateChildViewsInMainLayout();

        // Build AlertDialog to display directories
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setView(mainLayout);
        dialogBuilder.setTitle(String.format(
                getString(R.string.msg_choose),
                getString(R.string.str_folder)
        ));

        return dialogBuilder.create();
    }

    private void updateChildViewsInMainLayout() {

        if (getActivity() == null) {
            return;
        }

        mainLayout.removeAllViews();

        if (PermissionManager.INSTANCE.hasManageFilePermission(getActivity())) {

            mSdcardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            // Get the initial directory from intent if provided
            String initDir = getArguments().getString(KEY_CURRENT_DIR);
            mCurrentDir = (initDir != null) ? initDir : mSdcardDir;

            // If the passed dir doesn't exist or is not a directory,
            // use the root local storage directory
            File dirFile = new File(mCurrentDir);
            if (!dirFile.exists() || !dirFile.isDirectory())
                mCurrentDir = mSdcardDir;

            addDirectoryViewTo(mainLayout);
        } else {
            addGrantPermissionViewTo(mainLayout);
        }
    }

    private void addDirectoryViewTo(LinearLayout parent) {

        DirectoryLayout directoryLayout = new DirectoryLayout(getActivity());
        mControlsLayout = directoryLayout.getControlsLayout();
        mControlsLayout.setCurrentPath(mCurrentDir);
        mControlsLayout.setOnBackPressedListener(view -> {
            // If it is the top level directory, do nothing
            if (!mCurrentDir.equals(mSdcardDir)) {
                // Navigate back to an upper directory
                mCurrentDir = new File(mCurrentDir).getParent();
                updateDirectory();
            }
        });

        mControlsLayout.setOnNewDirPressedListener(view -> {

            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(50, 50, 50, 50);

            TextView titleView = new TextView(getActivity());
            titleView.setText(getString(R.string.str_new_folder_name));
            titleView.setTextAppearance(
                    getActivity(),
                    android.R.style.TextAppearance_DialogWindowTitle
            );
            titleView.getPaint().setFakeBoldText(true);
            titleView.setTextColor(Color.BLACK);
            linearLayout.addView(titleView);

            final EditText nameInput = new EditText(getActivity());
            linearLayout.addView(nameInput);

            // Show new folder name input dialog
            new AlertDialog.Builder(getActivity())
                    .setView(linearLayout)
                    .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                        String newDirName = nameInput.getText().toString();
                        createSubDir(mCurrentDir + "/" + newDirName);
                        updateDirectory();
                    }).setNegativeButton(android.R.string.cancel, null).show();
        });

        // Set click listener to choose button
        directoryLayout.getChooseButton().setOnClickListener(v -> {
            // Current directory chosen
            if (mOnDialogBtnPressedListener != null) {
                // Call registered listener supplied with the chosen directory
                Intent resultIntent = new Intent();
                resultIntent.putExtra(KEY_CURRENT_DIR, mCurrentDir);
                mOnDialogBtnPressedListener.onDialogBtnClicked(resultIntent,
                        com.ndhunju.folderpicker.OnDialogBtnClickedListener.BUTTON_POSITIVE, Activity.RESULT_OK,
                        getArguments().getInt(KEY_REQUEST_CODE));
                dismiss();
            }
        });

        directoryLayout.getCancelButton().setOnClickListener(v -> dismiss());

        // Get subdirectories for current directory
        mSubDirs = getSubDirectories(mCurrentDir);

        // Create a ArrayAdapter to display sub directories
        mDirListAdapter = new com.ndhunju.folderpicker.DirListAdapter<>(parent.getContext(), mSubDirs);
        ListView directoryListViw = directoryLayout.getDirectoryListView();
        directoryListViw.setAdapter(mDirListAdapter);
        directoryListViw.setOnItemClickListener((parent1, view, position, id) -> {
            // Navigate into the sub-directory
            mCurrentDir += "/" + mDirListAdapter.getItem(position);
            updateDirectory();
        });

        parent.addView(directoryLayout);

    }

    private void addGrantPermissionViewTo(LinearLayout parent) {

        if (getActivity() == null) {
            return;
        }

        TextView text = new TextView(parent.getContext());
        text.setGravity(Gravity.CENTER);
        text.setText(getString(R.string.msg_permission_manage_file_not_granted));
        text.setTextAppearance(
                parent.getContext(),
                android.R.style.TextAppearance_DeviceDefault_Widget_TextView
        );
        text.setPadding(50, 50, 50, 25);
        parent.addView(text);

        AppCompatButton button = new AppCompatButton(parent.getContext());
        LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(15, 15, 15, 50);
        params.setMarginStart(15);
        params.setMarginEnd(15);
        button.setLayoutParams(params);
        button.setText(R.string.btn_grant_permission);
        button.setOnClickListener(v -> PermissionManager.INSTANCE.askManageFilePermission(
                this,
                REQUEST_CODE_MANAGE_FILES_PERMISSIONS,
                REQUEST_PERMISSIONS_WRITE_STORAGE
        ));
        parent.addView(button);

    }

    /**
     * Returns an instance of {@link ControlsLayout}
     * which can be used to change default icons for New Folder Button
     * {@link ControlsLayout#setNewBtnImg(Drawable)} and likewise for Back Button.
     * Also OnClickListener can be set for buttons
     */
    public com.ndhunju.folderpicker.ControlsLayout getControlsLayout(@NonNull Context context){
        return mControlsLayout;
    }

    /**
     * This method creates a new folder with
     * passed directory
     */
    private boolean createSubDir(@NonNull String newDir) {
        File newDirFile = new File(newDir);
        // NOTE: Direction is created even though mkdir() return false below :idk:
        return !newDirFile.mkdir();
    }

    /**
     * This method returns all the sub folders' path in
     * string format in a list
     * @param dir : directory of which sub folders are needed
     */
    private List<String> getSubDirectories(String dir) {
        List<String> dirs = new ArrayList<>();

        try {
            File dirFile = new File(dir);
            if (!dirFile.exists() || !dirFile.isDirectory()) return dirs;

            for (File file : dirFile.listFiles()) {
                if (file.isDirectory())
                    dirs.add(file.getName());
            }
        } catch (Exception e) { e.printStackTrace(); }

        return dirs;
    }

    /**
     * This is a helper method to update the
     * current directories
     */
    private void updateDirectory() {
        mSubDirs.clear();
        mSubDirs.addAll(getSubDirectories(mCurrentDir));
        mControlsLayout.setCurrentPath(mCurrentDir);
        mDirListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_MANAGE_FILES_PERMISSIONS) {
            // Although, user granted the permission, resultCode == RESULT_CANCELLED
            // so can't use result code to check if (resultCode == Activity.RESULT_OK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    updateChildViewsInMainLayout();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        if (requestCode == REQUEST_PERMISSIONS_WRITE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateChildViewsInMainLayout();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}