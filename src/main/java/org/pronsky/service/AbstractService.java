package org.pronsky.service;

import java.util.List;

public interface AbstractService<K, T> {
    T getById(K id);

    List<T> getAll();

    T save(T t);

    void delete(T t);
}
