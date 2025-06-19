package com.capstone1.findable.exception;

import com.capstone1.findable.Exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        @RestController
        class DummyController {
            @GetMapping("/bad")
            public String bad() {
                throw new IllegalArgumentException("Bad argument");
            }
        }
        mockMvc = MockMvcBuilders
                .standaloneSetup(new DummyController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleIllegalArgumentException_returns400() throws Exception {
        mockMvc.perform(get("/bad").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Bad argument"));
    }

    @Test
    void handleGenericException_returns500() throws Exception {
        @RestController
        class ErrorController {
            @GetMapping("/oops")
            public String oops() {
                throw new RuntimeException("fail");
            }
        }
        MockMvc mvc = MockMvcBuilders
                .standaloneSetup(new ErrorController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mvc.perform(get("/oops"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message")
                        .value("An unexpected error occurred. Please try again later."));
    }
}