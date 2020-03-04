package se.iths.patrikgustafsson.myservice;


import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Optional;

@WebMvcTest(UsersController.class)
@Import({UserDataModelAssembler.class})
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UsersRepository repository;

    @MockBean
    RestTemplate restTemplate;

    @BeforeEach
    void setUpNewTest() {
        when(repository.findAll()).thenReturn(List.of(new User(1L, "Patrik87", "Patrik", "Trollhättan", 10000, true),
                new User(2L, "Sebbe", "Sebastian", "Göteborg", 20000, false)));
        when(repository.findById(1L)).thenReturn(Optional.of(new User(1L, "Patrik87", "Patrik", "Trollhättan", 10000, true)));
        when(repository.findByUserName("Patrik87")).thenReturn(Optional.of(new User(1L, "Patrik87", "Patrik", "Trollhättan", 10000, true)));
        when(repository.save(any(User.class))).thenAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            var p = (User) args[0];
            return new User(1L, p.getUserName(), p.getRealName(), p.getCity(), p.income, p.inRelationship);
        });
    }

    @Test
    @DisplayName("Get All Users with url api/v1/users/")
    void getAllReturnAllUsersInRepository() throws Exception {
        mockMvc.perform(get("/api/v1/users").contentType("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.userList[0]._links.self.href", is("http://localhost/api/v1/users/1")))
                .andExpect(jsonPath("_embedded.userList[0].userName", is("Patrik87")));
    }

    @Test
    @DisplayName("Get one user with url api/v1/users/1")
    void getOneUserWithValidId() throws Exception {
        mockMvc.perform(get("/api/v1/users/1").accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/users/1")));
    }

    @Test
    @DisplayName("Get on user with username in url api/v1/users/username")
    void getOneUserWithUserNameURL() throws Exception {
        mockMvc.perform(get("/api/v1/users/Patrik87").accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/users/Patrik87")))
                .andExpect(jsonPath("_embedded.userList[0].userName", is("Patrik87")));
    }

    @Test
    void getInvalidUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/3").accept("application/hal+json"))
                .andExpect(status().isNotFound());
    }


}
