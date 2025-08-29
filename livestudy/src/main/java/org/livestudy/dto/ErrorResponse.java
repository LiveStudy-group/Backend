package org.livestudy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private String timestamp;
    private int status;
    private String error;
    private String errorCode;
    private String message;
    private String path;
}
