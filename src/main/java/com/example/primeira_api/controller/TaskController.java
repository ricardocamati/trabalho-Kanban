package com.example.primeira_api.controller;

import com.example.primeira_api.model.Task;
import com.example.primeira_api.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestHeader("Authorization") String token, @RequestBody Task task) {
        String username = extractUsernameFromToken(token);
        task.setOwner(username); // Associa a tarefa ao usuário autenticado
        Task createdTask = taskService.createTask(task);
        return new ResponseEntity<>(createdTask, CREATED); // Retorna 201 Created
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@RequestHeader("Authorization") String token) {
        String username = extractUsernameFromToken(token);
        List<Task> tasks = taskService.getTasksByOwner(username); // Filtra tarefas pelo proprietário
        return new ResponseEntity<>(tasks, OK); // Retorna 200 OK com as tarefas do usuário
    }

    @PutMapping("/{id}/move")
    public ResponseEntity<Task> moveTask(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String username = extractUsernameFromToken(token);
        Task task = taskService.getTaskById(id, username)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tarefa não encontrada ou não pertence a você"));
        Task updatedTask = taskService.moveTaskToNextStatus(task);
        return new ResponseEntity<>(updatedTask, OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Task taskDetails) {
        String username = extractUsernameFromToken(token);
        Task task = taskService.getTaskById(id, username)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tarefa não encontrada ou não pertence a você"));
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setPriority(taskDetails.getPriority());
        Task updatedTask = taskService.updateTask(task);
        return new ResponseEntity<>(updatedTask, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        String username = extractUsernameFromToken(token);
        taskService.deleteTask(id, username);
        return new ResponseEntity<>(NO_CONTENT);
    }

    // Método auxiliar para extrair o nome de usuário do token
    private String extractUsernameFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(FORBIDDEN, "Token inválido ou ausente");
        }
        token = token.substring(7); // Remove o prefixo "Bearer "
        Jwt decodedJwt = jwtDecoder.decode(token);
        return decodedJwt.getSubject(); // O nome de usuário está no campo 'sub'
    }
}
