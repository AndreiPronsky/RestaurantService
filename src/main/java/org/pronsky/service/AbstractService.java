package org.pronsky.service;

import java.util.List;

public interface AbstractService<K, T> {
    T getById(K id);

    List<T> getAll();

    T create(T t);

    T update(T t);

    boolean delete(T t);
}
