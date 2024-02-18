package de.gabriel.gameEngine.entities;

import de.gabriel.gameEngine.models.TexturedModel;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

/**
 * Repräsentiert ein Objekt.
 */
@Getter
@Setter
public class Entity {

    /**
     * Das texturierte Model des Objektes.
     */
    private TexturedModel model;

    /**
     * Die Position des Objektes.
     */
    private Vector3f position;

    /**
     * Die Rotation um die entsprechenden Achsen des Objektes.
     */
    private float rotX, rotY, rotZ;

    /**
     * Die Skalierung des Objektes.
     */
    private float scale;

    /**
     * Gibt an welche Textur eines Texturatlasses verwendet werden soll.
     * Standardmäßig beginnt die Nummerierung eines Texturatlasses oben links
     * bei 0 und wird von links nach rechts und von oben nach unten fortgesetzt.
     */
    private int textureIndex = 0;

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public Entity(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.textureIndex = textureIndex;
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    /**
     * X Offset der Textur innerhalb des Texturatlasses.
     *
     * @return X Offset der Textur innerhalb des Texturatlasses.
     */
    public float getTextureXOffset() {
        int column = textureIndex % model.texture().getNumberOfRows();
        return (float) column / (float) model.texture().getNumberOfRows();
    }

    /**
     * Y Offset der Textur innerhalb des Texturatlasses.
     *
     * @return Y Offset der Textur innerhalb des Texturatlasses.
     */
    public float getTextureYOffset() {
        int row = textureIndex / model.texture().getNumberOfRows();
        return (float) row / (float) model.texture().getNumberOfRows();
    }

    /**
     * Bewegt das Objekt um die angegebenen Werte auf den entsprechenden Achsen.
     *
     * @param dx um wie viel das Objekt auf der x-Achse bewegt werden soll.
     * @param dy um wie viel das Objekt auf der y-Achse bewegt werden soll.
     * @param dz um wie viel das Objekt auf der z-Achse bewegt werden soll.
     */
    public void increasePosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    /**
     * Rotiert das Objekt um die angegebenen Werte auf den entsprechenden Achsen.
     *
     * @param dx um wie viel das Objekt auf der x-Achse rotiert werden soll.
     * @param dy um wie viel das Objekt auf der y-Achse rotiert werden soll.
     * @param dz um wie viel das Objekt auf der z-Achse rotiert werden soll.
     */
    public void increaseRotation(float dx, float dy, float dz) {
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }

}
