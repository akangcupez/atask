package com.akangcupez.atask.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.akangcupez.atask.BuildConfig

object PermissionUtil {

    private fun getStoragePermissionString(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    fun checkReadStoragePermission(
        activity: Activity,
        launcher: ActivityResultLauncher<String>,
        callback: (isGranted: Boolean) -> Unit
    ) {
        val storagePermission = getStoragePermissionString()
        when {
            ContextCompat.checkSelfPermission(
                activity,
                storagePermission
            ) == PackageManager.PERMISSION_GRANTED -> {
                callback(true)
            }
            shouldShowRequestPermissionRationale(activity, storagePermission) -> {
                callback(false)
            }
            else -> {
                launcher.launch(storagePermission)
            }
        }
    }

    fun checkCameraPermission(
        activity: Activity,
        launcher: ActivityResultLauncher<String>,
        callback: ((isGranted: Boolean) -> Unit)
    ) {
        when {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                callback(true)
            }
            shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) -> {
                callback(false)
            }
            else -> {
                launcher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    fun showAlert(activity: Activity, message: String) {
        if (activity.isFinishing) return
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle("")
            .setMessage(message)
            .setPositiveButton("Settings") { dialog, _ ->
                dialog.dismiss()
                gotoSettingActivity(activity)
            }
            .setCancelable(true)
            .setOnCancelListener { dialog -> dialog.dismiss() }
            .create()

        alertDialog.show()
    }

    private fun gotoSettingActivity(activity: Activity) {
        val action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
        val intent = Intent(action, uri)
        activity.startActivity(intent)
    }
}