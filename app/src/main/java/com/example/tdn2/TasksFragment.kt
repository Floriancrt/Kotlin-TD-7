package com.example.tdn2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tdn2.network.Api
import kotlinx.android.synthetic.main.activity_tasks.*
import kotlinx.android.synthetic.main.fragment_tasks.*
import kotlinx.android.synthetic.main.fragment_tasks.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*

class TasksFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this).get(TasksViewModel::class.java)
    }
    val tasksAdapter = TasksAdapter()

    companion object {
        const val UNIQUE_REQUEST_CODE = 777
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)


        Glide.with(this).load("https://goo.gl/gEgYUd").into(avatar)

        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.adapter = tasksAdapter


        viewModel.taskListLiveData.observe(this, Observer { newList ->
            tasksAdapter.list = newList.orEmpty()

        })


        tasksAdapter.onDeleteClickListener = { task ->
            viewModel.deleteTask(task)
        }

        tasksAdapter.onEditClickListener = { task ->
            val intent = Intent(context, TaskActivity::class.java)
            intent.putExtra("editTask", task)
            startActivityForResult(intent, 1)
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTasks()




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UNIQUE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val task = data!!.getSerializableExtra(TaskActivity.TASK_KEY) as Task
                viewModel.addTask(task)
            }
        }
        else if (resultCode == 2) {
            val task = data!!.getSerializableExtra("task") as Task

            viewModel.editTask(task)

        }

    }

}



