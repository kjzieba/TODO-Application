package efs.task.todoapp.repository;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class TaskEntity {
    @Expose
    UUID id;
    @Expose
    String description;
    @Expose
    @SerializedName("due")
    String dueDate;

    UserEntity user;


    public TaskEntity(String description, String dueDate, UserEntity user) {
        this.description = description;
        this.dueDate = dueDate;
        this.user = user;
        this.id = UUID.randomUUID();
    }

    public TaskEntity(String description, String dueDate) {
        this.description = description;
        this.dueDate = dueDate;
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setId() {
        this.id = UUID.randomUUID();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
