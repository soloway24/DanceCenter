package com.kuznets.danceCenter.repositories;

import com.kuznets.danceCenter.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUsername(String username);
    boolean existsAppUserByUsername(String username);

}