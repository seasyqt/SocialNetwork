package org.skillbox.socnet.api.request.account;

import lombok.Data;

@Data
public class SetPassword {
    private String token;
    private String password;
}
