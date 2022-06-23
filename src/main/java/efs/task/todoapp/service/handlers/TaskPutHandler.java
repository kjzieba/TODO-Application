package efs.task.todoapp.service.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.repository.UserEntity;
import efs.task.todoapp.service.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

public class TaskPutHandler {
    ToDoService toDoService;
    HttpExchange exchange;
    public TaskPutHandler(ToDoService toDoService, HttpExchange exchange) {
        this.toDoService = toDoService;
        this.exchange = exchange;
    }

    public void handlePut() throws IOException {
        String taskInfo = new BufferedReader(new InputStreamReader(this.exchange.getRequestBody())).lines().collect(Collectors.joining("\n"));

        try {
            Headers userInfo = this.exchange.getRequestHeaders();
            UserEntity user = toDoService.getUser(userInfo);

            String uuid = exchange.getRequestURI().getPath().replace("/todo/task/", "");
            String task = toDoService.updateTask(uuid, user, taskInfo);

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
