package com.codermonkeys.trelloclone.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codermonkeys.trelloclone.R
import com.codermonkeys.trelloclone.activities.TaskListActivity
import com.codermonkeys.trelloclone.databinding.ItemTaskBinding
import com.codermonkeys.trelloclone.models.Task
import com.sdsmdg.tastytoast.TastyToast

class TaskListItemAdapter(
    private val context: Context,
    private var list: ArrayList<Task>
) : RecyclerView.Adapter<TaskListItemAdapter.MyViewHolder>() {

    companion object {
        private const val TAG = "TaskListItemAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(context), parent, false)
        val layoutParams = LinearLayout.LayoutParams((parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDp().toPx()), 0, (40.toDp().toPx()), 0)
        binding.root.layoutParams = layoutParams
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        with(holder) {
            if (position == list.size - 1) {
                binding.tvAddTaskList.visibility = View.VISIBLE
                binding.llTaskItem.visibility = View.GONE
            } else {
                binding.tvAddTaskList.visibility = View.GONE
                binding.llTaskItem.visibility = View.VISIBLE
            }

            binding.tvTaskListTitle.text = model.title
            binding.tvAddTaskList.setOnClickListener {
                binding.tvAddTaskList.visibility = View.GONE
                binding.cvAddTaskListName.visibility = View.VISIBLE
            }

            binding.ibCloseListName.setOnClickListener {
                binding.tvAddTaskList.visibility = View.VISIBLE
                binding.cvAddTaskListName.visibility = View.GONE
            }

            binding.ibDoneListName.setOnClickListener {
                val listName = binding.etTaskListName.text.toString()
                Log.i(TAG, "onBindViewHolder: $listName")
                if(listName.isNotEmpty()) {
                    if(context is TaskListActivity) {
                        context.createTaskList(listName)
                    }
                } else {
                    TastyToast.makeText(context, "Task Name Required!!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show()
                }
            }

            binding.ibEditListName.setOnClickListener {
                binding.etEditTaskListName.setText(model.title)
                binding.llTitleView.visibility = View.GONE
                binding.cvEditTaskListName.visibility = View.VISIBLE
            }

            binding.ibCloseEditableView.setOnClickListener {
                binding.llTitleView.visibility = View.VISIBLE
                binding.cvEditTaskListName.visibility = View.GONE
            }

            binding.ibDoneEditListName.setOnClickListener {
                val listName = binding.etEditTaskListName.text.toString()
                if(listName.isNotEmpty()) {
                    if(context is TaskListActivity) {
                        context.updateTaskList(position, listName, model)
                    }
                } else {
                    TastyToast.makeText(context, "Task Name Required!!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show()
                }
            }

            binding.ibDeleteList.setOnClickListener {
                alertDialogForDeleteList(position, model.title)
            }
        }

    }

    override fun getItemCount() = list.size

    inner class MyViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure want to delete $title.")
        builder.setIcon(R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            if(context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("No") {dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}