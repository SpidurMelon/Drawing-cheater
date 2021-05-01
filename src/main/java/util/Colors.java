package util;

import java.awt.*;
import lombok.Getter;

public enum Colors {
    BACKGROUND(1,1,1,0.5f),
    PIN(0,0,0,1),
    INDICATOR(0.95f, 0.1f, 0.95f, 1)
    ;
    @Getter
    private Color color;
    Colors(float r, float g, float b, float a) {
        color = new Color(r, g, b, a);
    }
}
