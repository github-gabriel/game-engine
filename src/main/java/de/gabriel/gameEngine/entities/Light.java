package de.gabriel.gameEngine.entities;

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

    public Light(Vector3f position, Vector3f color) {
        super();
        this.position = position;
        this.color = color;
    }

}
