package com.bni.taskmgtapp.repository;

import com.bni.taskmgtapp.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    
}