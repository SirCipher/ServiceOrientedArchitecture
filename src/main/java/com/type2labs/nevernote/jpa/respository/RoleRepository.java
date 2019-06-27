package com.type2labs.nevernote.jpa.respository;


import com.type2labs.nevernote.jpa.entity.Role;
import com.type2labs.nevernote.jpa.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data access for roles
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
