package org.livestudy.domain.badge;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Badge {

    NONE,
    GOLD,
    SILVER,
    BRONZE
}
