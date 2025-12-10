package com.todo_list.todolist.tasks;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    // Cria task
    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {

        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();

        if (currentDate.toLocalDate().isAfter(taskModel.getStartAt().toLocalDate()) ||
                currentDate.toLocalDate().isAfter(taskModel.getEndAt().toLocalDate())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de início/término não pode ser anterior à data de hoje");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de início deve ser menor que a data de término");
        }

        try {
            var task = this.taskRepository.save(taskModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lista task com paginação
    @GetMapping("/")
    public ResponseEntity<Page<TaskModel>> list(
            HttpServletRequest request,
            @PageableDefault(page = 0, size = 9) Pageable pageable
    ) {
        var idUser = request.getAttribute("idUser");
        Page<TaskModel> tasks = this.taskRepository.findByIdUser((UUID) idUser, pageable);
        return ResponseEntity.ok(tasks);
    }

    // Busca task por {id}
    @GetMapping("/{id}")
    public ResponseEntity unique(@PathVariable UUID id, HttpServletRequest request) {

        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tarefa não encontrada.");
        }

        // Valida se o usuário que está buscando é o dono da tarefa
        var idUser = request.getAttribute("idUser");
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário não tem permissão para visualizar essa tarefa.");
        }

        return ResponseEntity.ok(task);
    }

    // Atualiza task
    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {

        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tarefa não encontrada.");
        }

        var idUser = request.getAttribute("idUser");

        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário não tem permissão para alterar essa tarefa.");
        }

        if (taskModel.getTitle() != null) {
            try {
                task.setTitle(taskModel.getTitle());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        if (taskModel.getDescription() != null) {
            task.setDescription(taskModel.getDescription());
        }
        if (taskModel.getStartAt() != null) {
            task.setStartAt(taskModel.getStartAt());
        }
        if (taskModel.getEndAt() != null) {
            task.setEndAt(taskModel.getEndAt());
        }
        if (taskModel.getPriority() != null) {
            task.setPriority(taskModel.getPriority());
        }

        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);
    }

    // Deleta task
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable UUID id, HttpServletRequest request) {

        var task = this.taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tarefa não encontrada.");
        }

        var idUser = request.getAttribute("idUser");
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário não tem permissão para deletar essa tarefa.");
        }

        this.taskRepository.delete(task);
        return ResponseEntity.status(HttpStatus.OK).body("Tarefa deletada com sucesso!");
    }
}