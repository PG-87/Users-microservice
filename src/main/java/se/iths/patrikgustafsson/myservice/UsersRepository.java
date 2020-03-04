package se.iths.patrikgustafsson.myservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {

        Optional<User> findByUserName(String userName);

}
