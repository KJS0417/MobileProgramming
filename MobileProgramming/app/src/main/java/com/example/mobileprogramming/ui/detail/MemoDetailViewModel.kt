package com.example.mobileprogramming.ui.detail

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileprogramming.data.db.Memo
import com.example.mobileprogramming.data.db.MemoDatabase
import com.example.mobileprogramming.data.repository.MemoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// Application을 상속받아 Context를 Repository에 전달
class MemoDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MemoRepository

    // 현재 선택된 이미지 Uri (UI에서 변경)
    var currentImageUri: Uri? = null
    // 이미지가 변경되었는지 (삭제 포함)
    var isImageChanged = false

    init {
        val memoDao = MemoDatabase.getDatabase(application).memoDao()
        repository = MemoRepository(memoDao, application.applicationContext)
    }

    fun getMemo(id: Int): Flow<Memo?> {
        return repository.getMemo(id)
    }

    fun saveMemo(id: Int, title: String, content: String) {
        viewModelScope.launch {
            if (id == -1) { // 새 메모
                repository.insertMemo(title, content, currentImageUri)
            } else { // 기존 메모 수정
                val memoToUpdate = Memo(id = id, title = title, content = content, imagePath = null)
            }
        }
    }

    // (Activity에서 원본 Memo 객체를 받아오는 것을 추천)
    fun updateMemo(originalMemo: Memo, newTitle: String, newContent: String) {
        viewModelScope.launch {
            val updatedMemo = originalMemo.copy(title = newTitle, content = newContent)
            repository.updateMemo(updatedMemo, currentImageUri, isImageChanged)
        }
    }

    fun deleteMemo(memo: Memo) {
        viewModelScope.launch {
            repository.deleteMemo(memo)
        }
    }
}