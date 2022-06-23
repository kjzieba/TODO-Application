package efs.task.todoapp.service.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.repository.UserEntity;
import efs.task.todoapp.service.*;

import java.io.IOException;
import java.io.OutputStream;

public class TaskGetHandler {
    ToDoService toDoService;
    HttpExchange exchange;

    public TaskGetHandler(ToDoService toDoService, HttpExchange exchange) {
        this.toDoService = toDoService;
        this.exchange = exchange;
    }

    public void handleGetTasks() throws IOException {
        try {
            Headers userInfo = this.exchange.getRequestHeaders();
            UserEntity user = toDoService.getUser(userInfo);
            String tasks = toDoService.getTasks(user);
            this.exchange.sendResponseHeaders(200, tasks.length());
            OutputStream os = exchange.getResponseBody();
            os.write(tasks.getBytes());
            os.close();
        } catch (IncorrectDataException e) {
            this.exchange.sendResponseHeaders(400, 0);
        } catch (AuthenticationFailedException e) {
            this.exchange.sendResponseHeaders(401, 0);
        }
        this.exchange.close();
    }

    public void handleGetTask() throws IOException {
        try {
            Headers userInfo = this.exchange.getRequestHeaders();
            UserEntity user = toDoService.getUser(userInfo);

            String uuid = exchange.getRequestURI().getPath().replace("/todo/task/", "");
            String task = toDoService.getTask(uuid, user);

            this.exchange.sendResponseHeaders(200, task.length());
            OutputStream os = exchange.getResponseBody();
            os.write(task.getBytes());
            os.close();
        } catch (IncorrectDataException e) {
            this.exchange.sendResponseHeaders(400, 0);
        } catch (AuthenticationFailedException e) {
            this.exchange.sendResponseHeaders(401, 0);
        } catch (TaskOwnershipException e) {
            this.exchange.sendResponseHeaders(403, 0);
        } catch (DataAbsentException e) {
            this.exchange.sendResponseHeaders(404, 0);
        }
        this.exchange.close();
    }
}
