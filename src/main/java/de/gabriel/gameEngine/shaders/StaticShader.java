package de.gabriel.gameEngine.shaders;

import de.gabriel.gameEngine.entities.Camera;
import de.gabriel.gameEngine.entities.Light;
import de.gabriel.gameEngine.utils.Maths;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static de.gabriel.gameEngine.Main.SHADER_PATH;

/**
 * Shader für statische Objekte.
 */
@Slf4j
public class StaticShader extends ShaderProgram {

    /**
     * Dateipfad des Vertex Shaders.
     */
    private static final String VERTEX_FILE = SHADER_PATH + "vertexShader.txt";

    /**
     * Dateipfad des Fragment Shaders.
     */
    private static final String FRAGMENT_FILE = SHADER_PATH + "fragmentShader.txt";

    /**
     * Die ID der Uniform Variable der Transformationsmatrix.
     */
    private int location_transformationMatrix;

    /**
     * Die ID der Uniform Variable der Projektionsmatrix.
     */
    private int location_projectionMatrix;

    /**
     * Die ID der Uniform Variable der View Matrix.
     */
    private int location_viewMatrix;

    /**
     * Die ID der Uniform Variable der Position des Lichts.
     */
    private int location_lightPosition;

    /**
     * Die ID der Uniform Variable der Farbe bzw. Intensität des Lichts.
     */
    private int location_lightColor;

    /**
     * Die ID der Uniform Variable des Glanzfaktors.
     */
    private int location_shineDamper;

    /**
     * Die ID der Uniform Variable der Reflektivität.
     */
    private int location_reflectivity;

    /**
     * Die ID der Uniform Variable, die angibt, ob Fake Lighting verwendet werden soll.
     */
    private int location_useFakeLighting;

    /**
     * Die ID der Uniform Variable der Himmelsfarbe.
     */
    private int location_skyColor;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * Binden der verschiedenen Attribute des VAOs an bestimmte Variablen
     * im Shader Programm.
     */
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    /**
     * Laden der IDs aller Uniform Variablen.
     */
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColor = super.getUniformLocation("lightColor");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        location_skyColor = super.getUniformLocation("skyColor");
        log.info("Uniform locations loaded;");
    }

    /**
     * Diese Methode lädt die Himmelsfarbe in die Uniform Variable im Shader.
     *
     * @param r der Rotanteil der Himmelsfarbe.
     * @param g der Grünanteil der Himmelsfarbe.
     * @param b der Blauanteil der Himmelsfarbe.
     */
    public void loadSkyColor(float r, float g, float b) {
        super.loadVector(location_skyColor, new Vector3f(r, g, b));
    }

    /**
     * Diese Methode lädt die Variable, die angibt, ob Fake Lighting verwendet werden soll, in die Uniform Variable im Shader.
     *
     * @param useFake gibt an, ob Fake Lighting verwendet werden soll.
     */
    public void loadFakeLightingVariable(boolean useFake) {
        super.loadBoolean(location_useFakeLighting, useFake);
    }

    /**
     * Diese Methode lädt den Glanzfaktor und die Reflektivität in die Uniform Variable im Shader.
     *
     * @param damper       der Glanzfaktor der Textur.
     * @param reflectivity die Reflektivität der Textur.
     */
    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    /**
     * Diese Methode lädt eine Transformationsmatrix in die Uniform Variable im Shader.
     *
     * @param matrix die Transformationsmatrix, die in die Uniform Variable geladen werden soll.
     */
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }


    /**
     * Diese Methode lädt die Position und Farbe (bzw. Intensität) des Lichts in die Uniform Variable im Shader.
     *
     * @param light das Licht, dessen Position und Farbe (bzw. Intensität) in die Uniform Variable geladen werden soll.
     */
    public void loadLight(Light light) {
        super.loadVector(location_lightPosition, light.getPosition());
        super.loadVector(location_lightColor, light.getColor());
    }

    /**
     * Diese Methode erstellt eine View Matrix und lädt diese in die Uniform Variable im Shader.
     *
     * @param camera die Kamera, für die die View Matrix erstellt werden soll.
     * @see Maths#createViewMatrix(Camera)
     */
    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    /**
     * Diese Methode lädt eine Projektionsmatrix in die Uniform Variable im Shader.
     *
     * @param projection die Projektionsmatrix, die in die Uniform Variable geladen werden soll.
     */
    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }

}
