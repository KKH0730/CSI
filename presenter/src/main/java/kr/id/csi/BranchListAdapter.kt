package kr.id.csi

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.id.csi.databinding.BranchItemBinding
import kr.id.data.model.BranchData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BranchListAdapter(private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<BranchListAdapter.BranchListViewHolder>() {
    private var branchList = ArrayList<BranchData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchListViewHolder {
        val binding = DataBindingUtil.inflate<BranchItemBinding>(LayoutInflater.from(parent.context), R.layout.branch_item, parent, false)
        return BranchListViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: BranchListViewHolder, position: Int) {
        holder.setItem(branchList[position])
    }

    override fun getItemCount(): Int {
        return branchList.size
    }

    fun setBranchList(branchList : ArrayList<BranchData>){
        this.branchList = branchList
    }

    class BranchListViewHolder(private val binding : BranchItemBinding, private val onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {

        fun setItem(data : BranchData){
            binding.txtBranch.text = data.modify_name

            val time = data.created_at.toLong()
            var date = Date(time)
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
            binding.txtDate.text = "수정됨 ${formatter.format(date)}"
            binding.topView.setOnClickListener { onItemClickListener.onItemClicked(data.branch, data.modify_name, data.created_at) }
        }
    }
}