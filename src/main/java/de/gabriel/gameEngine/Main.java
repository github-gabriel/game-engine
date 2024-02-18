package de.gabriel.gameEngine;

import de.gabriel.gameEngine.converter.normals.NormalMappedObjLoader;
import de.gabriel.gameEngine.converter.obj.Loader;
import de.gabriel.gameEngine.converter.obj.ModelData;
import de.gabriel.gameEngine.converter.obj.OBJFileLoader;
import de.gabriel.gameEngine.entities.Camera;
import de.gabriel.gameEngine.entities.Entity;
import de.gabriel.gameEngine.entities.Light;
import de.gabriel.gameEngine.entities.PointLight;
import de.gabriel.gameEngine.gui.GuiRenderer;
import de.gabriel.gameEngine.gui.GuiTexture;
import de.gabriel.gameEngine.input.Input;
import de.gabriel.gameEngine.models.RawModel;
import de.gabriel.gameEngine.models.TexturedModel;
import de.gabriel.gameEngine.renderer.MasterRenderer;
import de.gabriel.gameEngine.renderer.WindowManager;
import de.gabriel.gameEngine.skybox.time.HourlyDayNightCycle;
import de.gabriel.gameEngine.skybox.time.TimeCycle;
import de.gabriel.gameEngine.terrain.Terrain;
import de.gabriel.gameEngine.textures.ModelTexture;
import de.gabriel.gameEngine.textures.TerrainTexture;
import de.gabriel.gameEngine.textures.TerrainTexturePack;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

@Slf4j
public class Main {

    /**
     * Pfad zu den Ressourcen der Applikation.
     */
    public static final String RESOURCES_PATH = "C:/Users/gabriel/Desktop/Coding Projekte/engine/game-engine/src/main/resources/";

    /**
     * Pfad zu den Shadern der Applikation.
     */
    public static final String SHADER_PATH = RESOURCES_PATH + "shaders/";
    private static float delta;

    public static void main(String[] args) throws IOException {
        // Instanzen

        WindowManager windowManager = new WindowManager();
        long window = windowManager.init();

        Loader loader = new Loader();

        TimeCycle dayNightCycle = new HourlyDayNightCycle();

        MasterRenderer renderer = new MasterRenderer(loader, dayNightCycle);

        List<Entity> entities = new ArrayList<Entity>();
        List<Entity> normalMapEntities = new ArrayList<Entity>();

        List<PointLight> pointLights = new ArrayList<PointLight>();

        List<Light> lights = new ArrayList<Light>();

        // Lamp

        ModelData lampData = OBJFileLoader.loadOBJ("lamp");

        RawModel lampModel = loader.loadToVAO(lampData.vertices(), lampData.textureCoords(),
                lampData.normals(), lampData.indices());

        TexturedModel lampTexturedModel = new TexturedModel(lampModel, new ModelTexture(loader.loadTexture("lamp")));

        lampTexturedModel.texture().setShineDamper(10);

        // Stall

        ModelData stallData = OBJFileLoader.loadOBJ("stall");

        RawModel stallModel = loader.loadToVAO(stallData.vertices(), stallData.textureCoords(),
                stallData.normals(), stallData.indices());

        TexturedModel stallTexturedModel = new TexturedModel(stallModel, new ModelTexture(loader.loadTexture("stallTexture")));

        stallTexturedModel.texture().setShineDamper(10);
        stallTexturedModel.texture().setReflectivity(1);

        entities.add(new Entity(stallTexturedModel, new Vector3f(-400, 0, -25), 0, 0, 0, 3));

        // Terrain

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        Terrain terrain = new Terrain(-1, -1, loader, texturePack, blendMap, "heightmap");

        // Barrels

        TexturedModel barrelTexturedModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
                new ModelTexture(loader.loadTexture("barrel")));
        barrelTexturedModel.texture().setNormalMapID(loader.loadTexture("normals/barrelNormal"));
        barrelTexturedModel.texture().setShineDamper(10);
        barrelTexturedModel.texture().setReflectivity(0.5f);

        normalMapEntities.add(new Entity(barrelTexturedModel, new Vector3f(-380, terrain.getHeightOfTerrain(-380, -25) + 5, -25), 0, 0, 0, 1));

        // Pines

        ModelData pineData = OBJFileLoader.loadOBJ("pine");

        RawModel pineModel = loader.loadToVAO(pineData.vertices(), pineData.textureCoords(),
                pineData.normals(), pineData.indices());

        TexturedModel pineTexturedModel = new TexturedModel(pineModel, new ModelTexture(loader.loadTexture("pine")));

        pineTexturedModel.texture().setShineDamper(10);
        pineTexturedModel.texture().setReflectivity(1);

        Random randomPineTrees = new Random();

        for (int i = 0; i < 80; i++) {
            float x = randomPineTrees.nextFloat() * -800;
            float z = randomPineTrees.nextFloat() * -800;
            float y = terrain.getHeightOfTerrain(x, z);
            entities.add(new Entity(pineTexturedModel,
                    new Vector3f(x, y, z), 0, 0, 0, 3));
        }

        // Lamps

        lights.add(new Light(new Vector3f(-400, 0, -400), new Vector3f(0.5f, 0.5f, 0.5f))); // Sun

        pointLights.add(new PointLight(lampTexturedModel, new Vector3f(-300, terrain.getHeightOfTerrain(-300, -50), -50), new Vector3f(0, 0, 0), 1,
                new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
        pointLights.add(new PointLight(lampTexturedModel, new Vector3f(-325, terrain.getHeightOfTerrain(-325, -70), -70), new Vector3f(0, 0, 0), 1,
                new Vector3f(0, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
        pointLights.add(new PointLight(lampTexturedModel, new Vector3f(-350, terrain.getHeightOfTerrain(-350, -62), -62), new Vector3f(0, 0, 0), 1,
                new Vector3f(0, 0, 2), new Vector3f(1, 0.01f, 0.002f)));

        for (PointLight pointLight : pointLights) {
            entities.add(pointLight.getEntity());
            lights.add(pointLight.getLight());
        }

        // Vegetation

        ModelData grassData = OBJFileLoader.loadOBJ("grass");

        RawModel grassModel = loader.loadToVAO(grassData.vertices(), grassData.textureCoords(),
                grassData.normals(), grassData.indices());

        TexturedModel grassTexturedModel = new TexturedModel(grassModel,
                new ModelTexture(loader.loadTexture("grass01")));

        grassTexturedModel.texture().setHasTransparency(true);
        grassTexturedModel.texture().setUseFakeLighting(true);

        ModelData fernData = OBJFileLoader.loadOBJ("fern");

        RawModel fernModel = loader.loadToVAO(fernData.vertices(), fernData.textureCoords(),
                fernData.normals(), fernData.indices());

        TexturedModel fernTexturedModel = new TexturedModel(fernModel,
                new ModelTexture(loader.loadTexture("fern")));

        fernTexturedModel.texture().setHasTransparency(true);
        fernTexturedModel.texture().setNumberOfRows(2);

        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            float x = random.nextFloat() * -800;
            float z = random.nextFloat() * -800;
            float y = terrain.getHeightOfTerrain(x, z);
            entities.add(new Entity(grassTexturedModel,
                    new Vector3f(x, y, z), 0, 0, 0, 3));
        }

        for (int i = 0; i < 500; i++) {
            float x = random.nextFloat() * -800;
            float z = random.nextFloat() * -800;
            float y = terrain.getHeightOfTerrain(x, z);
            entities.add(new Entity(fernTexturedModel, random.nextInt(4),
                    new Vector3f(x, y, z), 0, 0, 0, 1));
        }

        // GUI

        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("lwjgl"), new Vector2f(0.75f, 0.75f), new Vector2f(0.225f, 0.1125f));
        guis.add(gui);

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        // Input

        Camera camera = new Camera();

        Input input = new Input(camera, window);

        // Game-Loop

        float lastUpdateTime = (float) glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            delta = (float) glfwGetTime() - lastUpdateTime;
            lastUpdateTime += delta;

            input.moveCamera(delta);
            camera.update(delta, terrain);

            renderer.addTerrain(terrain);

            for (Entity entity : entities) {
                renderer.processEntity(entity);
            }
            for (Entity entity : normalMapEntities) {
                renderer.processNormalMapEntity(entity);
            }

            renderer.render(lights, camera);

            guiRenderer.render(guis);

            windowManager.update();
        }

        // Clean Up

        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        windowManager.cleanUp();
        input.cleanUp();
    }

    public static float getDeltaTime() {
        return delta;
    }

}