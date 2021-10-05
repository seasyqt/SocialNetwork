package org.skillbox.socnet.api.dto.dialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.skillbox.socnet.api.dto.DTO;

@Data
@AllArgsConstructor
public class DTOUserOnline implements DTO {

    private boolean online;

    @JsonProperty("last_activity")
    private long lastActivity;

}
