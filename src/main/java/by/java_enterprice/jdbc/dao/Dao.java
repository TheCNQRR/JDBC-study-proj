package by.java_enterprice.jdbc.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    List<E> findAll();
    Optional<E> findById(K id);
    E save(E e);
    boolean update(E e);
    boolean delete(K id);
}
