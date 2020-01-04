package com.example.tdn2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tdn2.network.Api
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TaskRepository {
    private val tasksService = Api.taskService

    suspend fun loadTasks(): List<Task>? {
        val tasksResponse = tasksService.getTasks()
        return if (tasksResponse.isSuccessful) tasksResponse.body() else null
    }

    suspend fun updateTask(task: Task): Task? {
        val taskUpdate = tasksService.updateTask(task)
        return if (taskUpdate.isSuccessful) taskUpdate.body() else null
    }

    suspend fun deleteTask(task: Task): Boolean? {
        val taskDelete = tasksService.deleteTask(task.id)
        return taskDelete.isSuccessful
    }

    suspend fun createTask(task: Task): Task? {
        val taskAdd = tasksService.createTask(task)
        return if (taskAdd.isSuccessful) taskAdd.body() else null
    }



}