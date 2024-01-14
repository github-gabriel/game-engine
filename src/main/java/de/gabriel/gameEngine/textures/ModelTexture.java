package de.gabriel.gameEngine.textures;

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

    public ModelTexture(int textureID) {
        this.textureID = textureID;
    }

}
