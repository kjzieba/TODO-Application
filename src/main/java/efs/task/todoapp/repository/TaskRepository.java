package efs.task.todoapp.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class TaskRepository implements Repository<UUID, TaskEntity> {
    Map<UUID, TaskEntity> taskMap = new HashMap<>();

    @Override
    public UUID save(TaskEntity taskEntity) {
        TaskEntity prevTask = taskMap.putIfAbsent(taskEntity.getId(), taskEntity);

        if (nonNull(prevTask)) {
            return null;
        }

        return taskEntity.getId();
    }

    @Override
    public TaskEntity query(UUID uuid) {
        return taskMap.getOrDefault(uuid, null);
    }

    @Override
    public List<TaskEntity> query(Predicate<TaskEntity> condition) {
        return taskMap.values().stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    @Override
    public TaskEntity update(UUID uuid, TaskEntity taskEntity) {
        if (nonNull(taskMap.replace(uuid, taskEntity))) {
            return taskEntity;
        }
        return null;
    }

    @Override
    public boolean delete(UUID uuid) {
        return nonNull(taskMap.remove(uuid));
    }
}
