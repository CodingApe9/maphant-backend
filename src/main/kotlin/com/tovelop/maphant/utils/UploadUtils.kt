package com.tovelop.maphant.utils

import com.amazonaws.util.StringUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Component
import java.util.*


@Component
class UploadUtils {
    private val CONTENT_TYPES = arrayOf("png", "jpeg", "jpg")

    fun isImageFile(filename: String): Boolean {
        if (StringUtils.isNullOrEmpty(filename)) {
            return false
        }
        val extension: String =
            FilenameUtils.getExtension(filename.lowercase(Locale.getDefault()))
        return ArrayList(Arrays.asList(*CONTENT_TYPES)).contains(extension)
    }

    fun isNotImageFile(filename: String): Boolean {
        return !isImageFile(filename)
    }
}