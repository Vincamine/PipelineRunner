package edu.neu.cs6510.sp25.t1.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.net.HttpURLConnection;

class ApiResponseTest {

    @Test
    void testConstructorAndGetters() {
        ApiResponse response = new ApiResponse(200, "Success");

        assertEquals(200, response.getStatusCode());
        assertEquals("Success", response.getResponseBody());
    }

    @Test
    void testIsNotFound() {
        ApiResponse notFoundResponse = new ApiResponse(HttpURLConnection.HTTP_NOT_FOUND, "Not Found");
        ApiResponse otherResponse = new ApiResponse(HttpURLConnection.HTTP_OK, "OK");

        assertTrue(notFoundResponse.isNotFound());
        assertFalse(otherResponse.isNotFound());
    }
}
