package edu.neu.cs6510.sp25.t1.cli.commands;
import java.net.HttpURLConnection;

/**
 * The ApiResponse class is used to encapsulate the response information from a REST API.
 * It includes the status code and the response body.
 */

public class ApiResponse {
    private int statusCode; 
    private String responseBody;

    /**
     * Constructor to create an ApiResponse object.
     *
     * @param statusCode the status code of the response
     * @param responseBody the content of the response
     */
    public ApiResponse(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    /**
     * Checks if the response indicates a 404 Not Found error.
     *
     * @return true if the status code is 404, false otherwise
     */
    public boolean isNotFound() {
        return statusCode == HttpURLConnection.HTTP_NOT_FOUND;
    }
}
