package edu.neu.cs6510.sp25.t1.cli.commands;

import org.junit.jupiter.api.Test;

import edu.neu.cs6510.sp25.t1.model.ApiResponse;

import org.junit.jupiter.api.DisplayName;
import java.net.HttpURLConnection;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    @DisplayName("Test constructor and getters")
    void testConstructorAndGetters() {
        // Arrange
        final int expectedStatusCode = 200;
        final String expectedResponseBody = "Success";

        // Act
        final ApiResponse response = new ApiResponse(expectedStatusCode, expectedResponseBody);

        // Assert
        assertEquals(expectedStatusCode, response.getStatusCode(), 
            "Status code should match the constructor parameter");
        assertEquals(expectedResponseBody, response.getResponseBody(), 
            "Response body should match the constructor parameter");
    }

    @Test
    @DisplayName("Test isNotFound with 404 status code")
    void testIsNotFoundWithNotFoundStatus() {
        // Arrange
        final ApiResponse response = new ApiResponse(HttpURLConnection.HTTP_NOT_FOUND, "Not Found");

        // Act & Assert
        assertTrue(response.isNotFound(), 
            "isNotFound should return true for HTTP 404 status code");
    }

    @Test
    @DisplayName("Test isNotFound with non-404 status code")
    void testIsNotFoundWithOtherStatus() {
        // Arrange & Act
        final ApiResponse response200 = new ApiResponse(HttpURLConnection.HTTP_OK, "OK");
        final ApiResponse response500 = new ApiResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, "Server Error");
        final ApiResponse response403 = new ApiResponse(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");

        // Assert
        assertFalse(response200.isNotFound(), 
            "isNotFound should return false for HTTP 200 status code");
        assertFalse(response500.isNotFound(), 
            "isNotFound should return false for HTTP 500 status code");
        assertFalse(response403.isNotFound(), 
            "isNotFound should return false for HTTP 403 status code");
    }

    @Test
    @DisplayName("Test with null response body")
    void testWithNullResponseBody() {
        // Arrange & Act
        final ApiResponse response = new ApiResponse(HttpURLConnection.HTTP_OK, null);

        // Assert
        assertNull(response.getResponseBody(), 
            "Response body should be null when constructed with null");
        assertEquals(HttpURLConnection.HTTP_OK, response.getStatusCode(), 
            "Status code should be set correctly even with null response body");
    }

    @Test
    @DisplayName("Test with negative status code")
    void testWithNegativeStatusCode() {
        // Arrange
        final int negativeStatusCode = -1;
        
        // Act
        final ApiResponse response = new ApiResponse(negativeStatusCode, "Error");

        // Assert
        assertEquals(negativeStatusCode, response.getStatusCode(), 
            "Status code should allow negative values");
        assertFalse(response.isNotFound(), 
            "isNotFound should return false for negative status code");
    }
}