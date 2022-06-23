package efs.task.todoapp.service.handlers;

import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.service.ToDoService;

import java.io.IOException;

public class TaskOptionsHandler {
    ToDoService toDoService;
    HttpExchange exchange;

    public TaskOptionsHandler(ToDoService toDoService, HttpExchange exchange) {
        this.toDoService = toDoService;
        this.exchange = exchange;
    }

    public void handleOptions() throws IOException {
        this.exchange.sendResponseHeaders(200, 0);
        this.exchange.close();
    }
}
