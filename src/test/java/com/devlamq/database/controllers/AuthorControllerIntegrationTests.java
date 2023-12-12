package com.devlamq.database.controllers;

import com.devlamq.database.TestDataUtil;
import com.devlamq.database.domain.dto.AuthorDto;
import com.devlamq.database.domain.entities.AuthorEntity;
import com.devlamq.database.services.AuthorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthorControllerIntegrationTests {

    private final MockMvc mockMvc;

    private final AuthorService authorService;

    private final ObjectMapper objectMapper;

    @Autowired
    public AuthorControllerIntegrationTests(MockMvc mockMvc, AuthorService authorService) {
        this.mockMvc = mockMvc;
        this.authorService = authorService;
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testThatCreateAuthorSuccessfullyReturnsHttp201Created() throws Exception {

        AuthorEntity testAuthorA = TestDataUtil.createTestAuthorA();
        testAuthorA.setId(null);
        String authorJson = objectMapper.writeValueAsString(testAuthorA);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatCreateAuthorSuccessfullyReturnsSavedAuthor() throws Exception {

        AuthorEntity testAuthorA = TestDataUtil.createTestAuthorA();
        testAuthorA.setId(null);
        String authorJson = objectMapper.writeValueAsString(testAuthorA);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value("Abigail Rose")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.age").value("80")
        );
    }

    @Test
    public void testThatListAuthorsReturnsHttpStatus200() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatListAuthorsReturnsListOfAuthors() throws Exception {
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorA();
        authorService.save(testAuthorEntityA);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").isString()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].age").isNumber()
        );
    }

    @Test
    public void testThatGetAuthorReturnsAuthorWhenAuthorExists() throws Exception {
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorA();
        authorService.save(testAuthorEntityA);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/" + testAuthorEntityA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(testAuthorEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testAuthorEntityA.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.age").value(testAuthorEntityA.getAge())
        );
    }

    @Test
    public void testThatGetAuthorReturnsHttpStatus200WhenAuthorExists() throws Exception {
        AuthorEntity testAuthorEntityA = TestDataUtil.createTestAuthorA();
        authorService.save(testAuthorEntityA);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/" + testAuthorEntityA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatGetAuthorReturnsHttpStatus404WhenNoAuthorExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/100")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatFullUpdateAuthorReturnsHttpStatus404WhenNoAuthorExists() throws Exception {
        AuthorDto testAuthorDto = TestDataUtil.createTestAuthorDtoA();
        String testAuthorDtoJson = objectMapper.writeValueAsString(testAuthorDto);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testAuthorDtoJson)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatFullUpdateAuthorReturnsHttpStatus200WhenAuthorExists() throws Exception {
        AuthorEntity authorEntityA = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthorEntityA = authorService.save(authorEntityA);

        AuthorDto testAuthorDto = TestDataUtil.createTestAuthorDtoA();
        String testAuthorDtoJson = objectMapper.writeValueAsString(testAuthorDto);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/" + savedAuthorEntityA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testAuthorDtoJson)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatFullUpdateUpdatesExistingAuthor() throws Exception {
        AuthorEntity authorEntityA = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthorEntityA = authorService.save(authorEntityA);

        AuthorEntity authorDto = TestDataUtil.createTestAuthorB();
        String authorJson = objectMapper.writeValueAsString(authorDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/" + savedAuthorEntityA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(savedAuthorEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(savedAuthorEntityA.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.age").value(savedAuthorEntityA.getAge())
        );
    }

    @Test
    public void testThatPartialUpdateExistingAuthorReturnsHttpStatusOK() throws Exception{
        AuthorEntity authorEntityA = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthorEntityA = authorService.save(authorEntityA);

        AuthorDto testAuthorDto = TestDataUtil.createTestAuthorDtoA();
        String testAuthorDtoJson = objectMapper.writeValueAsString(testAuthorDto);
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/authors/" + savedAuthorEntityA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testAuthorDtoJson)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatPartialUpdateExistingAuthorReturnsUpdatedAuthor() throws Exception{
        AuthorEntity authorEntityA = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthorEntityA = authorService.save(authorEntityA);

        AuthorDto testAuthorDto = TestDataUtil.createTestAuthorDtoA();
        String testAuthorDtoJson = objectMapper.writeValueAsString(testAuthorDto);
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/authors/" + savedAuthorEntityA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testAuthorDtoJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(savedAuthorEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(savedAuthorEntityA.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.age").value(savedAuthorEntityA.getAge())
        );
    }

    @Test
    public void testThatDeleteAuthorReturnsHttpStatus204ForNonExistingAuthor() throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/authors/99")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testThatDeleteAuthorReturnsHttpStatus204ForExistingAuthor() throws Exception{
        AuthorEntity authorEntityA = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthorEntityA = authorService.save(authorEntityA);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/authors/" + savedAuthorEntityA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

}
