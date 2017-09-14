package it.sharengo.eteria.data.models;

import java.io.Serializable;

public class Images implements Serializable {

    public Icon icon;
    public Icon icon_svg;

    public Images() {
    }

    public Images(Icon icon, Icon icon_svg) {
        this.icon = icon;
        this.icon_svg = icon_svg;
    }
}
