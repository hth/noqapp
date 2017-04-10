package com.noqapp.repository;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 11/18/16 3:21 PM
 */
public interface RepositoryManager<T> extends Serializable {

    /**
     * Saves a record.
     *
     * @param object
     */
    void save(T object);

    /**
     * Delete a record for a particular object.
     */
    void deleteHard(T object);
}
