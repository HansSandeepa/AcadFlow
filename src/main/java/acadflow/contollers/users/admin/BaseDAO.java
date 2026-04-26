package acadflow.contollers.users.admin;

import java.util.List;


public interface BaseDAO<T> {

    // Return all records
    List<T> getAll();

    // Return one record by its integer primary key
    T getById(int id);

    // Insert a new record; returns true if successful
    boolean add(T entity);

    // Update an existing record; returns true if successful
    boolean update(T entity);

    // Delete a record by its integer primary key; returns true if successful
    boolean delete(int id);
}
