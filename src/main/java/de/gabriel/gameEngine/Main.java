package de.gabriel.gameEngine;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import de.gabriel.gameEngine.entities.Camera;
import de.gabriel.gameEngine.entities.Entity;
import de.gabriel.gameEngine.entities.Light;
import de.gabriel.gameEngine.input.Input;
import de.gabriel.gameEngine.models.RawModel;
import de.gabriel.gameEngine.models.TexturedModel;
import de.gabriel.gameEngine.objConverter.ModelData;
import de.gabriel.gameEngine.objConverter.OBJFileLoader;
import de.gabriel.gameEngine.renderEngine.Loader;
import de.gabriel.gameEngine.renderEngine.MasterRenderer;
import de.gabriel.gameEngine.renderEngine.WindowManager;
import de.gabriel.gameEngine.terrain.Terrain;
import de.gabriel.gameEngine.textures.ModelTexture;
import de.gabriel.gameEngine.textures.TerrainTexture;
import de.gabriel.gameEngine.textures.TerrainTexturePack;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

@Slf4j
public class Main {

    /**
     * Pfad zu den Ressourcen der Applikation.
     */
    public static final String RESOURCES_PATH = "D:/Windows/Desktop/Coding Projekte/engine/game-engine/src/main/resources/";

    /**
     * Pfad zu den Shadern der Applikation.
     */
    public static final String SHADER_PATH = "D:/Windows/Desktop/Coding Projekte/engine/game-engine/src/main/resources/shaders/";

    public static void main(String[] args) throws IOException {
        // Instanzen

        WindowManager windowManager = new WindowManager();
        long window = windowManager.init();

        Loader loader = new Loader();

        MasterRenderer renderer = new MasterRenderer();

        Light light = new Light(new Vector3f(100, 0, 0), new Vector3f(2, 2, 2));

        List<Entity> entities = new ArrayList<Entity>();

        // Stall

        ModelData stallData = OBJFileLoader.loadOBJ("stall");

        RawModel stallModel = loader.loadToVAO(stallData.vertices(), stallData.textureCoords(),
                stallData.normals(), stallData.indices());

        TexturedModel stallTexturedModel = new TexturedModel(stallModel, new ModelTexture(loader.loadTexture("stallTexture")));

        stallTexturedModel.texture().setShineDamper(10);
        stallTexturedModel.texture().setReflectivity(1);

        entities.add(new Entity(stallTexturedModel, new Vector3f(0, 0, -25), 0, 0, 0, 3));

        // Dragon

        ModelData dragonData = OBJFileLoader.loadOBJ("dragon");

        RawModel dragonModel = loader.loadToVAO(dragonData.vertices(), dragonData.textureCoords(),
                dragonData.normals(), dragonData.indices());

        TexturedModel dragonTexturedModel = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture("white")));

        dragonTexturedModel.texture().setShineDamper(5);
        dragonTexturedModel.texture().setReflectivity(10);

        Entity dragonEntity = new Entity(dragonTexturedModel, new Vector3f(50, 0, -25), 0, 0, 0, 2);

        entities.add(dragonEntity);

        // Grass

        ModelData grassData = OBJFileLoader.loadOBJ("grassModel");

        RawModel grassModel = loader.loadToVAO(grassData.vertices(), grassData.textureCoords(),
                grassData.normals(), grassData.indices());

        TexturedModel grassTexturedModel = new TexturedModel(grassModel,
                new ModelTexture(loader.loadTexture("grass01")));

        grassTexturedModel.texture().setHasTransparency(true);
        grassTexturedModel.texture().setUseFakeLighting(true);

        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            entities.add(new Entity(grassTexturedModel,
                    new Vector3f(random.nextFloat() * -800, 0, random.nextFloat() * -800), 0, 0, 0, 3));
        }
        for (int i = 0; i < 100; i++) {
            entities.add(new Entity(grassTexturedModel,
                    new Vector3f(random.nextFloat() * 800, 0, random.nextFloat() * -800), 0, 0, 0, 3));
        }

        Camera camera = new Camera();

        Input input = new Input(camera, window);

        // Terrain

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        Terrain terrain = new Terrain(-1, -1, loader, texturePack, blendMap);
        Terrain terrain2 = new Terrain(0, -1, loader, texturePack, blendMap);

        // Game-Loop

        float lastUpdateTime = (float) glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            dragonEntity.increaseRotation(0, 0.1f, 0);

            float delta = (float) glfwGetTime() - lastUpdateTime;
            lastUpdateTime += delta;

            input.moveCamera(delta);
            camera.update(delta);

            renderer.addTerrain(terrain);
            renderer.addTerrain(terrain2);

            for (Entity entity : entities) {
                renderer.processEntity(entity);
            }

            renderer.render(light, camera);

            windowManager.update();
        }

        // Clean Up

        renderer.cleanUp();
        loader.cleanUp();
        windowManager.cleanUp();
        input.cleanUp();
    }

}