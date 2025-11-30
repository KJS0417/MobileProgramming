package com.example.mobileprogramming.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileprogramming.R
import com.example.mobileprogramming.databinding.ActivityMainBinding
import com.example.mobileprogramming.ui.detail.MemoDetailActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MemoListViewModel by viewModels()
    private lateinit var memoAdapter: MemoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            return@setKeepOnScreenCondition viewModel.isLoading.value
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar) // 툴바 설정

        setupRecyclerView()
        setupFab()
        observeViewModel()
    }

    // 툴바에 메뉴(검색, 정렬) 추가
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu) // res/menu/main_menu.xml 필요

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText.orEmpty()) // 검색어 ViewModel에 전달
                return true
            }
        })
        return true
    }

    // 정렬 버튼 클릭 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                viewModel.toggleSortOrder() // 정렬 순서 변경
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        memoAdapter = MemoListAdapter { memo ->
            // 아이템 클릭 시
            val intent = Intent(this, MemoDetailActivity::class.java).apply {
                putExtra("MEMO_ID", memo.id)
            }
            startActivity(intent)
        }
        binding.recyclerView.apply {
            adapter = memoAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupFab() {
        binding.fabAddMemo.setOnClickListener {
            // 새 메모
            val intent = Intent(this, MemoDetailActivity::class.java).apply {
                putExtra("MEMO_ID", -1) // -1은 새 메모를 의미
            }
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            // viewModel의 memoList(StateFlow)를 관찰
            viewModel.memoList.collectLatest { memos ->
                memoAdapter.submitList(memos) // ListAdapter에 새 리스트 제출
            }
        }
    }
}