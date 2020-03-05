package se.iths.patrikgustafsson.myservice;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(any(User.class))).thenAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            var p = (User) args[0];
            return new User(1L, p.getUserName(), p.getRealName(), p.getCity(), p.income, p.inRelationship);
        });
    }

    @Test
    @DisplayName("Get All Users with url api/v1/users/")
    void getAllReturnAllUsersInRepository() throws Exception {
        mockMvc.perform(get("/api/v1/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.userList[0]._links.self.href", is("http://localhost/api/v1/users/1")))
                .andExpect(jsonPath("_embedded.userList[0].userName", is("Patrik87")));
    }

    @Test
    @DisplayName("Get one user with url api/v1/users/1")
    void getOneUserWithValidId() throws Exception {
        mockMvc.perform(get("/api/v1/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/users/1")));
    }

    @Test
    @DisplayName("Get on user with username in url api/v1/users/username")
    void getOneUserWithUserNameURL() throws Exception {
        mockMvc.perform(get("/api/v1/users/Patrik87").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/users/Patrik87")))
                .andExpect(jsonPath("userName", is("Patrik87")));
    }

    @Test
    @DisplayName("Try to get a user that do not exist with invalid ID")
    void getInvalidUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Post a new user with json body")
    void postNewUserInBodyWithJson() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":0,\"userName\":\"1Patrik1\",\"realName\":\"Nisse\",\"city\":\"Trollhättan\",\"income\":10000,\"inRelationship\":true}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Delete user with ID in url")
    void deleteUserInRepository() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Trying to delete user with invalid ID")
    void deleteUserWithInvalidID() throws Exception {
        mockMvc.perform(delete("/api/v1/users/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Put with complete data")
    void putUserWithCompleteDataWithId1() throws Exception {
        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":0,\"userName\":\"PG\",\"realName\":\"Patrik G\",\"city\":\"Göteborg\",\"income\":25000,\"inRelationship\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/users/1")))
                .andExpect(jsonPath("userName", is("PG")));
    }

    @Test
    @DisplayName("Put with incomplete data, should return null on missing content")
    void putUserWithIncompleteData() throws Exception {
        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userName\":\"Nisse32\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/users/1")))
                .andExpect(jsonPath("userName", is("Nisse32")))
                .andExpect(jsonPath("realName").doesNotExist());
    }

    @Test
    @DisplayName("Patch user with new complete data")
    void patchUserWithAllData() throws Exception {
        mockMvc.perform(patch("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":0,\"userName\":\"Nisse33\",\"realName\":\"Nils\",\"city\":\"Stockholm\",\"income\":5000,\"inRelationship\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/users/1")))
                .andExpect(jsonPath("userName", is("Nisse33")));
    }

    @Test
    @DisplayName("Patch with only username and expect other values to remain unchanged")
    void patchUserWithNewUsername() throws Exception {
        mockMvc.perform(patch("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userName\":\"Nisse33\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/v1/users/1")))
                .andExpect(jsonPath("userName", is("Nisse33")))
                .andExpect(jsonPath("realName", is("Patrik")));
    }
}
