package efs.task.todoapp.service.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import efs.task.todoapp.service.ToDoService;

import java.io.IOException;

public class UserHandler implements HttpHandler {
    ToDoService toDoService;

    public UserHandler(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Auth");
        if(exchange.getRequestMethod().equals("POST")){
            new UserPostHandler(toDoService, exchange).handlePost();
        } else if (exchange.getRequestMethod().equals("OPTIONS")) {
            new UserOptionsHandler(toDoService, exchange).handleOptions();
        }
    }
}
