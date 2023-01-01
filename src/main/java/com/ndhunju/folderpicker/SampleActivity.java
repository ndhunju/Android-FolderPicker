package com.ndhunju.folderpicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ndhunju.folderpicker.library.FolderPickerDialogFragment;
import com.ndhunju.folderpicker.library.OnDialogBtnClickedListener;


public class SampleActivity extends AppCompatActivity implements OnDialogBtnClickedListener {

    private static final int REQUEST_CODE_DIR = 1;
    private static final String TAG = SampleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Wire up widgets
        Button folderPickerBtn = (Button)findViewById(R.id.show_folder_picker_btn);
        folderPickerBtn.setOnClickListener(v -> {
            // Show the FolderPickerDialog
            FolderPickerDialogFragment fpdf = FolderPickerDialogFragment.newInstance(
                    null,
                    REQUEST_CODE_DIR
            );
            fpdf.show(getSupportFragmentManager(), TAG);
        });



    }

    @Override
    public void onDialogBtnClicked(Intent data, int whichBtn, int result, int requestCode) {
        switch (requestCode){
            case REQUEST_CODE_DIR:

                if(result != Activity.RESULT_OK)
                    return;
                // Get the selected folder path through intent
                String selectedFolderDir = data.getStringExtra(FolderPickerDialogFragment.KEY_CURRENT_DIR);
                Toast.makeText(getBaseContext(), selectedFolderDir, Toast.LENGTH_LONG).show();
        }
    }
}
