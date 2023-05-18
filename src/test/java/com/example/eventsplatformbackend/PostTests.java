package com.example.eventsplatformbackend;

import com.example.eventsplatformbackend.adapter.objectstorage.S3Adapter;
import com.example.eventsplatformbackend.config.AwsConfig;
import com.example.eventsplatformbackend.config.AwsCredentials;
import com.example.eventsplatformbackend.domain.dto.response.JwtResponse;
import com.example.eventsplatformbackend.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class PostTests extends TestParent{
    @MockBean
    private AwsConfig awsConfig;
    @MockBean
    private AwsCredentials awsCredentials;
    @MockBean
    private S3Adapter s3Adapter;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Order(1)
    @Test
    void signUp() {
        Map<String,Object> body = new HashMap<>();
        body.put("username",username);
        body.put("email",email);
        body.put("password",password);

        try{
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
        } catch (Exception e){
            log.error("Cannot sing in with {} user", username);
            log.error(e.getMessage());
        }
    }
    @Order(2)
    @Test
    void createPost() throws Exception {
        Map<String,String> body = new HashMap<>();
        body.put("email",email);
        body.put("password",password);
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JwtResponse jwtResponse = new ObjectMapper().readValue(response, JwtResponse.class);

        mockMvc.perform(post("/post")
                        .contentType(MediaType.MULTIPART_FORM_DATA) //from MediaType
                        .param("name", "Some event")
                        .param("beginDate", "2026-05-27T10:30:00")
                        .param("endDate", "2026-05-28T10:30:00")
                        .param("format", "OFFLINE")
                        .param("type", "MEETUP")
                        .header("Authorization", "Bearer "+jwtResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andReturn();
    }
    // TODO
}
