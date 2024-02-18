package de.gabriel.gameEngine.skybox;

import de.gabriel.gameEngine.Main;
import de.gabriel.gameEngine.entities.Camera;
import de.gabriel.gameEngine.shaders.ShaderProgram;
import de.gabriel.gameEngine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static de.gabriel.gameEngine.Main.SHADER_PATH;

public class SkyboxShader extends ShaderProgram {

    /**
     * Dateipfad des Skybox Vertex Shaders.
     */
    private static final String VERTEX_FILE = SHADER_PATH + "/skybox/" + "skyboxVertexShader.txt";

    /**
     * Dateipfad des Skybox Fragment Shaders.
     */
    private static final String FRAGMENT_FILE = SHADER_PATH + "/skybox/" + "skyboxFragmentShader.txt";

    private static final float ROTATION_SPEED = 1;

    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_fogColor;
    private int location_cubeMap;
    private int location_cubeMap2;
    private int location_blendFactor;

    private float rotation = 0;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f matrix = Maths.createViewMatrix(camera);
        // Translation auf 0 setzen
        matrix.m30(0);
        matrix.m31(0);
        matrix.m32(0);
        // Rotieren der Skybox
        rotation += ROTATION_SPEED * Main.getDeltaTime();
        matrix.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0));
        super.loadMatrix(location_viewMatrix, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_fogColor = super.getUniformLocation("fogColor");
        location_cubeMap = super.getUniformLocation("cubeMap");
        location_cubeMap2 = super.getUniformLocation("cubeMap2");
        location_blendFactor = super.getUniformLocation("blendFactor");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public void loadFogColor(float r, float g, float b) {
        super.loadVector(location_fogColor, new Vector3f(r, g, b));
    }

    /**
     * Lädt die Textur-Units für die Skyboxen in die entsprechenden Uniform-Variablen
     * im Shader, damit dieser auch auf die richtigen Texturen zugreifen kann.
     */
    public void connectTextureUnits() {
        super.loadInt(location_cubeMap, 0);
        super.loadInt(location_cubeMap2, 1);
    }

    public void loadBlendFactor(float blend) {
        super.loadFloat(location_blendFactor, blend);
    }

}
