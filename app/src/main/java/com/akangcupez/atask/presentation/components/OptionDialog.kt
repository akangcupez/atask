package com.akangcupez.atask.presentation.components

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akangcupez.atask.databinding.DialogOptionBinding
import com.akangcupez.atask.utils.Const

class OptionDialog : BaseDialog() {

    private lateinit var binding: DialogOptionBinding
    private var onDismissEvent: ((dialog: OptionDialog) -> Unit)? = null

    companion object {
        @JvmStatic
        fun newInstance(onDismissListener: ((dialog: OptionDialog) -> Unit)): OptionDialog = OptionDialog().apply {
            onDismissEvent = onDismissListener
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
        binding = DialogOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val selectedStorageType = sessions.storageType
            rbDialogOptionCache.isChecked = selectedStorageType == Const.StorageType.CACHE
            rbDialogOptionDb.isChecked = selectedStorageType == Const.StorageType.DB

            rgDialogOption.setOnCheckedChangeListener { _, checkedId ->
                when(checkedId) {
                    rbDialogOptionCache.id -> setStorageType(Const.StorageType.CACHE)
                    rbDialogOptionDb.id -> setStorageType(Const.StorageType.DB)
                    else -> Unit
                }
            }

            btnDialogOptionClose.setOnClickListener {
                this@OptionDialog.dismiss()
                onDismissEvent?.invoke(this@OptionDialog)
            }
        }
    }

    private fun setStorageType(storageType: Const.StorageType) {
        sessions.storageType = storageType
    }

    override fun onResume() {
        super.onResume()
        setWidthPercent(90)
    }
}
