package com.akangcupez.atask.utils

object SourceMode : ISourceMode {

    override fun getPermissionType(): Const.PermissionType {
        return Const.PermissionType.STORAGE
    }
}
