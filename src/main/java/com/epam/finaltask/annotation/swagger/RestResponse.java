package com.epam.finaltask.annotation.swagger;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(
        value = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
                @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
                @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
                @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
        })
public @interface RestResponse {

}
