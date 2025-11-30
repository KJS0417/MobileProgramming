package com.example.mobileprogramming.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow // LiveData 대신 Flow 사용 (더 유연함)

@Dao
interface MemoDao {
    // 코루틴 환경에서 실행되어야 하므로 suspend 키워드 사용
    @Insert
    suspend fun insert(memo: Memo)

    @Update
    suspend fun update(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)

    @Query("SELECT * FROM memo_table WHERE id = :id")
    fun getMemoById(id: Int): Flow<Memo?> // 단일 메모 가져오기

    // 정렬 쿼리 (Flow로 감싸면 데이터 변경 시 자동 업데이트)
    @Query("SELECT * FROM memo_table ORDER BY timestamp ASC")
    fun getAllMemosSortedByDateAsc(): Flow<List<Memo>>

    @Query("SELECT * FROM memo_table ORDER BY timestamp DESC")
    fun getAllMemosSortedByDateDesc(): Flow<List<Memo>>
}