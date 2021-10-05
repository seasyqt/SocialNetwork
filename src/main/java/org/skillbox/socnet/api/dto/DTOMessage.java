package org.skillbox.socnet.api.dto;


public class DTOMessage implements DTO {
    private String message = "ok";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
