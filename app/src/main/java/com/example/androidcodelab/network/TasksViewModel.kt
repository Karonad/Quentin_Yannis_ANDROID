package com.example.androidcodelab.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcodelab.Task
import kotlinx.coroutines.launch

class TasksViewModel : ViewModel() {
    private val repository = Api.tasksRepository
    private val _taskMap = MutableLiveData<Map<String, Task>>()
    val taskMap: LiveData<Map<String, Task>>
        get() = _taskMap

    fun loadTasks() {
        viewModelScope.launch {
            val taskList = repository.loadTasks()
            val newMap = taskList?.map { it.id to it }?.toMap()
            _taskMap.postValue(newMap)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            if(repository.deleteTask(task.id)) {
                mutateMap { it.remove(task.id) }
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.editTask(task)
            mutateMap { it.replace(task.id, task) }
        }
    }

    fun createTask(task: Task) {
        viewModelScope.launch {
            repository.createTask(task)
            mutateMap { it[task.id] = task }
        }
    }

    private fun mutateMap(callback: (MutableMap<String, Task>) -> Unit) {
        val mutatedMap = _taskMap.value.orEmpty().toMutableMap().apply(callback)
        _taskMap.postValue(mutatedMap)
    }
}