package com.example.primeira_api.service;

import com.example.primeira_api.model.Task;
import com.example.primeira_api.model.Status;
import com.example.primeira_api.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public Task moveTaskToNextStatus(Task task) {
        if (task.getStatus() == Status.A_FAZER) {
            task.setStatus(Status.EM_PROGRESSO);
        } else if (task.getStatus() == Status.EM_PROGRESSO) {
            task.setStatus(Status.CONCLUIDO);
        }
        return taskRepository.save(task);
    }
}
