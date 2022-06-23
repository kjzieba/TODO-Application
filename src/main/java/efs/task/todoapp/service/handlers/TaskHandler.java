package efs.task.todoapp.service.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import efs.task.todoapp.service.ToDoService;

import java.io.IOException;

public class TaskHandler implements HttpHandler {
    ToDoService toDoService;

    public TaskHandler(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Auth");
        if (exchange.getRequestMethod().equals("POST")) {
            new TaskPostHandler(toDoService, exchange).handlePost();
        } else if (exchange.getRequestMethod().equals("OPTIONS")) {
            new TaskOptionsHandler(toDoService, exchange).handleOptions();
        } else if (exchange.getRequestMethod().equals("GET")) {
            if (exchange.getRequestURI().getPath().equals("/todo/task")) {
                new TaskGetHandler(toDoService, exchange).handleGetTasks();
            }
            else {
                new TaskGetHandler(toDoService, exchange).handleGetTask();
            }
        } else if (exchange.getRequestMethod().equals("PUT")) {
            new TaskPutHandler(toDoService, exchange).handlePut();
        } else if (exchange.getRequestMethod().equals("DELETE")) {
            new TaskDeleteHandler(toDoService, exchange).handleDelete();
        }
    }
}
