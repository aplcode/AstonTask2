package ru.artemlychko.rest.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, K> {
    T save(T entity);

    void update(T entity);

    boolean deleteById(K id);

    Optional<T> findById(K id);

    List<T> findAll();

    boolean existsById(K id);
}
