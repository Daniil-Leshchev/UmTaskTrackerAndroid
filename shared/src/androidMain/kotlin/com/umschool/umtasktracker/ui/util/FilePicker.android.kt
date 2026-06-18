package com.umschool.umtasktracker.ui.util

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.umschool.umtasktracker.domain.model.SelectedFile

@Composable
actual fun rememberFilePicker(
    onFilesSelected: (List<SelectedFile>) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        val files = uris.mapNotNull { uri ->
            try {
                val bytes = context.contentResolver
                    .openInputStream(uri)
                    ?.use { it.readBytes() }
                    ?: return@mapNotNull null
                val mimeType = context.contentResolver.getType(uri)
                    ?: "application/octet-stream"
                val name = resolveFileName(context.contentResolver, uri)
                SelectedFile(name = name, bytes = bytes, mimeType = mimeType)
            } catch (e: Exception) {
                null
            }
        }
        if (files.isNotEmpty()) onFilesSelected(files)
    }
    return {
        launcher.launch(arrayOf("image/jpeg", "image/png", "application/pdf"))
    }
}

private fun resolveFileName(resolver: ContentResolver, uri: Uri): String {
    resolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx != -1) return cursor.getString(idx)
        }
    }
    return uri.lastPathSegment ?: "file"
}
