package de.gabriel.gameEngine.entities;

import de.gabriel.gameEngine.models.TexturedModel;
import lombok.Getter;
import org.joml.Vector3f;

@Getter
public class PointLight {

    private final Entity entity;

    private final Light light;

    public PointLight(TexturedModel model, Vector3f position, Vector3f rotation, float scale, Vector3f color, Vector3f attenuation) {
        this.entity = new Entity(model, position, rotation.x, rotation.y, rotation.z, scale);
        this.light = new Light(position, color, attenuation);
    }

}
