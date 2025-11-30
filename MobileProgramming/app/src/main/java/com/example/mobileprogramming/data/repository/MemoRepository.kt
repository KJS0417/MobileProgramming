package com.example.mobileprogramming.data.repository

import android.content.Context
import android.net.Uri
import com.example.mobileprogramming.data.db.Memo
import com.example.mobileprogramming.data.db.MemoDao
import com.example.mobileprogramming.data.util.ImageStorageManager
import kotlinx.coroutines.flow.Flow

class MemoRepository(
    private val memoDao: MemoDao,
    private val context: Context // 이미지 저장을 위해 Context 필요
) {
    // ViewModel에서 호출할 함수들
    fun getMemo(id: Int): Flow<Memo?> = memoDao.getMemoById(id)

    fun getAllMemos(isSortedDesc: Boolean): Flow<List<Memo>> {
        return if (isSortedDesc) {
            memoDao.getAllMemosSortedByDateDesc()
        } else {
            memoDao.getAllMemosSortedByDateAsc()
        }
    }

    suspend fun insertMemo(title: String, content: String, imageUri: Uri?) {
        val imagePath = imageUri?.let {
            ImageStorageManager.saveImageToInternalStorage(context, it)
        }
        val memo = Memo(title = title, content = content, imagePath = imagePath)
        memoDao.insert(memo)
    }

    suspend fun updateMemo(memo: Memo, newImageUri: Uri?, isImageChanged: Boolean) {
        var newPath = memo.imagePath

        if (isImageChanged) {
            // 1. 기존 이미지 삭제
            ImageStorageManager.deleteImageFromInternalStorage(memo.imagePath)

            // 2. 새 이미지 저장 (null일 수도 있음 - 이미지 제거)
            newPath = newImageUri?.let {
                ImageStorageManager.saveImageToInternalStorage(context, it)
            }
        }

        val updatedMemo = memo.copy(imagePath = newPath)
        memoDao.update(updatedMemo)
    }

    suspend fun deleteMemo(memo: Memo) {
        // DB에서 삭제하기 전 연결된 이미지 파일 먼저 삭제
        ImageStorageManager.deleteImageFromInternalStorage(memo.imagePath)
        memoDao.delete(memo)
    }
}