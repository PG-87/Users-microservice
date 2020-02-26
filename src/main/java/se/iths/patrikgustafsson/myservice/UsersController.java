package se.iths.patrikgustafsson.myservice;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

    final UsersRepository repository;

    private final UserDataModelAssembler assembler;

    public UsersController(UsersRepository storage, UserDataModelAssembler userAssembler) {
        this.repository = storage;
        this.assembler = userAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<User>> all() {
        return assembler.toCollectionModel(repository.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<User>> one(@PathVariable Integer id) {
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
