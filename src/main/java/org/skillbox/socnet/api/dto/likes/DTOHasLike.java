package org.skillbox.socnet.api.dto.likes;

import lombok.Data;
import org.skillbox.socnet.api.dto.DTO;

@Data
public class DTOHasLike implements DTO {
    private boolean likes;
}
