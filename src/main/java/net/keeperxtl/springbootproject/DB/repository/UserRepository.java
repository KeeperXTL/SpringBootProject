package net.keeperxtl.springbootproject.DB.repository;

import net.keeperxtl.springbootproject.DB.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

}
