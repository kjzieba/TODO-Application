package efs.task.todoapp.service.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.repository.UserEntity;
import efs.task.todoapp.service.AuthenticationFailedException;
import efs.task.todoapp.service.IncorrectDataException;
import efs.task.todoapp.service.ToDoService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

public class TaskPostHandler {
    ToDoService toDoService;
    HttpExchange exchange;

    public TaskPostHandler(ToDoService toDoService, HttpExchange exchange) {
        this.toDoService = toDoService;
        this.exchange = exchange;
    }

    public void handlePost() throws IOException {
        String taskInfo = new BufferedReader(new InputStreamReader(this.exchange.getRequestBody())).lines().collect(Collectors.joining("\n"));

        try {
            Headers userInfo = this.exchange.getRequestHeaders();
            UserEntity user = toDoService.getUser(userInfo);
            String uuid = toDoService.createTask(taskInfo, user);
            this.exchange.sendResponseHeaders(201, uuid.length());
            OutputStream os = exchange.getResponseBody();
            os.write(uuid.getBytes());
            os.close();
        } catch (IncorrectDataException e) {
            this.exchange.sendResponseHeaders(400, 0);
        } catch (AuthenticationFailedException e) {
            this.exchange.sendResponseHeaders(401, 0);
        }
        this.exchange.close();
    }
}
