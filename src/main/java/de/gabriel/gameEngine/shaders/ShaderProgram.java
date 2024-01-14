package de.gabriel.gameEngine.shaders;

import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * Generische Klasse zum Repräsentieren eines Shader Programmes.
 * Für ein spezifischeres Shader Programm muss diese Klasse erweitert werden.
 *
 * @see StaticShader
 * @see TerrainShader
 */
@Slf4j
public abstract class ShaderProgram {

    private final static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16); // Float Buffer für 4 · 4 (=16) Float Matrizen mit homogenen Koordinaten
    private final int programID;
    private final int vertexShaderID;
    private final int fragmentShaderID;

    /**
     * Erstellt ein Shader Programm mit den angegebenen Shadern,
     * um sie ans Programm zu binden und dieses zu linken und
     * zu validieren.
     *
     * @param vertexFile   der Dateipfad des Vertex Shaders.
     * @param fragmentFile der Dateipfad des Fragment Shaders.
     */
    public ShaderProgram(String vertexFile, String fragmentFile) {
        vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
        getAllUniformLocations();

        log.info("Successfully created shader program; {[ShaderProgramId={}], [VertexShaderId={}], [FragmentShaderId={}]}",
                programID, vertexShaderID, fragmentShaderID);
    }

    /**
     * Diese Methode lädt einen Shader aus einer Datei und kompiliert diesen Shader nach
     * dem Auslesen dann mit dem angegebenen Typ.
     *
     * @param file die Datei, aus der der Shader geladen werden soll.
     * @param type der Typ des Shaders, der kompiliert werden soll.
     * @return die ID des kompilierten Shaders.
     * @see GL20#GL_VERTEX_SHADER
     * @see GL20#GL_FRAGMENT_SHADER
     */
    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            log.error("Could not read file!", e);
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            log.error(GL20.glGetShaderInfoLog(shaderID, 500));
            System.exit(-1);
        }
        log.trace("Read shader file; {[ShaderFile={}], [ShaderId={}]}",
                file, shaderID);
        return shaderID;
    }

    /**
     * Diese Methode zwingt die erbenden Klassen, eine Methode zu implementieren,
     * die alle Uniform Variablen, die sich je nach Zweck des Shader Programmes
     * unterscheiden können, des Shaders lädt.
     */
    protected abstract void getAllUniformLocations();

    /**
     * Diese Methode gibt die ID einer Uniform Variable im Shader zurück.
     *
     * @param uniformName der Name der Uniform Variable.
     * @return die ID der Uniform Variable.
     */
    protected int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(programID, uniformName);
    }

    /**
     * Nutzen des Programms als Teil des Rendering-Prozesses.
     */
    public void start() {
        GL20.glUseProgram(programID);
    }

    /**
     * Beenden des Programms als Teil des Rendering-Prozesses, durch das
     * Referenzieren ein invaliden Programmes, wodurch die Ergebnisse der Shader undefiniert sind.
     */
    public void stop() {
        GL20.glUseProgram(0);
    }

    /**
     * Beenden aller Programme und Entbinden und Löschen der Shader sowie des Programmes.
     */
    public void cleanUp() {
        stop();
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);

        log.info("Cleaned up shader program; {[ShaderProgramId={}], [VertexShaderId={}], [FragmentShaderId={}]}",
                programID, vertexShaderID, fragmentShaderID);
    }

    /**
     * Diese Methode bindet die Attribute eines VAOs an das Shader Programm,
     * da die Shader teilweise VAO spezifisch sind und daher nicht immer
     * die selben Attributes teilen.
     */
    protected abstract void bindAttributes();

    /**
     * Eine Methode zum Binden eines Attributes von einem VAO an das Shader Programm.
     *
     * @param attribute    die Nummer des Attributes im VAO, das gebunden werden soll.
     * @param variableName der Name des Attributes an das der Wert im Shader gebunden werden soll.
     */
    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    /**
     * Diese Methode lädt einen Float Wert in eine Uniform Variable im Shader.
     *
     * @param location die ID der Uniform Variable.
     * @param value    der Float Wert, der in die Uniform Variable geladen werden soll.
     */
    protected void loadFloat(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    /**
     * Diese Methode lädt einen Integer Wert in eine Uniform Variable im Shader.
     *
     * @param location die ID der Uniform Variable.
     * @param value    der Integer Wert, der in die Uniform Variable geladen werden soll.
     */
    protected void loadInt(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    /**
     * Diese Methode lädt einen Vektor in eine Uniform Variable im Shader.
     *
     * @param location die ID der Uniform Variable.
     * @param vector   der Vektor, der in die Uniform Variable geladen werden soll.
     */
    protected void loadVector(int location, Vector3f vector) {
        GL20.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    /**
     * Diese Methode lädt einen Boolean Wert in eine Uniform Variable im Shader.
     * Dabei wird der Boolean im Shader als Float repräsentiert. Es gilt:
     *
     * <ul>
     *     <li>0.0f = false</li>
     *     <li>1.0f = true</li>
     * </ul>
     *
     * @param location die ID der Uniform Variable.
     * @param value    der Boolean Wert, der in die Uniform Variable geladen werden soll.
     */
    protected void loadBoolean(int location, boolean value) {
        float toLoad = 0;
        if (value) {
            toLoad = 1;
        }
        GL20.glUniform1f(location, toLoad);
    }

    /**
     * Diese Methode lädt eine Matrix in eine Uniform Variable im Shader.
     *
     * @param location die ID der Uniform Variable.
     * @param matrix   die Matrix, die in die Uniform Variable geladen werden soll.
     */
    protected void loadMatrix(int location, Matrix4f matrix) {
        matrix.get(matrixBuffer);
        GL20.glUniformMatrix4fv(location, false, matrixBuffer);
    }

}
