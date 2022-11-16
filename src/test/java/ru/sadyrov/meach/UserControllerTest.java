package ru.sadyrov.meach;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.sadyrov.meach.repository.UserRepository;
import ru.sadyrov.meach.services.UserService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    private String accessToken;

    @Test
    public void login() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login", "playg");
        jsonObject.put("password", "qwerty");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        accessToken = "Bearer " + JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
    }

    @Test
    public void refreshToken() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login", "playg");
        jsonObject.put("password", "qwerty");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/login")
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        String access = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
        String refresh = JsonPath.read(result.getResponse().getContentAsString(), "$.refreshToken");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("refreshToken", refresh);
        MvcResult result2 = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/auth/token")
                        .content(jsonObject2.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/auth/refresh")
                .header("Access-Control-Request-Method", "GET")
                .header("Origin", "http://localhost:8080")
                .header("accessToken", "Bearer " + access)
                .content(jsonObject2.toString())
                .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    public void getUsersWithoutAuth() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/user/users")
        ).andExpect(status().is4xxClientError());
    }

    @Test
    public void getUsersWithAuth() throws Exception {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("login", "playg");
//        jsonObject.put("password", "qwerty");
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/api/auth/login")
//                        .content(jsonObject.toString())
//                        .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andReturn();
//        String access = "Bearer " + JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
//        System.out.println(access);
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/user/users")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://localhost:8080")
                        .header("accessToken", "Bearer " + accessToken)
        );
    }
}