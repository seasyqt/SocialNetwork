package org.skillbox.socnet.api.request.account;

import lombok.Data;

@Data
public class RegisterConfirm {
    private Integer userId;
    private String token;
}
