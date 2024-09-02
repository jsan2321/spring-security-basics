package com.security03.persistence.repository;

import com.security03.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> { // or JpaReposistory

    // jpa query method
    Optional<UserEntity> findUserEntityByUsername(String username);

//    @Query("SELECT u FROM UserEntity u WHERE u.username = ?") // query annotation
//    Optional<UserEntity> findUser(String username);

}