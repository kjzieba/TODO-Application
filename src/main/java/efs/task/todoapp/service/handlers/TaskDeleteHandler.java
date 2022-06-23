package efs.task.todoapp.service.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.repository.UserEntity;
import efs.task.todoapp.service.*;

import java.io.IOException;

public class TaskDeleteHandler {
    ToDoService toDoService;
    HttpExchange exchange;

    public TaskDeleteHandler(ToDoService toDoService, HttpExchange exchange) {
        this.toDoService = toDoService;
        this.exchange = exchange;
    }

    public void handleDelete() throws IOException {
        try {
            Headers userInfo = this.exchange.getRequestHeaders();
            UserEntity user = toDoService.getUser(userInfo);

            String uuid = exchange.getRequestURI().getPath().replace("/todo/task/", "");

            toDoService.delete(uuid, user);

            this.exchange.sendResponseHeaders(200, 0);
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
