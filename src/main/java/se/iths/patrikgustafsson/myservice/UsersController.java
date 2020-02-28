package se.iths.patrikgustafsson.myservice;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

//här ka nvi lägga en @RequestMapping("/api/users")

@Slf4j
@RequestMapping("/api/v1/users")
@RestController
public class UsersController {


    final UsersRepository repository;
    private final UserDataModelAssembler assembler;


    //Vi behöver ha en konstruktor med lite injections. Tex UserRepository och en som fixar länkarna
    public UsersController(UsersRepository storage, UserDataModelAssembler usersModelAssembler) {
        this.repository = storage;
        this.assembler = usersModelAssembler;
    }


    //här ska vi ha metoder som get, head, post, put, patch om vi vill.
    //till exempel:
    // getAllUsers med GetMapping
    @GetMapping
    public CollectionModel<EntityModel<User>> all() {
        log.info("All persons called");
        return assembler.toCollectionModel(repository.findAll());
    }

    // getUserByID -"- och (value = "/{id}")
    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<User>> one(@PathVariable Long id) {
        log.info("One person called");
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // createUser med: PostMapping och @RequestBody User user
    @PostMapping
    public ResponseEntity<EntityModel<User>> createUser(@RequestBody User user) {

        //409 if conflict, if resource already exists
        if(repository.findById(user.getId()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        log.info("Created " + user);
        var u = repository.save(user);
        log.info("Saved to repository " + u);

        //länkar med EntityModel
        var entityModel = assembler.toModel(u);

        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);

    }

    // deleteUser med DeleteMapping(@Pathvariable long id)

    @DeleteMapping("/{id}")
    ResponseEntity<?> deletePerson(@PathVariable Long id) {
        if (repository.existsById(id)) {
            log.info("User deleted with id " + id);
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    // modifyUser med PatchMapping("/{id}")

    // replaceUser med PutMapping("/{id}")
    @PutMapping("/{id}") // uppdate  replace
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
                    if(updatedUser.getIncome() != newUser.getIncome())
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
//    @PatchMapping("/{id}")
//    ResponseEntity<User> modifyPerson(@RequestBody User newUser, @PathVariable Long id) {
//
//        return repository.findById(id)
//                .map(person -> {
//                    if (newUser.getRealName() != null)
//                        person.setRealName(newUser.getRealName());
//
//                    repository.save(person);
//                    HttpHeaders headers = new HttpHeaders();
//                    headers.setLocation(linkTo(UsersController.class).slash(person.getId()).toUri());
//                    return new ResponseEntity<>(person, headers, HttpStatus.OK);
//                })
//                .orElseGet(() ->
//                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
}
