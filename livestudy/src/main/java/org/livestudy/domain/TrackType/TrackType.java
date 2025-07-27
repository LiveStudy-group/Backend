package org.livestudy.domain.TrackType;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TrackType {
    AUDIO,
    VIDEO,
    SCREEN
}
