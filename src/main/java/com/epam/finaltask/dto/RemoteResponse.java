package com.epam.finaltask.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemoteResponse<T> {
    private boolean succeeded;
    private String statusCode;
    private String statusMessage;
    private List<T> results;

    public static <T> RemoteResponse<T> createSuccessResponse(List<T> results, String statusMessage) {

        if (results == null) {
            results = Collections.emptyList();
        }
        if (statusMessage == null) {
            statusMessage = "The operation was successful";
        }
        RemoteResponse<T> response = new RemoteResponse<>();
        response.setSucceeded(true);
        response.setStatusCode(HttpStatus.OK.name());
        response.setStatusMessage(statusMessage);
        response.setResults(results);
        return response;
    }

    public static <T> RemoteResponse<T> createFailureResponse(String statusCode, String statusMessage) {
        RemoteResponse<T> response = new RemoteResponse<>();
        response.setSucceeded(false);
        response.setStatusCode(statusCode);
        response.setStatusMessage(statusMessage);
        return response;
    }
}
