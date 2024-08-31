package com.security02.repositories;

import com.security02.models.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username); // specifying method firm

    @Query("select u from UserEntity  u where u.username = ?1") // specifying query to be executed
    Optional<UserEntity> getName(String username);

}
