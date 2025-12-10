package com.todo_list.todolist.tasks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {

    Page<TaskModel> findByIdUser(UUID idUser, Pageable pageable);

}