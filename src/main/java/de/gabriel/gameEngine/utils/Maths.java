package de.gabriel.gameEngine.utils;

import de.gabriel.gameEngine.entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Eine Klasse zum Umgehen mit Matrizen.
 */
public class Maths {

    /**
     * Die Höhe des Spielers. Die y Position in der View Matrix wird automatisch um diesen Wert reduziert, um die Kamera auf die "Augenhöhe" des Spielers zu setzen.
     */
    private static final int PLAYER_HEIGHT = 7;

    /**
     * Erstellt eine Transformationsmatrix mit den angegebenen Parametern in der Reihenfolge der Parameter.
     *
     * @param translation die Translation, um die der Vertex verschoben werden soll.
     * @param rx          die Rotation um die x-Achse.
     * @param ry          die Rotation um die y-Achse.
     * @param rz          die Rotation um die z-Achse.
     * @param scale       der Skalierungsfaktor auf allen Achsen (x, y, z).
     * @return die erstellte Transformationsmatrix.
     */
    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(translation);
        matrix.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0));
        matrix.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0));
        matrix.rotate((float) Math.toRadians(rz), new Vector3f(1, 0, 1));
        matrix.scale(new Vector3f(scale, scale, scale));
        return matrix;
    }

    /**
     * Erstellt eine View Matrix, die die Bewegung der Kamera simuliert und "das Gegenteil der Projektionsmatrix ist".
     *
     * @param camera die Kamera, für die die View Matrix erstellt werden soll.
     * @return die erstellte View Matrix.
     */
    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0));
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y - PLAYER_HEIGHT, -cameraPos.z); // Negative Kamera-Position, um die Welt in die entgegengesetzte Richtung zu bewegen, um Bewegung simulieren.
        viewMatrix.translate(negativeCameraPos);
        return viewMatrix;
    }

}
