package com.umschool.umtasktracker.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umschool.umtasktracker.domain.model.CuratorTask
import com.umschool.umtasktracker.domain.model.SelectedFile
import com.umschool.umtasktracker.domain.model.TaskStatus
import com.umschool.umtasktracker.presentation.curator.CuratorTasksViewModel
import com.umschool.umtasktracker.presentation.curator.SubmitState
import com.umschool.umtasktracker.presentation.util.DateFormatter
import com.umschool.umtasktracker.ui.theme.CardBackground
import com.umschool.umtasktracker.ui.theme.TextDark
import com.umschool.umtasktracker.ui.theme.TextHint
import com.umschool.umtasktracker.ui.theme.UmOrange
import com.umschool.umtasktracker.ui.util.rememberFilePicker

private val SuccessGreen = Color(0xFF22C55E)
private val BadgeGreenBg = Color(0xFFE8F5E9)
private val BadgeGreenFg = Color(0xFF2E7D32)
private val BadgeRedBg = Color(0xFFFFEBEE)
private val BadgeRedFg = Color(0xFFC62828)
private val PdfRed = Color(0xFFE53935)
private val ImageBlue = Color(0xFF1565C0)
private val FieldBorder = Color(0xFFE2E5EA)

@Composable
fun DetailedCuratorTaskScreen(
    task: CuratorTask,
    viewModel: CuratorTasksViewModel,
    onBack: () -> Unit,
) {
    val report by viewModel.report.collectAsState()
    val reportLoading by viewModel.reportLoading.collectAsState()
    val reportText by viewModel.reportText.collectAsState()
    val selectedFiles by viewModel.selectedFiles.collectAsState()
    val submitState by viewModel.submitState.collectAsState()

    LaunchedEffect(task.id) { viewModel.loadReport(task.id) }
    DisposableEffect(task.id) {
        onDispose { viewModel.resetReportDraft() }
    }

    val launchFilePicker = rememberFilePicker { files ->
        viewModel.addFiles(files)
    }

    val isSubmitted = report?.isSubmitted == true ||
        submitState == SubmitState.Success ||
        task.status == TaskStatus.COMPLETED ||
        task.status == TaskStatus.COMPLETED_LATE

    LaunchedEffect(submitState) {
        if (submitState == SubmitState.Success) onBack()
    }

    Scaffold(containerColor = CardBackground) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        text = task.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(Modifier.height(20.dp))

                    SectionLabel("Описание")
                    Spacer(Modifier.height(4.dp))
                    Text(text = task.description, fontSize = 14.sp, color = TextHint)

                    Spacer(Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel("Дедлайн:")
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = DateFormatter.formatMoscow(task.deadline) ?: task.deadline,
                                fontSize = 14.sp,
                                color = TextDark,
                            )
                        }
                        ReportStatusBadge(isSubmitted = isSubmitted)
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Column(modifier = Modifier.weight(1f)) {
                            SectionLabel("Формат отчёта:")
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = task.reportTemplate,
                                fontSize = 14.sp,
                                color = TextHint
                            )
                        }
                        if (!isSubmitted) {
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(UmOrange, RoundedCornerShape(8.dp))
                                    .clickable { launchFilePicker() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "Прикрепить файл",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    when {
                        reportLoading -> Box(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = UmOrange) }

                        isSubmitted -> SubmittedView(
                            text = report?.reportText,
                            fileUrls = report?.fileUrls.orEmpty()
                        )

                        else -> EditView(
                            text = reportText,
                            files = selectedFiles,
                            onTextChange = viewModel::onReportTextChange,
                            onRemoveFile = viewModel::removeFile,
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                        ) { Text("Закрыть", color = TextDark) }

                        if (!isSubmitted) {
                            val isLoading = submitState is SubmitState.Loading
                            val enabled = !isLoading &&
                                (reportText.isNotBlank() || selectedFiles.isNotEmpty())
                            Button(
                                onClick = { viewModel.submitReport(task.id) },
                                enabled = enabled,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SuccessGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text("Отправить")
                                }
                            }
                        }
                    }

                    val errorMsg = (submitState as? SubmitState.Error)?.message
                    if (errorMsg != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextDark
    )
}

@Composable
private fun ReportStatusBadge(isSubmitted: Boolean) {
    val text: String
    val icon: ImageVector
    val bg: Color
    val fg: Color
    if (isSubmitted) {
        text = "Завершено"
        icon = Icons.Default.CheckCircle
        bg = BadgeGreenBg
        fg = BadgeGreenFg
    } else {
        text = "Не начато"
        icon = Icons.Default.Cancel
        bg = BadgeRedBg
        fg = BadgeRedFg
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(text, color = fg, fontSize = 12.sp)
        }
    }
}

@Composable
private fun EditView(
    text: String,
    files: List<SelectedFile>,
    onTextChange: (String) -> Unit,
    onRemoveFile: (Int) -> Unit,
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = { Text("Введите текст", color = TextHint) },
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 120.dp),
        maxLines = 10,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = UmOrange.copy(alpha = 0.6f),
            unfocusedBorderColor = FieldBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = UmOrange
        )
    )

    if (files.isNotEmpty()) {
        Spacer(Modifier.height(12.dp))
        files.forEachIndexed { index, file ->
            AttachedFileItem(file = file, onRemove = { onRemoveFile(index) })
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SubmittedView(text: String?, fileUrls: List<String>) {
    if (!text.isNullOrBlank()) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF4F5F7),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                color = TextDark,
                modifier = Modifier.padding(12.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
    }
    if (fileUrls.isNotEmpty()) {
        fileUrls.forEach { url ->
            SubmittedFileItem(url = url)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AttachedFileItem(file: SelectedFile, onRemove: () -> Unit) {
    val icon = when {
        file.mimeType == "application/pdf" -> Icons.Default.PictureAsPdf
        file.mimeType.startsWith("image/") -> Icons.Default.Image
        else -> Icons.Default.AttachFile
    }
    val tint = if (file.mimeType == "application/pdf") PdfRed else ImageBlue
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(36.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = file.name,
            fontSize = 12.sp,
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Удалить",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SubmittedFileItem(url: String) {
    val isPdf = url.contains(".pdf", ignoreCase = true)
    val icon = if (isPdf) Icons.Default.PictureAsPdf else Icons.Default.Image
    val tint = if (isPdf) PdfRed else ImageBlue
    val uriHandler = LocalUriHandler.current
    val filename = url.substringAfterLast("/").substringBefore("?").ifBlank { "Файл" }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { runCatching { uriHandler.openUri(url) } }
    ) {
        Box {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(36.dp))
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = BadgeGreenFg,
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.BottomEnd)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = filename,
            fontSize = 12.sp,
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}
