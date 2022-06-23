package efs.task.todoapp.web;

import com.sun.net.httpserver.HttpServer;
import efs.task.todoapp.repository.TaskRepository;
import efs.task.todoapp.repository.UserRepository;
import efs.task.todoapp.service.ToDoService;
import efs.task.todoapp.service.handlers.TaskHandler;
import efs.task.todoapp.service.handlers.UserHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServerFactory {
    public static HttpServer createServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
        ToDoService service = new ToDoService(new UserRepository(), new TaskRepository());
        server.createContext("/todo/user", new UserHandler(service));
        server.createContext("/todo/task", new TaskHandler(service));
        return server;
    }
}
