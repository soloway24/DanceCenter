package com.kuznets.danceCenter.repositories;

import com.kuznets.danceCenter.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
    boolean existsRoleByName(String name);
}