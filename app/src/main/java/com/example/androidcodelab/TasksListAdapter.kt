package com.example.androidcodelab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.*
import kotlin.properties.Delegates

class TasksListAdapter() : RecyclerView.Adapter<TasksListAdapter.TaskViewHolder>() {

    internal var tasks: List<Task> by Delegates.observable(emptyList()) {
            _, _, _ -> notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun getItemCount(): Int{
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    var onDeleteClickListener: ((Task) -> Unit)? = null

    var onEditClickListener: ((Task) -> Unit)? = null


    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(task: Task) {
            // Afficher les données et relier les listeners
            itemView.task_title.text = task.title
            itemView.task_description.text = task.description
            itemView.task_delete.setOnClickListener{
                onDeleteClickListener?.invoke(task)
            }

            itemView.task_edit.setOnClickListener{
                onEditClickListener?.invoke(task)
            }

        }
    }



}
