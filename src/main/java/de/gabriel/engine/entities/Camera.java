package de.gabriel.engine.entities;

import de.gabriel.engine.terrain.Terrain;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

/**
 * Repräsentiert die virtuelle Kamera.
 */
@Getter
@Setter
public class Camera {

    /**
     * Die Sensitivität der Maus.
     */
    private static final float MOUSE_SENSITIVITY = 0.2f;

    /**
     * Die Geschwindigkeit der Kamera.
     */
    public static float CAMERA_SPEED = 15f;

    /**
     * Position der Kamera.
     */
    private Vector3f position = new Vector3f(-400, 0, 0);

    /**
     * Drehung der Kamera um die eigene y-Achse.
     */
    private float pitch = 0;

    /**
     * Drehung der Kamera eigene, vertikale z-Achse.
     */
    private float yaw;

    /**
     * Drehung der Kamera um die eigene x-Achse.
     */
    private float roll;

    /**
     * Gibt an, ob die Kamera gerade springt.
     */
    private boolean isJumping = false;

    /**
     * Die Geschwindigkeit, mit der die Kamera springt.
     */
    private float jumpSpeed = 30f;

    /**
     * Die Gravitation, die auf die Kamera wirkt.
     */
    private float gravity = -75f;

    /**
     * Die vertikale Geschwindigkeit der Kamera.
     */
    private float verticalVelocity = 0;

    /**
     * Sorgt für die Rotation der Kamera und dann auch der Rotation
     * (der vertikalen Achse) des Spielers.
     */
    public void move(float dx, float dy) {
        this.pitch += dy * MOUSE_SENSITIVITY;
        this.yaw += dx * MOUSE_SENSITIVITY;

        this.pitch = Math.clamp(this.pitch, -90, 90);

        this.yaw %= 360;

        if (this.yaw < 0) {
            this.yaw += 360;
        }
    }

    /**
     * Bewegt die Kamera nach vorne.
     *
     * @param delta die Zeit, die seit dem letzten Frame vergangen ist.
     */
    public void moveForward(float delta) {
        this.position.x += (float) (delta * CAMERA_SPEED * Math.sin(Math.toRadians(this.yaw)));
        this.position.z -= (float) (delta * CAMERA_SPEED * Math.cos(Math.toRadians(this.yaw)));
    }

    /**
     * Bewegt die Kamera nach hinten.
     *
     * @param delta die Zeit, die seit dem letzten Frame vergangen ist.
     */
    public void moveBackward(float delta) {
        this.position.x -= (float) (delta * CAMERA_SPEED * Math.sin(Math.toRadians(this.yaw)));
        this.position.z += (float) (delta * CAMERA_SPEED * Math.cos(Math.toRadians(this.yaw)));
    }

    /**
     * Bewegt die Kamera nach rechts.
     *
     * @param delta die Zeit, die seit dem letzten Frame vergangen ist.
     */
    public void moveRight(float delta) {
        this.position.x -= (float) (delta * CAMERA_SPEED * Math.sin(Math.toRadians(this.yaw - 90)));
        this.position.z += (float) (delta * CAMERA_SPEED * Math.cos(Math.toRadians(this.yaw - 90)));
    }

    /**
     * Bewegt die Kamera nach links.
     *
     * @param delta die Zeit, die seit dem letzten Frame vergangen ist.
     */
    public void moveLeft(float delta) {
        this.position.x -= (float) (delta * CAMERA_SPEED * Math.sin(Math.toRadians(this.yaw + 90)));
        this.position.z += (float) (delta * CAMERA_SPEED * Math.cos(Math.toRadians(this.yaw + 90)));
    }

    /**
     * Imitiert einen Sprung, indem der Kamera vertikale Geschwindigkeit
     * hinzugefügt wird.
     */
    public void jump() {
        if (!isJumping) {
            isJumping = true;
            verticalVelocity = jumpSpeed;
        }
    }

    /**
     * Aktualisiert die Position der Kamera, was relevant für die
     * Sprungmechanik ist.
     *
     * @param delta die Zeit, die seit dem letzten Frame vergangen ist.
     */
    public void update(float delta, Terrain terrain) {
        float height = terrain.getHeightOfTerrain(position.x, position.z); // Höhe des Terrains an der Position der Kamera

        if (isJumping) {
            position.y += verticalVelocity * delta; // Vertikale Geschwindigkeit für y anwenden
            verticalVelocity += gravity * delta;  // Gravitation für vertikale Geschwindigkeit anwenden ("verringern")

            if (position.y < height) {
                position.y = height;
                isJumping = false; // Nicht mehr am Springen
                verticalVelocity = 0; // Keine vertikale Geschwindigkeit mehr
            }
        } else {
            position.y = height;
        }
    }

}
