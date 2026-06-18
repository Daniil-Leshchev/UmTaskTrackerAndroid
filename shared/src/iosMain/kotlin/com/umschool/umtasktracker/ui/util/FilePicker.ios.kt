package com.umschool.umtasktracker.ui.util

import androidx.compose.runtime.Composable
import com.umschool.umtasktracker.domain.model.SelectedFile

@Composable
actual fun rememberFilePicker(
    onFilesSelected: (List<SelectedFile>) -> Unit
): () -> Unit = { }
