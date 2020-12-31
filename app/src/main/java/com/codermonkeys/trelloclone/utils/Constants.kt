package com.codermonkeys.trelloclone.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap


object Constants {
    const val USERS = "users"
    const val BOARDS = "boards"

    const val IMAGE = "image"
    const val NAME = "name"
    const val MOBILE = "mobile"
    const val ASSIGNED_TO = "assignedTo"
    const val DOCUMENT_ID = "documentId"
    const val TASK_LIST = "taskList"

    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}