package com.example.mobileprogramming.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobileprogramming.data.db.Memo
import com.example.mobileprogramming.databinding.ListItemMemoBinding // 뷰 바인딩
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MemoListAdapter(
    private val onItemClick: (Memo) -> Unit // 아이템 클릭 리스너
) : ListAdapter<Memo, MemoListAdapter.MemoViewHolder>(MemoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val binding = ListItemMemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemoViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MemoViewHolder(
        private val binding: ListItemMemoBinding,
        private val onItemClick: (Memo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

        fun bind(memo: Memo) {
            binding.itemTvTitle.text = memo.title
            binding.itemTvContentSnippet.text = memo.content
            binding.itemTvDate.text = dateFormat.format(Date(memo.timestamp))

            if (memo.imagePath != null) {
                binding.itemIvImage.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(memo.imagePath)
                    .into(binding.itemIvImage)
            } else {
                binding.itemIvImage.visibility = View.GONE
            }

            // 아이템 클릭 시 수정/삭제 화면으로 이동
            itemView.setOnClickListener {
                onItemClick(memo)
            }
        }
    }
}

// ListAdapter가 리스트 변경을 감지하는 방식
class MemoDiffCallback : DiffUtil.ItemCallback<Memo>() {
    override fun areItemsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Memo, newItem: Memo): Boolean {
        return oldItem == newItem
    }
}