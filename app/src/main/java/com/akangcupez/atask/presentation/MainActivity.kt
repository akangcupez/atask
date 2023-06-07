package com.akangcupez.atask.presentation

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.akangcupez.atask.R
import com.akangcupez.atask.databinding.ActivityMainBinding
import com.akangcupez.atask.presentation.adapter.MainAdapter
import com.akangcupez.atask.presentation.components.OptionDialog
import com.akangcupez.atask.presentation.components.ResultDialog
import com.akangcupez.atask.presentation.viewmodels.MainViewModel
import com.akangcupez.atask.utils.Const
import com.akangcupez.atask.utils.PermissionUtil
import com.akangcupez.atask.utils.Resource
import com.akangcupez.atask.utils.SourceMode
import com.akangcupez.atask.utils.useCamera
import com.akangcupez.atask.utils.useGallery
import com.sangcomz.fishbun.FishBun
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var mAdapter: MainAdapter

    /**
     * Camera Activity Launcher, when receive data then proceed with OCR
     */
    @Suppress("DEPRECATION")
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.let { intent ->
                    try {
                        val bmp = intent.extras?.get("data") as Bitmap
                        val cpy = bmp.copy(Bitmap.Config.ARGB_8888, true)
                        mainViewModel.setBitmap(cpy)
                        showResultDialog(ResultDialog.ScanMode.BITMAP)
                    } catch (e: Exception) {
                        showError(e.message)
                    }
                }
            }
        }

    /**
     * Camera Permission Check Activity Launcher
     */
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                useCamera(this@MainActivity, cameraLauncher)
            } else {
                PermissionUtil.showAlert(this@MainActivity, Const.DENIED_CAMERA_MESSAGE)
            }
        }

    /**
     * PhotoPicker will be used whenever available
     */
    private val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            mainViewModel.setImageUri(it)
            showResultDialog(ResultDialog.ScanMode.URI)
        }
    }

    /**
     * Fishbun library will be used for android OS not supporting android's PhotoPicker
     */
    private val fisbunLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.let { data ->
                    val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        data.getParcelableArrayListExtra(FishBun.INTENT_PATH, Uri::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        data.getParcelableArrayListExtra(FishBun.INTENT_PATH)
                    }

                    list?.let { uriList ->
                        if (uriList.isNotEmpty()) {
                            mainViewModel.setImageUri(uriList[0])
                            showResultDialog(ResultDialog.ScanMode.URI)
                        }
                    }
                }
            }
        }

    /**
     * File Permission Check Activity Launcher
     */
    private val filePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                useGallery(this@MainActivity, photoPickerLauncher, fisbunLauncher)
            } else {
                PermissionUtil.showAlert(this@MainActivity, Const.DENIED_STORAGE_MESSAGE)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)
        //check permission on add button's click event
        binding.fabAdd.setOnClickListener {
            when (SourceMode.getPermissionType()) {
                Const.PermissionType.CAMERA -> {
                    mainViewModel.setBitmap(null) //reset bitmap state
                    initCamera()
                }
                Const.PermissionType.STORAGE -> initGallery()
            }
        }
        mAdapter = MainAdapter()
        setupRecyclerView()
    }

    private fun setupRecyclerView() = with(binding) {
        val lm = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        rvMain.apply {
            layoutManager = lm
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
        subscribeToCalculationList()
        mainViewModel.getCalculations()
    }

    /**
     * Initialize camera feature with permission checking.
     * If has permission granted, then proceed to utilize it, otherwise
     * show alert with redirection to android settings screen
     */
    private fun initCamera() {
        PermissionUtil.checkCameraPermission(
            this@MainActivity,
            cameraPermissionLauncher
        ) { isGranted ->
            if (isGranted) {
                useCamera(this@MainActivity, cameraLauncher)
            } else {
                PermissionUtil.showAlert(this@MainActivity, Const.DENIED_CAMERA_MESSAGE)
            }
        }
    }

    /**
     * Initialize gallery feature with permission checking.
     * If has permission granted, then proceed to utilize it, otherwise
     * show alert with redirection to android settings screen
     */
    private fun initGallery() {
        PermissionUtil.checkReadStoragePermission(
            this@MainActivity,
            filePermissionLauncher
        ) { isGranted ->
            if (isGranted) {
                useGallery(this@MainActivity, photoPickerLauncher, fisbunLauncher)
            } else {
                PermissionUtil.showAlert(this@MainActivity, Const.DENIED_STORAGE_MESSAGE)
            }
        }
    }

    private fun showResultDialog(mode: ResultDialog.ScanMode) {
        val dialog = ResultDialog.newInstance(mode)
        dialog.addDialogEvent { _, calculation, error ->
            if (calculation != null && error == null) {
                mainViewModel.getCalculations()
            } else {
                error?.let { showError(it) }
            }
        }
        dialog.show(supportFragmentManager, dialog.tag)
    }

    private fun showOptionDialog() {
        val dialog = OptionDialog.newInstance {
            mainViewModel.getCalculations()
        }
        dialog.show(supportFragmentManager, dialog.tag)
    }

    private fun showError(s: String?) {
        Toast.makeText(this@MainActivity, s ?: "Unknown Error", Toast.LENGTH_SHORT).show()
    }

    private fun subscribeToCalculationList() = lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
            mainViewModel.calculationList.collect { resource ->
                when(resource) {
                    is Resource.Completed -> {
                        resource.data?.let { mAdapter.submitList(it) }
                    }
                    is Resource.Empty, is Resource.Error -> {
                        mAdapter.submitList(null)
                        resource.error?.let { showError(it) }
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.act_config) {
            showOptionDialog()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
