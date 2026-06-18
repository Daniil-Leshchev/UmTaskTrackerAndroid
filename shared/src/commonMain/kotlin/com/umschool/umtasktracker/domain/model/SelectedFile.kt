package com.umschool.umtasktracker.domain.model

data class SelectedFile(
    val name: String,
    val bytes: ByteArray,
    val mimeType: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SelectedFile) return false
        return name == other.name && mimeType == other.mimeType && bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int =
        31 * (31 * name.hashCode() + mimeType.hashCode()) + bytes.contentHashCode()
}
