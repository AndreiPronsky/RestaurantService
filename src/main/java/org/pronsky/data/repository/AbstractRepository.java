package org.pronsky.data.repository;

import java.util.List;

public interface AbstractRepository<K, T> {
    T findById(K id);

    List<T> findAll();

    T save(T entity);

    void delete(T entity);
}
