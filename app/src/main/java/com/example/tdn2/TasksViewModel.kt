package com.example.tdn2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TasksViewModel: ViewModel() {

    val taskListLiveData = MutableLiveData<List<Task>?>()
    private val repository = TaskRepository()

    fun loadTasks() {
        viewModelScope.launch {
            val taskList = repository.loadTasks()
            taskListLiveData.postValue(taskList)
        }
    }

    fun editTask(task: Task) {
        val success = MutableLiveData<Task>()
        viewModelScope.launch {
            success.postValue(repository.updateTask(task))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            val success = repository.deleteTask(task)

            val newList = taskListLiveData.value.orEmpty().toMutableList()
            if (success == true) {
                newList.remove(task)
            }
            taskListLiveData.postValue(newList)
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {

            val taskAdd = repository.createTask(task)
//            taskListLiveData.postValue()
        }
        }

    }

