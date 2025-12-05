package com.todo_list.todolist.user;

import com.todo_list.todolist.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<UserModel, UUID> {
    UserModel findByUsername(String username);
}