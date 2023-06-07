package com.akangcupez.atask.utils

object Const {

    const val DENIED_CAMERA_MESSAGE = "You should allow camera permission to use this feature"
    const val DENIED_STORAGE_MESSAGE = "You should allow file permission to use this feature"

    enum class StorageType {
        CACHE, DB
    }

    enum class PermissionType {
        STORAGE, CAMERA
    }
}