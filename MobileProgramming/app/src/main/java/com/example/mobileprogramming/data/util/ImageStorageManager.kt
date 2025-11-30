package com.example.mobileprogramming.data.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

// 갤러리 URI를 앱 내부 저장소로 복사하고, 그 경로를 반환합니다.
object ImageStorageManager {

    // 이미지 저장
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName) // 앱 고유 디렉토리
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath // 저장된 파일의 절대 경로 반환
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 이미지 삭제
    fun deleteImageFromInternalStorage(path: String?): Boolean {
        if (path == null) return false
        return try {
            val file = File(path)
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}