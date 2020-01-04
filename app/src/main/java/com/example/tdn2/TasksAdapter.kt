package com.example.tdn2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.*
import kotlin.properties.Delegates



class TasksAdapter() : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    var onDeleteClickListener : (Task) -> Unit = {}
    var onEditClickListener: (Task) -> Unit = {}

    var list: List<Task> by Delegates.observable(emptyList()) {
            _, _, _ -> notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        val itemTasks = list[position]
        holder.bind(itemTasks)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(task: Task) {
            itemView.task_title.text = task.title
            itemView.task_description.text = task.description
            itemView.button_delete.setOnClickListener {onDeleteClickListener.invoke(task)}
            itemView.findViewById<Button>(R.id.button_edit).setOnClickListener{
                onEditClickListener.invoke(task)
            }
        }

    }


}




