package com.bni.taskmgtapp.service;

import com.bni.taskmgtapp.model.Task;
import com.bni.taskmgtapp.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTasks_shouldReturnListOfTasks() {
        // Arrange
        when(taskRepository.findAll()).thenReturn(Arrays.asList(
                new Task("Title1", "Desc1", false),
                new Task("Title2", "Desc2", true)));

        // Act
        List<Task> tasks = taskService.getAllTasks();

        // Assert
        assertThat(tasks).hasSize(2);
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void createTask_shouldSaveAndReturnTask() {
        // Arrange
        Task task = new Task("New Task", "New Description", false);
        when(taskRepository.save(task)).thenReturn(task);

        // Act
        Task savedTask = taskService.createTask(task);

        // Assert
        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("New Task");
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void getTaskById_shouldReturnTask_whenExists() {
        // Arrange
        Long id = 1L;
        Task task = new Task("Title", "Description", false);
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        // Act
        Optional<Task> result = taskService.getTaskById(id);

        // Assert
        assertThat(result).isPresent().hasValue(task);
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void getTaskById_shouldReturnEmpty_whenNotExists() {
        // Arrange
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Task> result = taskService.getTaskById(id);

        // Assert
        assertThat(result).isEmpty();
        verify(taskRepository, times(1)).findById(id);
    }

    @Test
    void updateTask_shouldUpdateExistingTask() {
        // Arrange
        Long id = 1L;
        Task existingTask = new Task("Old Title", "Old Desc", false);
        Task updatedTask = new Task("New Title", "New Desc", true);

        when(taskRepository.findById(id)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        // Act
        Task result = taskService.updateTask(id, updatedTask);

        // Assert
        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        assertThat(result.isCompleted()).isTrue();
        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void updateTask_shouldThrowException_whenTaskNotFound() {
        // Arrange
        Long id = 1L;
        Task updatedTask = new Task("New Title", "New Desc", true);
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.updateTask(id, updatedTask))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Task not found");

        verify(taskRepository, times(1)).findById(id);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_shouldCallDeleteById() {
        // Arrange
        Long id = 1L;

        // Act
        taskService.deleteTask(id);

        // Assert
        verify(taskRepository, times(1)).deleteById(id);
    }
}