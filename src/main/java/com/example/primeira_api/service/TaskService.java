package com.example.primeira_api.service;

import com.example.primeira_api.model.Task;
import com.example.primeira_api.model.Status;
import com.example.primeira_api.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task) {
        // Cria uma nova tarefa no banco de dados
        return taskRepository.save(task);
    }

    public List<Task> getTasksByOwner(String owner) {
        // Retorna todas as tarefas associadas ao proprietário
        return taskRepository.findByOwner(owner);
    }

    public Optional<Task> getTaskById(Long id, String owner) {
        // Retorna uma tarefa pelo ID se o proprietário for o mesmo
        return taskRepository.findById(id)
                .filter(task -> task.getOwner().equals(owner))
                .or(() -> {
                    throw new ResponseStatusException(FORBIDDEN, "Acesso negado à tarefa");
                });
    }

    public Task updateTask(Task task) {
        // Atualiza uma tarefa existente no banco de dados
        return taskRepository.save(task);
    }

    public void deleteTask(Long id, String owner) {
        // Verifica se a tarefa existe e pertence ao proprietário antes de deletar
        Task task = getTaskById(id, owner).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "Tarefa não encontrada"));
        taskRepository.deleteById(task.getId());
    }

    public Task moveTaskToNextStatus(Task task) {
        // Atualiza o status da tarefa para o próximo estágio com os novos valores traduzidos
        if (task.getStatus() == Status.A_FAZER) {
            task.setStatus(Status.EM_PROGRESSO);
        } else if (task.getStatus() == Status.EM_PROGRESSO) {
            task.setStatus(Status.CONCLUIDO);
        } else {
            throw new ResponseStatusException(BAD_REQUEST, "Tarefa já está concluída");
        }
        return taskRepository.save(task);
    }
}
