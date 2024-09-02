package com.security04.persistence.repository;

import com.security04.persistence.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> { // or JpaReposistory

    // jpa query method
    Optional<UserEntity> findUserEntityByUsername(String username);


}