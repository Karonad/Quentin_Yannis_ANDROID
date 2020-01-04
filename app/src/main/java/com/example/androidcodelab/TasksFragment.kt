package com.example.androidcodelab

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.androidcodelab.network.Api
import com.example.androidcodelab.network.TasksViewModel
import kotlinx.android.synthetic.main.fragment_tasks.*
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {

    private val taskViewModel by lazy {
        ViewModelProvider(this).get(TasksViewModel::class.java)
    }

    private val adapter = TasksListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tasks_recycler_view.adapter = adapter
        tasks_recycler_view.layoutManager = LinearLayoutManager(activity)

        header_image_view.setOnClickListener(){
            val intent = Intent(activity, UserInfoActivity::class.java)
            startActivity(intent)
        }
        taskViewModel.taskMap.observe(this, Observer { newMap ->
            adapter.tasks = newMap?.values?.toList().orEmpty()
        })

        adapter.onDeleteClickListener = { task ->
            taskViewModel.deleteTask(task)
        }
        floatingActionButton.setOnClickListener {
            val intent = Intent(activity, TaskActivity::class.java)
            startActivityForResult(intent, ADD_REQUEST_CODE)
        }

        adapter.onEditClickListener = { task ->
            val intent = Intent(activity, TaskActivity::class.java)
            intent.putExtra(TASK, task)
            startActivityForResult(intent, EDIT_REQUEST_CODE)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val reply = data!!.getSerializableExtra(TaskActivity.EXTRA_REPLY) as Task
                taskViewModel.createTask(reply )

            }
        } else if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val reply = data!!.getSerializableExtra(TaskActivity.EXTRA_REPLY) as Task
                taskViewModel.updateTask(reply)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //outState.putSerializable("task_list", taskMap.toTypedArray())
    }


    override fun onResume() {
        super.onResume()
        taskViewModel.loadTasks()
        val glide = Glide.with(this)
        lifecycleScope.launch {
            val userInfo = Api.userService.getInfo().body()!!
            header_text_view.text = "${userInfo.firstName} ${userInfo.lastName}"
            if (userInfo.avatar != null) {
                glide
                    .load(userInfo.avatar)
                    .circleCrop()
                    .into(header_image_view)
            } else {
                glide
                    .load("https://43ch47qsavx2jcvnr30057vk-wpengine.netdna-ssl.com/wp-content/uploads/2016/03/Daphnis_Icon-1.png")
                    .circleCrop()
                    .into(header_image_view)
            }
        }
    }


    companion object {
        const val ADD_REQUEST_CODE = 907
        const val EDIT_REQUEST_CODE = 709
        const val TASK = "task"
    }


}