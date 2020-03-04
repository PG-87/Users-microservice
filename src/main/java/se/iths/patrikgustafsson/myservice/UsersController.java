package se.iths.patrikgustafsson.myservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Slf4j
@RequestMapping("/api/v1/users")
@RestController
public class UsersController {

    @Autowired
    RestTemplate restTemplate;

    final UsersRepository repository;
    private final UserDataModelAssembler assembler;

    public UsersController(UsersRepository storage, UserDataModelAssembler usersModelAssembler) {
        this.repository = storage;
        this.assembler = usersModelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<User>> all() {
        log.info("All persons called");
        return assembler.toCollectionModel(repository.findAll());
    }

    @GetMapping(value = "/{id:[0-9]+}")
    public ResponseEntity<EntityModel<User>> one(@PathVariable Long id) {
        log.info("One person called");
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/{userName:[A-Z, a-z]+}")
    public ResponseEntity<EntityModel<User>> getByUsername(@PathVariable String userName) {
        log.info("User with username: " + userName);
        return repository.findByUserName(userName)
                .map(assembler::toModelName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<User>> createUser(@RequestBody User user) {

        if(repository.findById(user.getId()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        log.info("Created " + user);
        var u = repository.save(user);
        log.info("Saved to repository " + u);

        var entityModel = assembler.toModel(u);

        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deletePerson(@PathVariable Long id) {
        if (repository.existsById(id)) {
            log.info("User deleted with id " + id);
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    ResponseEntity<EntityModel<User>> replacePerson(@RequestBody User userIn, @PathVariable Long id) {

        if(repository.findById(id).isPresent()){
            var p = repository.findById(id)
                    .map(existingUser -> {
                        existingUser.setUserName(userIn.getUserName());
                        existingUser.setRealName(userIn.getRealName());
                        existingUser.setCity((userIn.getCity()));
                        existingUser.setIncome(userIn.getIncome());
                        existingUser.setInRelationship(userIn.inRelationship);
                        repository.save(existingUser);
                        return existingUser;})
                    .get();
            var entityModel = assembler.toModel(p);
            log.info("IDnr: " + p.getId() + " Updated!");
            return new ResponseEntity<>(entityModel, HttpStatus.OK);
        }
        else{
            log.info("Wrong ID");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    ResponseEntity<EntityModel<User>> modifyUser(@RequestBody User updatedUser, @PathVariable Long id){
        if(repository.findById(id).isPresent()){
            var p = repository.findById(id)
                .map(newUser -> {
                    if(updatedUser.getUserName() != null)
                        newUser.setUserName(updatedUser.getUserName());
                    if(updatedUser.getRealName() != null)
                        newUser.setRealName(updatedUser.getRealName());
                    if(updatedUser.getCity() != null)
                        newUser.setCity(updatedUser.getCity());
                    if(updatedUser.getIncome() != null)
                        newUser.setIncome(updatedUser.getIncome());
                    if(updatedUser.isInRelationship() != newUser.isInRelationship())
                        newUser.setInRelationship(updatedUser.isInRelationship());
                    repository.save(newUser);
                    return newUser;}).get();
            var entityModel = assembler.toModel(p);
            log.info("IDnr: " + p.getId() + " modified!");
            return new ResponseEntity<>(entityModel, HttpStatus.OK);
        }
        else {
            log.info("Wrong ID");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
