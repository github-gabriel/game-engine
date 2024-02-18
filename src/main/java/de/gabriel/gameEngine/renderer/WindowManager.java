package de.gabriel.gameEngine.renderer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glViewport;

@Slf4j
@Getter
public class WindowManager {

    /**
     * Die Breite des Displays.
     */
    public static int WIDTH = 1280;

    /**
     * Die Höhe des Displays.
     */
    public static int HEIGHT = 720;

    /**
     * Die maximalen FPS.
     */
    private final int FPS_CAP = 120;

    /**
     * Der Titel des Displays.
     */
    private final String TITLE = "Display";

    /**
     * Gibt an, ob V-Sync aktiviert ist.
     */
    private final boolean V_SYNC = true;

    /**
     * Der GLFW Window Handle.
     */
    private long window;

    /**
     * Der {@link GLFWWindowSizeCallback} um den Viewport an die
     * neue Größe des Fensters im Falle von einem Resize Event anzupassen.
     */
    private GLFWWindowSizeCallback windowSizeCallback;

    /**
     * Initialisiert das Fenster.
     *
     * @return den Window Handle als long
     */
    public long init() {
        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

        glfwSetErrorCallback(errorCallback);

        glfwInit();

        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, 0, 0);

        if (window == 0) {
            throw new RuntimeException("Failed to create window");
        }

        glfwMakeContextCurrent(window);

        if (V_SYNC) {
            glfwSwapInterval(1);
        }

        glfwShowWindow(window);
        GL.createCapabilities();

        addCallbacks();

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glViewport(0, 0, WIDTH, HEIGHT);

        log.info("Successfully created the window; {[Width={}], [Height={}], [FpsCap={}], [Title={}], [VSync={}]}",
                WIDTH, HEIGHT, FPS_CAP, TITLE, V_SYNC);

        return window;
    }

    /**
     * Fügt die Callbacks für das Fenster hinzu.
     * In diesem Fall nur den {@link GLFWWindowSizeCallback},
     * der den Viewport an die neue Größe des Fensters im Falle von einem Resize Event anpasst.
     */
    private void addCallbacks() {
        windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                WIDTH = width;
                HEIGHT = height;
                glViewport(0, 0, WIDTH, HEIGHT);
                glfwInit();
                log.trace("Viewport successfully updated to new size; [Width={}], [Height={}]", WIDTH, HEIGHT);
            }
        };

        glfwSetWindowSizeCallback(window, windowSizeCallback);
    }

    /**
     * Tauscht Front und Back Buffer des Fensters aus und ruft
     * die Events ab. Muss pro Frame aufgerufen werden.
     */
    public void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    /**
     * Gibt den {@link GLFWWindowSizeCallback} frei, zerstört das Fenster
     * und terminiert GLFW.
     */
    public void cleanUp() {
        windowSizeCallback.free();
        glfwDestroyWindow(window);
        glfwTerminate();
        log.info("Freed Window Size Callback, destroyed window and terminated GLFW;");
    }

}
