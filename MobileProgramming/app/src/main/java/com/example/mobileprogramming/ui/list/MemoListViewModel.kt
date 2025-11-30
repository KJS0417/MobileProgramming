package com.example.mobileprogramming.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileprogramming.data.db.Memo
import com.example.mobileprogramming.data.db.MemoDatabase
import com.example.mobileprogramming.data.repository.MemoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MemoListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MemoRepository

    // 로딩 상태 정의 (스플래시 화면 제어용)
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 정렬 상태 (true = DESC, false = ASC)
    private val _isSortedDesc = MutableStateFlow(true)

    // 검색어
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // 최종 UI에 표시될 메모 리스트 (StateFlow)
    val memoList: StateFlow<List<Memo>>

    init {
        val memoDao = MemoDatabase.getDatabase(application).memoDao()
        repository = MemoRepository(memoDao, application.applicationContext)

        viewModelScope.launch {
            // 1초 지연을 사용
            // 이 지연 시간(또는 DB 로딩 시간) 동안 스플래시 화면이 유지됨
            kotlinx.coroutines.delay(1000)

            // 로딩 완료: 이 시점에 스플래시 화면이 사라짐
            _isLoading.value = false
        }

        // 정렬 상태(_isSortedDesc)가 바뀔 때마다 flatMapLatest가
        // repository의 다른 Flow(ASC/DESC)를 구독하도록 전환합니다.
        memoList = _isSortedDesc.flatMapLatest { isDesc ->
            repository.getAllMemos(isDesc)
        }
            // 그리고 그 결과를 _searchQuery와 combine(조합)하여
            // 검색어 필터링을 적용합니다.
            .combine(_searchQuery) { memos, query ->
                if (query.isBlank()) {
                    memos // 검색어가 없으면 전체 리스트
                } else {
                    memos.filter { it.title.contains(query, ignoreCase = true) } // 검색
                }
            }
            // ViewModel이 살아있는 동안 Flow를 유지 (stateIn)
            .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())
    }

    // 검색어 변경 시 호출
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // 정렬 버튼 클릭 시 호출
    fun toggleSortOrder() {
        _isSortedDesc.value = !_isSortedDesc.value
    }
}