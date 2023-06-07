package com.akangcupez.atask.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.ext.SdkExtensions
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.akangcupez.atask.R
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.FishBunCreator
import com.sangcomz.fishbun.MimeType
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter

fun isPhotoPickerAvailable(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        true
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        isValidExtensionVersion()
    } else {
        false
    }
}

private fun isValidExtensionVersion(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2
    } else {
        true
    }
}

fun useCamera(context: Context, cameraLauncher: ActivityResultLauncher<Intent>) {
    val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    try {
        cameraLauncher.launch(i)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Failed to launch camera. Please check your camera", Toast.LENGTH_SHORT).show()
    }
}

fun useGallery(
    activity: Activity,
    photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>,
    fishbunLauncher: ActivityResultLauncher<Intent>
) {
    if (isPhotoPickerAvailable()) {
        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    } else {
        setupFishbunLib(activity)
            .setMaxCount(1)
            .startAlbumWithActivityResultCallback(fishbunLauncher)
    }
}

fun setupFishbunLib(activity: Activity): FishBunCreator {
    val primaryColor = ContextCompat.getColor(activity, R.color.colorPrimary)
    return FishBun.with(activity)
        .setImageAdapter(GlideAdapter())
        .setIsUseDetailView(false)
        .setPickerSpanCount(4)
        .setActionBarColor(primaryColor, primaryColor,false)
        .setActionBarTitleColor(Color.WHITE)
        .setButtonInAlbumActivity(false)
        .setCamera(false)
        .setHomeAsUpIndicatorDrawable(
            ContextCompat.getDrawable(
                activity, R.drawable.arrow_back_24
            )
        )
        .setAllViewTitle("All")
        .setActionBarTitle("Gallery")
        .textOnImagesSelectionLimitReached("You have choosen maximum image limit")
        .textOnNothingSelected("You're not selecting any images")
        .setSelectCircleStrokeColor(
            ContextCompat.getColor(activity, R.color.colorPrimary)
        )
        .isStartInAllView(false)
        .exceptMimeType(listOf(MimeType.GIF))
}

fun calculateMath(number1: Int, number2: Int, operator: String): Int {
    return when(operator) {
        "x", "*" -> number1 * number2
        "/", ":" -> number1 / number2
        "+" -> number1 + number2
        "-" -> number1 - number2
        else -> 0
    }
}

fun sanitizeMathOperator(operator: String): String {
    return when(operator) {
        "x" -> "*"
        ":" -> "/"
        else -> operator
    }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}
