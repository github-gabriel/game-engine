package de.gabriel.engine.shaders;

import de.gabriel.engine.entities.Camera;
import de.gabriel.engine.entities.Light;
import de.gabriel.engine.utils.Maths;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

import static de.gabriel.engine.Main.SHADER_PATH;

/**
 * Shader für statische Objekte.
 */
@Slf4j
public class StaticShader extends ShaderProgram {

    /**
     * Maximale Anzahl an Lichtern, die ein Entity beeinflussen können.
     * Findet sich wieder in den Shadern unter resources/shaders.
     */
    private static final int MAX_LIGHTS = 6;

    /**
     * Dateipfad des Vertex Shaders.
     */
    private static final String VERTEX_FILE = SHADER_PATH + "vertexShader.glsl";

    /**
     * Dateipfad des Fragment Shaders.
     */
    private static final String FRAGMENT_FILE = SHADER_PATH + "fragmentShader.glsl";

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
    private int[] location_lightPosition;

    /**
     * Die ID der Uniform Variable der Farbe bzw. Intensität des Lichts.
     */
    private int[] location_lightColor;

    /**
     * Die ID der Uniform Variable der Dämpfung des Lichts.
     */
    private int[] location_attenuation;

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

    /**
     * Die ID der Uniform Variable der Anzahl der Reihen eines Texturatlasses.
     */
    private int location_numberOfRows;

    /**
     * Die ID der Uniform Variable des Offset einer Textur innerhalb eines Texturatlasses.
     */
    private int location_offset;

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
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        location_skyColor = super.getUniformLocation("skyColor");
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");

        location_lightPosition = new int[MAX_LIGHTS];
        location_lightColor = new int[MAX_LIGHTS];
        location_attenuation = new int[MAX_LIGHTS];

        for (int i = 0; i < MAX_LIGHTS; i++) {
            location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
            location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
    }

    /**
     * Diese Methode lädt die Anzahl der Reihen eines Texturatlasses in die Uniform Variable im Shader.
     *
     * @param numberOfRows die Anzahl der Reihen eines Texturatlasses.
     */
    public void loadNumberOfRows(int numberOfRows) {
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    /**
     * Diese Methode lädt den Offset einer Textur innerhalb eines Texturatlasses in die Uniform Variable im Shader.
     *
     * @param x der X Offset der Textur innerhalb des Texturatlasses.
     * @param y der Y Offset der Textur innerhalb des Texturatlasses.
     */
    public void loadOffset(float x, float y) {
        super.loadVector(location_offset, new Vector2f(x, y));
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
     * Diese Methode lädt die Position und Farbe (bzw. Intensität) der Lichter in die Uniform Variablen im Shader.
     *
     * @param light die Liste an Lichtern, dessen Position und Farbe (bzw. Intensität) in die Uniform Variablen geladen werden soll.
     */
    public void loadLights(List<Light> light) {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < light.size()) { // Nur Lichter, die in der Szene vorhanden sind, werden geladen.
                super.loadVector(location_lightPosition[i], light.get(i).getPosition());
                super.loadVector(location_lightColor[i], light.get(i).getColor());
                super.loadVector(location_attenuation[i], light.get(i).getAttenuation());
            } else { // Leere Lichter werden mit Position (0, 0, 0) und Farbe (0, 0, 0) geladen.
                super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
                super.loadVector(location_lightColor[i], new Vector3f(0, 0, 0));
                super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
            }
        }
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
