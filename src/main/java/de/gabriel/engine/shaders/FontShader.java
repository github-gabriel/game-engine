package de.gabriel.engine.shaders;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static de.gabriel.engine.Main.RESOURCES_PATH;

public class FontShader extends ShaderProgram {

    private static final String VERTEX_FILE = RESOURCES_PATH + "shaders/fonts/fontVertexShader.glsl";
    private static final String FRAGMENT_FILE = RESOURCES_PATH + "shaders/fonts/fontFragmentShader.glsl";

    private int location_colour;
    private int location_translation;

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_colour = super.getUniformLocation("color");
        location_translation = super.getUniformLocation("translation");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    public void loadColor(Vector3f colour) {
        super.loadVector(location_colour, colour);
    }

    public void loadTranslation(Vector2f translation) {
        super.loadVector(location_translation, translation);
    }


}
