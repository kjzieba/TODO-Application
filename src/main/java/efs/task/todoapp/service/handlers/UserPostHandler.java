package efs.task.todoapp.service.handlers;

import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.service.DataAlreadyPresentException;
import efs.task.todoapp.service.IncorrectDataException;
import efs.task.todoapp.service.ToDoService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class UserPostHandler {
    ToDoService toDoService;
    HttpExchange exchange;

    public UserPostHandler(ToDoService toDoService, HttpExchange exchange) {
        this.toDoService = toDoService;
        this.exchange = exchange;
    }

    public void handlePost() throws IOException {
        String userInfo = new BufferedReader(new InputStreamReader(this.exchange.getRequestBody())).lines().collect(Collectors.joining("\n"));
        try {
            toDoService.createUser(userInfo);
            this.exchange.sendResponseHeaders(201, 0);
        } catch (IncorrectDataException e) {
            this.exchange.sendResponseHeaders(400, 0);
        } catch (DataAlreadyPresentException e) {
            this.exchange.sendResponseHeaders(409, 0);
        }
        this.exchange.close();
    }
}
