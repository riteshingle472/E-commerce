package org.riteshingle.ecommerce.Repository;

import org.riteshingle.ecommerce.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {


}
