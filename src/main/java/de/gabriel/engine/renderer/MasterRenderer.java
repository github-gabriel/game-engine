package de.gabriel.engine.renderer;

import de.gabriel.engine.converter.Loader;
import de.gabriel.engine.entities.Camera;
import de.gabriel.engine.entities.Entity;
import de.gabriel.engine.entities.Light;
import de.gabriel.engine.models.TexturedModel;
import de.gabriel.engine.shaders.StaticShader;
import de.gabriel.engine.shaders.TerrainShader;
import de.gabriel.engine.skybox.SkyboxRenderer;
import de.gabriel.engine.skybox.time.TimeCycle;
import de.gabriel.engine.terrain.Terrain;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

/**
 * Der MasterRenderer ist zuständig für das gesamte Rendern der Szene.
 */
@Slf4j
public class MasterRenderer {

    /**
     * Das Sichtfeld (Field of View) der Kamera. Ähnlich wie bei einer Kameralinse.
     */
    private static final float FOV = 110;

    /**
     * Der Abstand zur nahen Clipping-Ebene. Alles, was näher als dieser Wert ist, wird nicht mehr gerendert.
     * Dahinter wird jedoch alles bis zur fernen Clipping-Ebene gerendert.
     */
    private static final float NEAR_PLANE = 0.1f;

    /**
     * Der Abstand zur fernen Clipping-Ebene. Alles, was weiter als dieser Wert ist, wird nicht mehr gerendert.
     * Davor wird jedoch alles ab der nahen Clipping-Ebene gerendert.
     */
    private static final float FAR_PLANE = 1000;

    /**
     * Der Rotanteil der Farbe des Himmels.
     */
    public static float RED = 0.3f;

    /**
     * Der Grünanteil der Farbe des Himmels.
     */
    public static float GREEN = 0.4f;

    /**
     * Der Blauanteil der Farbe des Himmels.
     */
    public static float BLUE = 0.5f;

    /**
     * Shader für statische Objekte in der Szene.
     */
    private final StaticShader shader = new StaticShader();

    /**
     * Renderer für Entities in der Szene.
     */
    private final EntityRenderer renderer;

    /**
     * Renderer für Terrain in der Szene.
     */
    private final TerrainRenderer terrainRenderer;

    /**
     * Shader für Terrain in der Szene.
     */
    private final TerrainShader terrainShader = new TerrainShader();

    /**
     * Eine Map mit texturierten Modellen und den dazugehörigen Entities als Liste.
     * Dadurch können alle Entities eines texturierten Modells gerendert werden, ohne
     * dass jedes texturierte Modell aufs neue verarbeitet werden muss.
     *
     * @see TexturedModel
     * @see Entity
     */
    private final Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();

    private final Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();

    /**
     * Eine Liste mit allen Terrains in der Szene. Die Terrains in der Szene sind einzelne Tiles, die
     * jeweils eine feste Größe und Vertex Count haben. Zusammengesetzt werden sie in einem Grid und
     * bilden so das gesamte Terrain.
     *
     * @see Terrain
     */
    private final List<Terrain> terrains = new ArrayList<Terrain>();
    /**
     * Renderer für die Skybox in der Szene.
     */
    private final SkyboxRenderer skyboxRenderer;
    /**
     * Die Projektionsmatrix mit Informationen zur Projektion der 3D-Objekte auf einen 2D Bildschirm.
     */
    private Matrix4f projectionMatrix;
    private Loader loader;

    private NormalMappingRenderer normalMappingRenderer;

    public MasterRenderer(Loader loader, TimeCycle timeCycle) {
        this.loader = loader;
        enableCulling();
        createProjectionMatrix();
        renderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(this.loader, projectionMatrix, timeCycle);
        normalMappingRenderer = new NormalMappingRenderer(projectionMatrix);
    }

    /**
     * Aktiviert das Culling, sodass nur die Vorderseite der Dreiecke (die dessen Normalvektor zur Kamera zeigt) gerendert wird.
     */
    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    /**
     * Deaktiviert das Culling, sodass die Vorder- und Rückseite der Dreiecke gerendert wird.
     */
    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    /**
     * Rendert alle Objekte in der Szene.
     *
     * @param lights die Lichtquellen in der Szene.
     * @param camera die Kamera in der Szene.
     */
    public void render(List<Light> lights, Camera camera) {
        prepare();
        shader.start();
        shader.loadSkyColor(RED, GREEN, BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        normalMappingRenderer.render(normalMapEntities, lights, camera);
        terrainShader.start();
        terrainShader.loadSkyColor(RED, GREEN, BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        skyboxRenderer.render(camera, RED, GREEN, BLUE);
        terrains.clear();
        entities.clear();
        normalMapEntities.clear();
    }

    /**
     * Fügt ein Terrain der Liste von Terrains hinzu.
     * d
     *
     * @param terrain das Terrain, das hinzugefügt werden soll.
     * @see MasterRenderer#terrains
     */
    public void addTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    /**
     * Fügt eine Entity der Entities Map hinzu.
     *
     * @param Entity die Entity, die hinzugefügt werden soll.
     * @see MasterRenderer#entities
     */
    public void processEntity(Entity Entity) {
        TexturedModel entityModel = Entity.getModel(); // Das texturierte Modell der Entity
        List<Entity> batch = entities.get(entityModel); // Batch mit Entities, die dasselbe texturierte Modell verwenden
        if (batch != null) {
            batch.add(Entity); // Entity zum Batch hinzufügen, wenn Batch mit texturiertem Modell existiert
        } else {
            List<Entity> newBatch = new ArrayList<Entity>(); // Neuen Batch erstellen, wenn Batch mit texturiertem Modell noch nicht existiert
            newBatch.add(Entity); // Entity zum neuen Batch hinzufügen
            entities.put(entityModel, newBatch); // Neuen Batch der Entities Map hinzufügen
        }
    }

    public void processNormalMapEntity(Entity Entity) {
        TexturedModel entityModel = Entity.getModel(); // Das texturierte Modell der Entity
        List<Entity> batch = normalMapEntities.get(entityModel); // Batch mit Entities, die dasselbe texturierte Modell verwenden
        if (batch != null) {
            batch.add(Entity); // Entity zum Batch hinzufügen, wenn Batch mit texturiertem Modell existiert
        } else {
            List<Entity> newBatch = new ArrayList<Entity>(); // Neuen Batch erstellen, wenn Batch mit texturiertem Modell noch nicht existiert
            newBatch.add(Entity); // Entity zum neuen Batch hinzufügen
            normalMapEntities.put(entityModel, newBatch); // Neuen Batch der Entities Map hinzufügen
        }
    }

    /**
     * Löscht die Shader Programme beim Beenden der Sitzung.
     *
     * @see StaticShader#cleanUp()
     * @see TerrainShader#cleanUp()
     */
    public void cleanUp() {
        normalMappingRenderer.cleanUp();
        shader.cleanUp();
        terrainShader.cleanUp();
    }

    /**
     * Bereitet die Szene für das Rendern vor.
     */
    public void prepare() {
        updateSkyColor();
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Test um die Tiefenwerte zu vergleichen, sodass die Dreiecke in der richtigen Reihenfolge gerendert werden.
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(RED, GREEN, BLUE, 1);
    }

    private void updateSkyColor() {
        Vector3f skyColor = skyboxRenderer.getCurrentSkyColor();
        RED = skyColor.x;
        GREEN = skyColor.y;
        BLUE = skyColor.z;
    }


    /**
     * Initialisiert die Projektionsmatrix {@link #projectionMatrix}.
     */
    private void createProjectionMatrix() {
        long window = GLFW.glfwGetCurrentContext();

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(window, w, h);
        int width = w.get(0);
        int height = h.get(0);

        float aspectRatio = (float) width / (float) height;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
        projectionMatrix.m33(0);

        log.info("Created projection matrix; {[WindowWidth={}], [WindowHeight={}], [FrustumLength={}], [NearPlane={}], [FarPlane={}], [AspectRatio={}]}",
                width, height, frustum_length, NEAR_PLANE, FAR_PLANE, aspectRatio);
    }


}