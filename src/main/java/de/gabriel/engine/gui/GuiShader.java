package de.gabriel.engine.gui;


import de.gabriel.engine.shaders.ShaderProgram;
import org.joml.Matrix4f;

import static de.gabriel.engine.Main.SHADER_PATH;

public class GuiShader extends ShaderProgram {

    /**
     * Dateipfad des Vertex Shaders.
     */
    private static final String VERTEX_FILE = SHADER_PATH + "gui/" + "guiVertexShader.glsl";

    /**
     * Dateipfad des Fragment Shaders.
     */
    private static final String FRAGMENT_FILE = SHADER_PATH + "gui/" + "guiFragmentShader.glsl";

    /**
     * Die ID der Uniform Variable der Transformationsmatrix.
     */
    private int location_transformationMatrix;

    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * Diese Methode lädt eine Transformationsmatrix in die Uniform Variable im Shader.
     *
     * @param matrix die Transformationsmatrix, die in die Uniform Variable geladen werden soll.
     */
    public void loadTransformation(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    /**
     * Laden der IDs aller Uniform Variablen.
     */
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    /**
     * Binden der verschiedenen Attribute des VAOs an bestimmte Variablen
     * im Shader Programm.
     */
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}