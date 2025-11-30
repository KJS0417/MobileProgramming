package com.example.mobileprogramming.ui.detail

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mobileprogramming.data.db.Memo
import com.example.mobileprogramming.databinding.ActivityMemoDetailBinding // 뷰 바인딩 사용 권장
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MemoDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemoDetailBinding
    private val viewModel: MemoDetailViewModel by viewModels()

    private var currentMemoId: Int = -1
    private var originalMemo: Memo? = null

    // Activity Result API (갤러리 실행)
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.currentImageUri = it
            viewModel.isImageChanged = true
            Glide.with(this).load(it).into(binding.ivImage)
            binding.ivImage.visibility = View.VISIBLE
            binding.btnRemoveImage.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentMemoId = intent.getIntExtra("MEMO_ID", -1)

        if (currentMemoId == -1) {
            // 새 메모
            binding.btnDelete.visibility = View.GONE
        } else {
            // 기존 메모 수정
            binding.btnDelete.visibility = View.VISIBLE
            loadMemoData()
        }

        setupClickListeners()
    }

    private fun loadMemoData() {
        lifecycleScope.launch {
            viewModel.getMemo(currentMemoId).collectLatest { memo ->
                memo?.let {
                    originalMemo = it // 원본 데이터 저장 (수정/삭제 시 사용)
                    binding.etTitle.setText(it.title)
                    binding.etContent.setText(it.content)
                    if (it.imagePath != null) {
                        viewModel.isImageChanged = false // 아직 변경 안됨
                        viewModel.currentImageUri = Uri.parse(it.imagePath) // (File Path를 Uri로)
                        Glide.with(this@MemoDetailActivity)
                            .load(it.imagePath)
                            .into(binding.ivImage)
                        binding.ivImage.visibility = View.VISIBLE
                        binding.btnRemoveImage.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnAddImage.setOnClickListener {
            imagePickerLauncher.launch("image/*") // 갤러리 열기
        }

        binding.btnRemoveImage.setOnClickListener {
            viewModel.currentImageUri = null
            viewModel.isImageChanged = true
            binding.ivImage.visibility = View.GONE
            binding.btnRemoveImage.visibility = View.GONE
            Glide.with(this).clear(binding.ivImage)
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val content = binding.etContent.text.toString()

            if (title.isBlank()) {
                Toast.makeText(this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentMemoId == -1) {
                // 새 메모 저장
                viewModel.saveMemo(-1, title, content)
            } else {
                // 기존 메모 업데이트
                originalMemo?.let {
                    viewModel.updateMemo(it, title, content)
                }
            }
            finish() // 액티비티 종료
        }

        binding.btnDelete.setOnClickListener {
            originalMemo?.let {
                viewModel.deleteMemo(it)
                finish()
            }
        }
    }
}