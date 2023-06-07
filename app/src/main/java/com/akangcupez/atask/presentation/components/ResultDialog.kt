package com.akangcupez.atask.presentation.components

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.akangcupez.atask.App
import com.akangcupez.atask.databinding.DialogResultBinding
import com.akangcupez.atask.domain.model.Calculation
import com.akangcupez.atask.presentation.viewmodels.MainViewModel
import com.akangcupez.atask.utils.Resource
import com.akangcupez.atask.utils.hide
import com.akangcupez.atask.utils.show
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.coroutines.launch

class ResultDialog : BaseDialog() {

    private lateinit var binding: DialogResultBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    private var calculation: Calculation? = null
    private var eventListener: ((mode: ScanMode, calculation: Calculation?, error: String?) -> Unit)? = null

    private var scanMode: ScanMode = ScanMode.BITMAP

    enum class ScanMode {
        BITMAP, URI
    }

    companion object {
        private const val EXT_DATA = "ext_data"
        @JvmStatic
        fun newInstance(mode: ScanMode): ResultDialog = ResultDialog().apply {
            arguments = bundleOf(
                EXT_DATA to mode
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                scanMode = it.getSerializable(EXT_DATA, ScanMode::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                scanMode = it.getSerializable(EXT_DATA) as ScanMode
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        binding = DialogResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnDialogResultClose.setOnClickListener {
                this@ResultDialog.dismiss()
            }
            btnDialogResultSave.setOnClickListener {
                if (btnDialogResultSave.isEnabled) {
                    calculation?.let { data ->
                        saveResult(data)
                    }
                }
            }
        }

        showLoader()
        if (scanMode == ScanMode.BITMAP) {
            subscribeToBitmap()
        } else if (scanMode == ScanMode.URI) {
            subscribeToUri()
        }
    }

    private fun showLoader() = with(binding) {
        boxDialogResult.hide()
        pbDialogResult.show()
    }

    private fun hideLoader() = with(binding) {
        pbDialogResult.hide()
        boxDialogResult.show()
    }

    private fun populateResultUI(data: Calculation) = with(binding) {
        val result = "Result: ${data.result}"
        tvDialogResultIn.text = data.input
        tvDialogResultOut.text = result
        btnDialogResultSave.isEnabled = true
    }

    private fun saveResult(data: Calculation) = lifecycleScope.launch {
        mainViewModel.insertCalculation(data).collect {
            when(it) {
                is Resource.Loading -> showLoader()
                is Resource.Completed -> {
                    eventListener?.invoke(scanMode, data, null)
                    this@ResultDialog.dismiss()
                }
                is Resource.Error -> {
                    eventListener?.invoke(scanMode, data, it.error)
                    this@ResultDialog.dismiss()
                }
                else -> Unit
            }
        }
    }

    /**
     * Subscribe to Bitmap value for Camera scan mode
     */
    private fun subscribeToBitmap() = lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            mainViewModel.bitmap.collect {
                it?.let { bmp ->
                    val img = FirebaseVisionImage.fromBitmap(bmp)
                    processImage(img)
                }
            }
        }
    }

    /**
     * Subscribe to Uri value for Gallery scan mode
     */
    private fun subscribeToUri() = lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            mainViewModel.imageUri.collect {
                it?.let { uri ->
                    val img = FirebaseVisionImage.fromFilePath(App.context, uri)
                    processImage(img)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun processImage(img: FirebaseVisionImage) {
        val ocr = FirebaseVision.getInstance().onDeviceTextRecognizer
        ocr.processImage(img).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                processOcrResult(task.result)
            } else {
                eventListener?.invoke(scanMode, null, task.exception?.message)
            }
        }
    }

    private fun processOcrResult(text: FirebaseVisionText) = lifecycleScope.launch {
        mainViewModel.processOcr(text).collect { resource ->
            when(resource) {
                is Resource.Completed -> {
                    hideLoader()
                    resource.data?.let { data ->
                        calculation = data
                        populateResultUI(data)
                    }
                }
                is Resource.Error -> {
                    eventListener?.invoke(scanMode, null, resource.error)
                    this@ResultDialog.dismiss()
                }
                else -> Unit
            }
        }
    }

    fun addDialogEvent(listener: ((mode: ScanMode, calculation: Calculation?, error: String?) -> Unit)) {
        eventListener = listener
    }

    override fun onResume() {
        super.onResume()
        setWidthPercent(90)
    }
}
