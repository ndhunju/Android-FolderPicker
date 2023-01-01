# Android-FolderPicker
A folder chooser library for android

A simple picker chooser anyone can integrate into their Android app.

Pros
- Light weight
- Permissions are handled by the Library itself
- Data is returned using convention used by Android
- Doesn't need any layout file. View is created dynamically

# Usage

For a full example see the `SampleActivity` in the
[repository](https://github.com/ndhunju/Android-FolderPicker/blob/master/src/main/java/com/ndhunju/folderpicker/SampleActivity.java).

# To:Do

1. In Activity/Fragment, get an instance of the fragment:

```java

//Show the FolderPickerDialog
FolderPickerDialogFragment fpdf = FolderPickerDialogFragment
                .newInstance(INIT_DIRECTORY, REQUEST_CODE_DIR);
fpdf.show(getSupportFragmentManager(), TAG);

```

2. Handle the result,
(Your Activity or Fragment must implement OnDialogBtnClickedListener.java)
```java
@Override
public void onDialogBtnClicked(Intent data, int whichBtn, int result, int requestCode) {
  switch(requestCode){
    case REQUEST_CODE_DIR:
      if(result != Activity.RESULT_OK)
        return;
      //Get the selected folder path through intent
      String selectedFolderDir = data
                        .getStringExtra(FolderPickerDialogFragment.KEY_CURRENT_DIR);
      Toast.makeText(getBaseContext(), selectedFolderDir, Toast.LENGTH_LONG).show();
      break;
    }
  }
```

# Screenshot
<table>
<tr>
<th>Screenshot of Folder Picker<br><img src="picker_dialog.png" width="30%"></th>
</tr>
</table>
