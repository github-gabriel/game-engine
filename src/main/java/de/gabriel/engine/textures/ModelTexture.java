package de.gabriel.engine.textures;

import lombok.Getter;
import lombok.Setter;

/**
 * Repräsentiert eine Textur, die auf ein Modell gemappt werden kann.
 */
@Getter
@Setter
public class ModelTexture {

    /**
     * Die ID der Textur.
     */
    private int textureID;

    /**
     * Die ID der Normal Map (Textur).
     */
    private int normalMapID;

    /**
     * Der Glanzfaktor der Textur.
     */
    private float shineDamper = 1;

    /**
     * Die Reflektivität der Textur.
     */
    private float reflectivity = 0;

    /**
     * Gibt an, ob die Textur an Stellen transparent ist.
     * Transparente Stellen (mit einem Alpha Wert < 0.5) werden nicht gerendert.
     * Standardmäßig ist die Textur nicht transparent (false).
     */
    private boolean hasTransparency = false;

    /**
     * Gibt an, ob die Textur Fake Lighting verwenden soll.
     * Dies wird benötigt, wenn die Normals der Textur dafür sorgen, dass
     * die Textur falsch beleuchtet erscheint.
     * Standardmäßig wird kein Fake Lighting verwendet (false).
     */
    private boolean useFakeLighting = false;

    /**
     * Die Anzahl der Reihen an Texturen innerhalb der Textur.
     * Standardmäßig besteht eine normale Textur aus einer Reihe (1).
     * Texturatlasse können jedoch mehrere Reihen mit unterschiedlichen
     * Texturen für dasselbe Modell enthalten.
     * Für Texturatlasse gilt: Anzahl der Reihen = Anzahl der Spalten
     */
    private int numberOfRows = 1;

    public ModelTexture(int textureID) {
        this.textureID = textureID;
    }

}
