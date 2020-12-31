package com.codermonkeys.trelloclone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.codermonkeys.trelloclone.R
import com.codermonkeys.trelloclone.adapters.TaskListItemAdapter
import com.codermonkeys.trelloclone.databinding.ActivityTaskListBinding
import com.codermonkeys.trelloclone.firebase.FirestoreClass
import com.codermonkeys.trelloclone.models.Board
import com.codermonkeys.trelloclone.models.Task
import com.codermonkeys.trelloclone.utils.Constants.DOCUMENT_ID


class TaskListActivity : BaseActivity() {

    private lateinit var binding: ActivityTaskListBinding
    private lateinit var mBoardDetails: Board


    companion object {
        private const val TAG = "TaskListActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var boardDocumentId = ""
        if (intent.hasExtra(DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, boardDocumentId)
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarTaskListActivity)
        val actionBar = supportActionBar
        actionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            it.title = mBoardDetails.name
        }
        binding.toolbarTaskListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun boardDetails(board: Board) {

        mBoardDetails = board

        hideProgressDialog()
        setUpActionBar()

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        val adapter = TaskListItemAdapter(this, board.taskList)

        binding.rvTaskList.apply {
            layoutManager =
                LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            this.adapter = adapter
        }
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, mBoardDetails.documentId)
    }

    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun updateTaskList(position: Int, listName: String, model: Task) {
        val task = Task(listName, model.createdBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun deleteTaskList(position: Int) {
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }
}