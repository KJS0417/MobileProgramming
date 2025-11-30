package com.example.mobileprogramming.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo_table")
data class Memo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val imagePath: String?, // 이미지의 내부 저장소 경로
    val timestamp: Long = System.currentTimeMillis() // 정렬을 위한 작성 시간
)