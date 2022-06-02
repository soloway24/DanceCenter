package com.kuznets.danceCenter.repositories;

import com.kuznets.danceCenter.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUsername(String username);
    boolean existsAppUserByUsername(String username);

    @Query("select case when count(u)> 0 then true else false end from AppUser u where lower(u.username) like lower(concat('%', :username,'%'))")
    boolean existsByUsernameText(@Param("username") String username);

    @Query("select u from AppUser u where lower(u.username) like lower(concat('%', :username,'%'))")
    List<AppUser> findByUsernameText(@Param("username") String username);
}