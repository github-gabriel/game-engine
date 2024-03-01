package de.gabriel.engine.entities;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

/**
 * Repräsentiert eine Lichtquelle in der Szene.
 */
@Getter
@Setter
public class Light {

    /**
     * Position des Lichts.
     */
    private Vector3f position;

    /**
     * Farbe und Intensität des Lichts.
     */
    private Vector3f color;

    private Vector3f attenuation = new Vector3f(1, 0, 0);

    public Light(Vector3f position, Vector3f color) {
        super();
        this.position = position;
        this.color = color;
    }

    public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
        super();
        this.position = position;
        this.color = color;
        this.attenuation = attenuation;
    }

}
