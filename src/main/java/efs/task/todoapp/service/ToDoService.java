package efs.task.todoapp.service;

import com.sun.net.httpserver.Headers;
import efs.task.todoapp.repository.TaskEntity;
import efs.task.todoapp.repository.TaskRepository;
import efs.task.todoapp.repository.UserEntity;
import efs.task.todoapp.repository.UserRepository;
import com.google.gson.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;


public class ToDoService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ToDoService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public void createUser(String userInfo) throws IncorrectDataException, DataAlreadyPresentException {
        if (userInfo.isBlank()) {
            throw new IncorrectDataException();
        }

        UserEntity newUser;
        try {
            newUser = new Gson().fromJson(userInfo, UserEntity.class);
        } catch (JsonSyntaxException e) {
            throw new IncorrectDataException();
        }

        if (newUser == null || newUser.getUsername() == null || newUser.getPassword() == null) {
            throw new IncorrectDataException();
        }
        if (newUser.getUsername().isBlank() || newUser.getPassword().isBlank()) {
            throw new IncorrectDataException();
        }


        String resp = userRepository.save(newUser);
        if (resp == null) {
            throw new DataAlreadyPresentException();
        }
    }

    public UserEntity getUser(Headers userInfo) throws AuthenticationFailedException, IncorrectDataException {
        String auth;
        try {
            auth = userInfo.getOrDefault("auth", null).get(0);
        } catch (NullPointerException e) {
            throw new IncorrectDataException();
        }

        if (auth == null) {
            throw new IncorrectDataException();
        }
        if (auth.isBlank()) {
            throw new IncorrectDataException();
        }
        if (!auth.contains(":")) {
            throw new IncorrectDataException();
        }

        String[] userInformation = auth.split(":");
        if (userInformation[0] == null || userInformation[1] == null) {
            throw new IncorrectDataException();
        }
        if (userInformation[0].isBlank() || userInformation[1].isBlank()) {
            throw new IncorrectDataException();
        }

        String username;
        String password;
        try {
            username = new String(Base64.getDecoder().decode(userInformation[0].getBytes()));
            password = new String(Base64.getDecoder().decode(userInformation[1].getBytes()));
        } catch (IllegalArgumentException e) {
            throw new IncorrectDataException();
        }
        UserEntity user = userRepository.query(username);
        if (user == null) {
            throw new AuthenticationFailedException();
        }
        if (!user.getPassword().equals(password)) {
            throw new AuthenticationFailedException();
        }

        return user;
    }

    public String createTask(String taskInfo, UserEntity user) throws IncorrectDataException {
        if (taskInfo.isBlank()) {
            throw new IncorrectDataException();
        }
        TaskEntity newTask;
        try {
            newTask = new Gson().fromJson(taskInfo, TaskEntity.class);
        } catch (JsonSyntaxException e) {
            throw new IncorrectDataException();
        }
        if (newTask.getDescription() == null) {
            throw new IncorrectDataException();
        }
        if (newTask.getDueDate() != null) {
            if (!newTask.getDueDate().matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
                throw new IncorrectDataException();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            try {
                format.parse(newTask.getDueDate());
            } catch (ParseException e) {
                throw new IncorrectDataException();
            }
        }
        newTask.setUser(user);
        newTask.setId();

        UUID resp = taskRepository.save(newTask);
        JsonObject response = new JsonObject();
        response.addProperty("id", resp.toString());

        return new GsonBuilder().setPrettyPrinting().create().toJson(response);
    }

    public String getTasks(UserEntity user) {
        Predicate<TaskEntity> condition = task -> task.getUser() == user;
        List<TaskEntity> tasks = taskRepository.query(condition);

        JsonArray ret = new JsonArray();

        for (var task : tasks) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", task.getId().toString());
            System.out.println(new Gson().toJson(obj));
            obj.addProperty("description", task.getDescription());
            obj.addProperty("due", task.getDueDate());
            ret.add(obj);
        }

        //return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(tasks);
        return new GsonBuilder().setPrettyPrinting().create().toJson(ret);
    }

    public String getTask(String uuid, UserEntity user) throws DataAbsentException, TaskOwnershipException, IncorrectDataException {
        UUID uuid1;
        try {
            uuid1 = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new IncorrectDataException();
        }
        TaskEntity task = taskRepository.query(uuid1);
        if (task == null) {
            throw new DataAbsentException();
        }
        if (task.getUser() != user) {
            throw new TaskOwnershipException();
        }

        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create().toJson(task);
    }

    public String updateTask(String uuid, UserEntity user, String taskInfo) throws DataAbsentException, TaskOwnershipException, IncorrectDataException {
        if (taskInfo == null) {
            throw new IncorrectDataException();
        }
        if (taskInfo.isBlank()) {
            throw new IncorrectDataException();
        }
        UUID uuid1;
        try {
            uuid1 = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new IncorrectDataException();
        }
        TaskEntity task = taskRepository.query(uuid1);
        if (task == null) {
            throw new DataAbsentException();
        }
        if (task.getUser() != user) {
            throw new TaskOwnershipException();
        }

        TaskEntity updatedTask;

        try {
            updatedTask = new Gson().fromJson(taskInfo, TaskEntity.class);
        } catch (JsonSyntaxException e) {
            throw new IncorrectDataException();
        }

        if (updatedTask.getDescription() == null && updatedTask.getDueDate() == null) {
            throw new IncorrectDataException();
        }

        String ret;
        if (updatedTask.getDueDate() == null && updatedTask.getDueDate() != null) {
            updatedTask.setId(uuid1);
            ret = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create().toJson(updatedTask);
            updatedTask.setDueDate(task.getDueDate());
        } else if (updatedTask.getDescription() == null && updatedTask.getDescription() != null) {
            updatedTask.setId(uuid1);
            ret = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create().toJson(updatedTask);
            if (!updatedTask.getDueDate().matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
                throw new IncorrectDataException();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            try {
                format.parse(updatedTask.getDueDate());
            } catch (ParseException e) {
                throw new IncorrectDataException();
            }
            updatedTask.setDescription(task.getDescription());
        } else {
            updatedTask.setId(uuid1);
            ret = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create().toJson(updatedTask);
        }
        updatedTask.setUser(user);
        taskRepository.update(uuid1, updatedTask);

        return ret;
    }

    public void delete(String uuid, UserEntity user) throws DataAbsentException, TaskOwnershipException, IncorrectDataException {
        UUID uuid1;

        try {
            uuid1 = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new IncorrectDataException();
        }

        TaskEntity task = taskRepository.query(uuid1);
        if (task == null) {
            throw new DataAbsentException();
        }
        if (task.getUser() != user) {
            throw new TaskOwnershipException();
        }

        taskRepository.delete(uuid1);
    }
}
