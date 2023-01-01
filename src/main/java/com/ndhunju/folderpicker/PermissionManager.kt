package com.ndhunju.folderpicker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment

/**
 * This class will hold logic around permission that the app needs.
 */
object PermissionManager {

    /**
     * Returns true if the app has permission to manage files in the device
     */
    fun hasManageFilePermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check WRITE_EXTERNAL_STORAGE permission for Android OS older than R
            return ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        // For rest, once we define the the AndroidManifest file, we have the permission
        return true
    }

    /**
     * Returns true is permission is already granted.
     */
    fun askManageFilePermission(
            fragment: Fragment,
            requestCodeForManageFilePermission: Int,
            requestCodeForWriteExternalStoragePermission: Int
    ): Boolean {

        val context = fragment.context ?: return false

        if (hasManageFilePermission(context)) {
            return true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            // Request manage all files permission at runtime
            val uri = Uri.parse("package:" + context.packageName)
            fragment.startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri),
                    requestCodeForManageFilePermission
            )
            return false
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check WRITE_EXTERNAL_STORAGE permission for Android OS older than R
            if (checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request WRITE_EXTERNAL_STORAGE permission for Android OS >= M and <R
                fragment.requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        requestCodeForWriteExternalStoragePermission
                )
            }
            return false
        }
        return true
    }

    fun canSaveImageOnDownloadsFolder(context: Context): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Storing image to the "Downloads" folder worked without
            // getting an image write permission on Tiramisu (33)
            return true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Keep the existing logic for Android M and above
            return checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        return true
    }

    fun askPermissionForSavingImageOnDownloadsFolder(
            activity: Activity,
            requestCode: Int
    ): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Storing image to the "Downloads" folder worked without
            // getting an image write permission on Tiramisu (33)
            return true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Keep the existing logic for Android M and above
            if (checkSelfPermission(
                            activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                activity.requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        requestCode
                )

                // Permission not granted yet
                return false
            }
        }

        return true
    }
}