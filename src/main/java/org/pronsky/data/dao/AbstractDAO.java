package org.pronsky.data.dao;

import java.util.List;

public interface AbstractDAO<K, T> {
    T getById(K id);

    List<T> getAll();

    T create(T t);

    T update(T t);

    boolean deleteById(K id);
}
