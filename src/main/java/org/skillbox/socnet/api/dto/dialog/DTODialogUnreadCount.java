package org.skillbox.socnet.api.dto.dialog;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.skillbox.socnet.api.dto.DTO;

@Data
@AllArgsConstructor
public class DTODialogUnreadCount implements DTO {
    private long count;
}
