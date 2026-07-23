package com.stafo.app.screens.tms.dataClass

import android.net.Uri

data class TaskAttachment(
    val id: Int? = null,
    val uri: Uri? = null,
    val isLocal: Boolean = true,
    val fileUrl: String? = null
)
