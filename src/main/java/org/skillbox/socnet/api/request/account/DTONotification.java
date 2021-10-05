package org.skillbox.socnet.api.request.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.skillbox.socnet.model.entity.enums.NotificationType;

@Data
public class DTONotification {
    @JsonProperty("notification_type")
    private NotificationType notificationType;
    private boolean enable;
}
