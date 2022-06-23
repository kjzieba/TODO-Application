package efs.task.todoapp.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class UserRepository implements Repository<String, UserEntity> {
    Map<String, UserEntity> userMap = new HashMap<>();

    @Override
    public String save(UserEntity userEntity) {
        UserEntity prevUser = userMap.putIfAbsent(userEntity.getUsername(), userEntity);

        if (nonNull(prevUser)) {
            return null;
        }

        return userEntity.getUsername();
    }

    @Override
    public UserEntity query(String s) {
        return userMap.getOrDefault(s, null);
    }

    @Override
    public List<UserEntity> query(Predicate<UserEntity> condition) {
        return userMap.values().stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    @Override
    public UserEntity update(String s, UserEntity userEntity) {
        if (nonNull(userMap.replace(s, userEntity))) {
            return userEntity;
        }
        return null;
    }

    @Override
    public boolean delete(String s) {
        return nonNull(userMap.remove(s));
    }
}
