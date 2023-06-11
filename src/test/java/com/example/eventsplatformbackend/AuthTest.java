package com.example.eventsplatformbackend;

import com.example.eventsplatformbackend.adapter.objectstorage.S3Adapter;
import com.example.eventsplatformbackend.config.AwsConfig;
import com.example.eventsplatformbackend.config.AwsCredentials;
import com.example.eventsplatformbackend.domain.dto.response.JwtResponse;
import com.example.eventsplatformbackend.security.JwtUtil;
import com.example.eventsplatformbackend.service.s3.S3ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


class AuthTest extends TestParent{
    @MockBean
    private AwsConfig awsConfig;
    @MockBean
    private AwsCredentials awsCredentials;
    @MockBean S3Adapter s3Adapter;
    @MockBean
    private S3ServiceImpl s3Service;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void registerUser() throws Exception {
        Map<String,Object> body = new HashMap<>();
        body.put("username",username);
        body.put("email",email);
        body.put("password",password);

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(s3Service).pickRandomObjectFromDirectory(anyString());
    }
    @Test
    void loginWithMalformedEmail_get422() throws Exception {
        Map<String,String> body = new HashMap<>();
        body.put("email","wrongEmail");
        body.put("password",password);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(422))
                .andReturn();
    }
    @Test
    void login_getJwt_extractUserDataFromJwt() throws Exception {
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
        assertNotNull("token should return owner's id!", jwtUtil.extractUserId(jwtResponse.getAccessToken()));
    }
}
