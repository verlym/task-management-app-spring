package com.bni.taskmgtapp.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.bni.taskmgtapp.model.Task;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldSaveAndFindTaskById() {
        // Given
        Task task = new Task("Test Task", "Test Description", false);

        // When
        Task savedTask = taskRepository.save(task);
        Optional<Task> foundTask = taskRepository.findById(savedTask.getId());

        // Then
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("Test Task");
    }

    @Test
    void shouldFindAllTasks() {
        // Given
        Task task1 = new Task("First Task", "Description 1", false);
        Task task2 = new Task("Second Task", "Description 2", false);

        // When
        taskRepository.save(task1);
        taskRepository.save(task2);
        List<Task> tasks = (List<Task>) taskRepository.findAll();

        // Then
        assertThat(tasks).hasSize(2);
        assertThat(tasks.stream().map(Task::getTitle))
            .containsExactlyInAnyOrder("First Task", "Second Task");
    }

    @Test
    void shouldDeleteTask() {
        // Given
        Task task = new Task("ToDelete Task", "Some Description", false);
        Task savedTask = taskRepository.save(task);

        // When
        taskRepository.deleteById(savedTask.getId());
        Optional<Task> deletedTask = taskRepository.findById(savedTask.getId());

        // Then
        assertThat(deletedTask).isNotPresent();
    }

    @Test
    void shouldUpdateTask() {
        // Given
        Task task = new Task("Old Title", "Old Description", false);
        Task savedTask = taskRepository.save(task);

        // When
        savedTask.setTitle("New Title");
        savedTask.setDescription("New Description");
        taskRepository.save(savedTask);

        Optional<Task> updatedTask = taskRepository.findById(savedTask.getId());

        // Then
        assertThat(updatedTask).isPresent();
        assertThat(updatedTask.get().getTitle()).isEqualTo("New Title");
        assertThat(updatedTask.get().getDescription()).isEqualTo("New Description");
    }
}