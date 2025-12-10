package com.todo_list.todolist.user;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserCreateRequest {

  @Schema(example = "usuario")
  private String username;

  @Schema(example = "usuario")
  private String name;

  @Schema(example = "usuario@email.com")
  private String email;

  @Schema(example = "123456")
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
