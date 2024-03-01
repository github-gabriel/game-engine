package de.gabriel.engine.entities;

import de.gabriel.engine.models.TexturedModel;
import lombok.Getter;
import org.joml.Vector3f;

@Getter
public class PointLight {

    private final Entity Entity;

    private final Light light;

    public PointLight(TexturedModel model, Vector3f position, Vector3f rotation, float scale, Vector3f color, Vector3f attenuation) {
        this.Entity = new Entity(model, position, rotation.x, rotation.y, rotation.z, scale);
        this.light = new Light(position, color, attenuation);
    }

}
