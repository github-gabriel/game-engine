package de.gabriel.engine.input;

import de.gabriel.engine.entities.Camera;
import de.gabriel.engine.renderer.WindowManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Diese Klasse registriert GLFW Callbacks für die Tastatur und die Maus und bewegt die Kamera
 * mithilfe dieser.
 */
@Getter
@Slf4j
public class Input {

    /**
     * Die Kamera, die bewegt werden soll.
     */
    private final Camera camera;

    /**
     * Der Callback für die Tastatur.
     */
    private final GLFWKeyCallback keyCallback;

    /**
     * Der Callback für die Cursorposition.
     */
    private final GLFWCursorPosCallback cursorPosCallback;

    private boolean isMovingForward = false;
    private boolean isMovingBackward = false;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean isJumping = false;

    /**
     * Der Input Handler erstellt im Konstruktor die Callbacks für die Tastatur und die Maus,
     * um die Eingaben des Spielers zu verarbeiten.
     *
     * @param camera die Kamera, die bewegt werden soll.
     * @param window das Fenster, in dem die Callbacks registriert werden sollen.
     */
    public Input(Camera camera, long window) {
        this.camera = camera;
        keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW.GLFW_KEY_W) {
                    isMovingForward = (action != GLFW.GLFW_RELEASE); // Wenn die Taste gedrückt wird, dann true, sonst false.
                }

                if (key == GLFW.GLFW_KEY_S) {
                    isMovingBackward = (action != GLFW.GLFW_RELEASE);
                }

                if (key == GLFW.GLFW_KEY_D) {
                    isMovingRight = (action != GLFW.GLFW_RELEASE);
                }

                if (key == GLFW.GLFW_KEY_A) {
                    isMovingLeft = (action != GLFW.GLFW_RELEASE);
                }

                if (key == GLFW.GLFW_KEY_SPACE) {
                    isJumping = (action != GLFW.GLFW_RELEASE);
                }

                if (key == GLFW_KEY_LEFT_SHIFT && action == GLFW_PRESS) {
                    Camera.CAMERA_SPEED += 10;
                } else if (key == GLFW_KEY_LEFT_SHIFT && action == GLFW_RELEASE) {
                    Camera.CAMERA_SPEED -= 10;
                }

                if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                    glfwSetWindowShouldClose(window, true);
                }
                if (key == GLFW_KEY_F && action == GLFW_PRESS) {
                    glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, WindowManager.WIDTH, WindowManager.HEIGHT, GLFW_DONT_CARE);
                }
            }
        };

        GLFW.glfwSetKeyCallback(window, keyCallback);

        cursorPosCallback = new GLFWCursorPosCallback() {
            private double lastX, lastY;

            @Override
            public void invoke(long window, double x, double y) {
                double deltaX = x - lastX;
                double deltaY = y - lastY;
                lastX = x;
                lastY = y;

                camera.move((float) deltaX, (float) deltaY);
            }
        };

        GLFW.glfwSetCursorPosCallback(window, cursorPosCallback);
    }

    /**
     * Bewegt die Kamera entsprechend der Tasteneingaben. Muss im GameLoop aufgerufen werden,
     * um delta übergeben zu können und die Bewegungen wiederholt ausführen zu können.
     *
     * @param delta die Zeit, die seit dem letzten Frame vergangen ist.
     */
    public void moveCamera(float delta) {
        if (isMovingForward()) {
            camera.moveForward(delta);
        }
        if (isMovingBackward()) {
            camera.moveBackward(delta);
        }
        if (isMovingRight()) {
            camera.moveRight(delta);
        }
        if (isMovingLeft()) {
            camera.moveLeft(delta);
        }
        if (isJumping()) {
            camera.jump();
        }
    }

    /**
     * Gibt die Callbacks frei, um Speicherlecks zu vermeiden.
     */
    public void cleanUp() {
        keyCallback.free();
        cursorPosCallback.free();
        log.info("Input callbacks cleaned up;");
    }

}
