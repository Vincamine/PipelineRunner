package edu.neu.cs6510.sp25.t1.util;

import java.net.HttpURLConnection;

/**
 * Represents an API response with a status code and response body.
 * Used to encapsulate HTTP responses from REST API calls.
 */
public class ApiResponse {
    private final int statusCode;
    private final String responseBody;

    /**
     * Constructs an ApiResponse object.
     *
     * @param statusCode   The HTTP status code of the response.
     * @param responseBody The response body content.
     */
    public ApiResponse(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Retrieves the HTTP status code.
     *
     * @return The HTTP status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Retrieves the response body content.
     *
     * @return The response body as a string.
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * Checks if the response indicates a 404 Not Found error.
     *
     * @return {@code true} if the status code is 404, otherwise {@code false}.
     */
    public boolean isNotFound() {
        return statusCode == HttpURLConnection.HTTP_NOT_FOUND;
    }
}
