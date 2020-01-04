package com.example.androidcodelab.network

import android.util.Log
import com.example.androidcodelab.Task

class TasksRepository {

        private val tasksService = Api.tasksService

        suspend fun loadTasks(): List<Task>? {
            val tasksResponse = tasksService.getTasks()
            Log.e("loadTasks", tasksResponse.toString())
            return if (tasksResponse.isSuccessful) tasksResponse.body()!! else null
        }

        suspend fun deleteTask(id: String): Boolean {
            val taskResponse = tasksService.deleteTask(id)
            Log.e("deleteTask", taskResponse.toString())
            return taskResponse.isSuccessful
        }
        suspend fun editTask(task: Task): Task? {
            val taskResponse = tasksService.updateTask(task)
            Log.e("editTasks", taskResponse.toString())
            return if (taskResponse.isSuccessful) taskResponse.body()!! else null
        }

        suspend fun createTask(task: Task): Task? {
            val taskResponse = tasksService.createTask(task)
            Log.e("createTask", taskResponse.toString())
            return if (taskResponse.isSuccessful) taskResponse.body()!! else null
        }





}