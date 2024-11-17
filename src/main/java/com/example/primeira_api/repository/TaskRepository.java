package com.example.primeira_api.repository;

import com.example.primeira_api.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwner(String owner); // Busca todas as tarefas do proprietário
}