package edu.neu.cs6510.sp25.t1.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.HttpURLConnection;

class ApiResponseTest {

    @Test
    void testConstructorAndGetters() {
        final ApiResponse response = new ApiResponse(200, "Success");

        assertEquals(200, response.getStatusCode());
        assertEquals("Success", response.getResponseBody());
    }

    @Test
    void testIsNotFound() {
        final ApiResponse notFoundResponse = new ApiResponse(HttpURLConnection.HTTP_NOT_FOUND, "Not Found");
        final ApiResponse otherResponse = new ApiResponse(HttpURLConnection.HTTP_OK, "OK");

        assertTrue(notFoundResponse.isNotFound());
        assertFalse(otherResponse.isNotFound());
    }
}
