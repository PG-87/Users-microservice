package se.iths.patrikgustafsson.myservice;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

    final UsersRepository repository;

    public UsersController(UsersRepository storage) {
        this.repository = storage;
    }
}
