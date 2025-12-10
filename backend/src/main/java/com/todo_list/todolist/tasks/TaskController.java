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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Tarefas", description = "Gerenciamento de tarefas")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    // Cria task

    @Operation(summary = "Criar tarefa", description = "Cria uma nova tarefa associada ao usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro na validação dos dados")
    })
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

    @Operation(summary = "Listar tarefas", description = "Lista todas as tarefas do usuário autenticado com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas recuperada com sucesso")
    @GetMapping("/")
    public ResponseEntity<Page<TaskModel>> list(
            HttpServletRequest request,
            @PageableDefault(page = 0, size = 9) Pageable pageable) {
        var idUser = request.getAttribute("idUser");
        Page<TaskModel> tasks = this.taskRepository.findByIdUser((UUID) idUser, pageable);
        return ResponseEntity.ok(tasks);
    }

    // Busca task por {id}

    @Operation(summary = "Detalhar tarefa", description = "Busca uma tarefa específica pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "400", description = "Usuário sem permissão")
    })
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

    @Operation(summary = "Atualizar tarefa", description = "Atualiza uma tarefa existente pelo ID. Todos os campos no corpo da requisição são opcionais.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro na validação"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
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

    @Operation(summary = "Deletar tarefa", description = "Remove uma tarefa existente pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "400", description = "Sem permissão para deletar")
    })
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