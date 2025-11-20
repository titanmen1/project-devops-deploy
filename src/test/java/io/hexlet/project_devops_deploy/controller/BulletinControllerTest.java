package io.hexlet.project_devops_deploy.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hexlet.project_devops_deploy.dto.BulletinRequest;
import io.hexlet.project_devops_deploy.model.bulletin.BulletinState;
import io.hexlet.project_devops_deploy.repository.BulletinRepository;
import io.hexlet.project_devops_deploy.util.ModelGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BulletinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private BulletinRepository bulletinRepository;

    @BeforeEach
    void setup() {
        bulletinRepository.deleteAll();
    }

    @Test
    void testCreate() throws Exception {
        BulletinRequest request = BulletinRequest.builder().title("Create title").description("Create description")
                .state(BulletinState.DRAFT).contact("create@example.com").build();

        mockMvc.perform(post("/api/bulletins").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue())).andExpect(jsonPath("$.title", equalTo("Create title")));

        mockMvc.perform(get("/api/bulletins")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testIndex() throws Exception {
        mockMvc.perform(get("/api/bulletins")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    void testShow() throws Exception {
        var bulletin = bulletinRepository.save(modelGenerator.generateBulletin());

        mockMvc.perform(get("/api/bulletins/" + bulletin.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(bulletin.getId().intValue())))
                .andExpect(jsonPath("$.title", equalTo(bulletin.getTitle())));
    }

    @Test
    void testUpdate() throws Exception {
        var bulletin = bulletinRepository.save(modelGenerator.generateBulletin());

        BulletinRequest request = BulletinRequest.builder().title("Updated title").description("Updated description")
                .state(BulletinState.PUBLISHED).contact("updated@example.com").build();

        mockMvc.perform(put("/api/bulletins/" + bulletin.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.title", equalTo("Updated title")))
                .andExpect(jsonPath("$.state", equalTo("PUBLISHED")));
    }

    @Test
    void testDelete() throws Exception {
        var bulletin = bulletinRepository.save(modelGenerator.generateBulletin());

        mockMvc.perform(delete("/api/bulletins/" + bulletin.getId())).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/bulletins/" + bulletin.getId())).andExpect(status().isNotFound());
    }
}
