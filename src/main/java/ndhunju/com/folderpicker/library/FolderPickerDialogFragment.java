package ndhunju.com.folderpicker.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class can be used to let the user
 * pick a folder to save a file. It returns select folder's
 * absolute path via callbacks
 */
public class FolderPickerDialogFragment extends DialogFragment{

    //Constants
    private static final String KEY_REQUEST_CODE = "keyRequestCode";
    public static final String KEY_CURRENT_DIR = "keyCurrentDirectory";
    private static final String TAG = FolderPickerDialogFragment.class.getSimpleName();

    //Member variables
    private String mCurrentDir="";
    private String mSdcardDir ="";
    private List<String> mSubDirs ;
    private ControlsLayout mControlsLayout;
    private ArrayAdapter<String> mDirListAdapter;
    private OnDialogBtnClickedListener mOnDialogBtnPressedListener;

    /**
     * This method should be called to get a new instance of the class. This assures
     * that the state of the DialogFragment is retrieved by Android when there is
     * a configuration change or when the whole screen needs to be recreated
     * @param initAbsoluteDir : Initial directory to show
     * @param requestCode : Result is delivered with the same requestCode
     * @return
     */
    public static FolderPickerDialogFragment newInstance( String initAbsoluteDir, int requestCode){
        FolderPickerDialogFragment instance = new FolderPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_CURRENT_DIR, initAbsoluteDir);
        args.putInt(KEY_REQUEST_CODE, requestCode);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //make sure the calling activity implements the listener
        try {  mOnDialogBtnPressedListener = (OnDialogBtnClickedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " +
                    OnDialogBtnClickedListener.class.getSimpleName());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mSdcardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        //get the initial directory from intent if provided
        String initDir = getArguments().getString(KEY_CURRENT_DIR);
        mCurrentDir = (initDir != null) ? initDir : mSdcardDir;


        //If the passed dir doesn't exist or is not a directory, use the root sdcard directory
        File dirFile = new File(mCurrentDir);
        if (!dirFile.exists() || !dirFile.isDirectory())
            mCurrentDir = mSdcardDir;

        //Create a header container to add child views including navigation controls
        LinearLayout headerLayout = new LinearLayout(getActivity());
        headerLayout.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(getActivity());
        title.setText("Choose Folder");
        title.setTextAppearance(getActivity(), android.R.style.TextAppearance_DeviceDefault_Widget_ActionBar_Title);
        title.setPadding(15, 15, 15, 15);

        //If the ControlsLayout has not been set, use default
        if(mControlsLayout == null)
            mControlsLayout = createDefaultControlLayout(getActivity());

        //Add ControlLayout to container
        headerLayout.addView(title);
        headerLayout.addView(mControlsLayout);

        //get subdirectories for current directory
        mSubDirs = getSubDirectories(mCurrentDir);

        //create a ArrayAdapter to display sub directories
        mDirListAdapter = new DirListAdapter<>(getActivity(), mSubDirs);

        //Build AlertDialog to display directories
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setCustomTitle(headerLayout)
                    .setSingleChoiceItems(mDirListAdapter, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int item) {
                            // Navigate into the sub-directory
                            mCurrentDir += "/" + ((AlertDialog) dialogInterface).getListView().getAdapter().getItem(item);
                            updateDirectory();
                        }
                    })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Current directory chosen
                            if (mOnDialogBtnPressedListener != null) {
                                // Call registered listener supplied with the chosen directory
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(KEY_CURRENT_DIR, mCurrentDir);
                                mOnDialogBtnPressedListener.onDialogBtnClicked(resultIntent,
                                        OnDialogBtnClickedListener.BUTTON_POSITIVE, Activity.RESULT_OK,
                                        getArguments().getInt(KEY_REQUEST_CODE));
                                dismiss();
                            }
                        }
                    })
                    .setNegativeButton(getString(android.R.string.cancel), null);



        return dialogBuilder.create();
    }

    /**
     * Returns an instance of {@link ControlsLayout}
     * which can be used to change default icons for New Folder Button {@Link #ControlLayout#setNewBtnImg}
     * and likewise for Back Button. Also OnClickListener can be set for buttons
     * @return
     */
    public ControlsLayout getControlsLayout(Context context){
        if(mControlsLayout == null)
            mControlsLayout = createDefaultControlLayout(context);
        return mControlsLayout;
    }

    /**
     * This methods create an instance of {@Link ControlLayout} below settings,
     * Back Button Pressed: takes to parent directory
     * New Button Pressed: displays EditText to create new directory
     * @param context
     * @return
     */
    public ControlsLayout createDefaultControlLayout(Context context){
        ControlsLayout controlLayout = new ControlsLayout(context);
        controlLayout.setCurrentPath(mCurrentDir);
        controlLayout.setOnBackPressedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If it is the top level directory, do nothing
                if (!mCurrentDir.equals(mSdcardDir)) {
                    // Navigate back to an upper directory
                    mCurrentDir = new File(mCurrentDir).getParent();
                    updateDirectory();
                }
            }
        });
        controlLayout.setOnNewDirPressedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText nameInput = new EditText(getActivity());

                // Show new folder name input dialog
                new AlertDialog.Builder(getActivity())
                        .setTitle("New Folder Name")
                        .setView(nameInput)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String newDirName = nameInput.getText().toString();
                                // Create new directory
                                if (createSubDir(mCurrentDir + "/" + newDirName)) {
                                    // Navigate into the new directory
                                    mCurrentDir += "/" + newDirName;
                                    updateDirectory();
                                } else {
                                    Log.d(TAG, "Failed creating new directory ");
                                }
                            }
                        }).setNegativeButton(android.R.string.cancel, null).show();
            }
        });

        return controlLayout;
    }

    /**
     * This method creates a new folder with
     * passed directory
     * @param newDir
     * @return
     */
    private boolean createSubDir(String newDir) {
        File newDirFile = new File(newDir);
        if (!newDirFile.exists()) return newDirFile.mkdir();
        return false;
    }

    /**
     * This method returns all the sub folders' path in
     * string format in a list
     * @param dir : directory of which sub folders are needed
     * @return
     */
    private List<String> getSubDirectories(String dir) {
        List<String> dirs = new ArrayList<String>();

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

}