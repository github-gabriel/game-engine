package engineTester;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	public static void main(String[] args) {

		// Instanzen

		DisplayManager.createDisplay();

		Loader loader = new Loader();

		MasterRenderer renderer = new MasterRenderer();

		Light light = new Light(new Vector3f(100, 0, 0), new Vector3f(1, 1, 1));

		List<Entity> entities = new ArrayList<Entity>();

		// Grass

		ModelData grassData = OBJFileLoader.loadOBJ("grassModel");

		RawModel grassModel = loader.loadToVAO(grassData.getVertices(), grassData.getTextureCoords(),
				grassData.getNormals(), grassData.getIndices());

		TexturedModel grassTexturedModel = new TexturedModel(grassModel,
				new ModelTexture(loader.loadTexture("grass01")));

		grassTexturedModel.getTexture().setHasTransparency(true);
		grassTexturedModel.getTexture().setUseFakeLighting(true);

		Random random = new Random();

		for (int i = 0; i < 100; i++) {
			entities.add(new Entity(grassTexturedModel,
					new Vector3f(random.nextFloat() * -800, 0, random.nextFloat() * -800), 0, 0, 0, 3));
		}
		for (int i = 0; i < 100; i++) {
			entities.add(new Entity(grassTexturedModel,
					new Vector3f(random.nextFloat() * 800, 0, random.nextFloat() * -800), 0, 0, 0, 3));
		}

		// Player

		ModelData playerData = OBJFileLoader.loadOBJ("player");

		RawModel playerModel = loader.loadToVAO(playerData.getVertices(), playerData.getTextureCoords(),
				playerData.getNormals(), playerData.getIndices());

		TexturedModel playerTexturedModel = new TexturedModel(playerModel,
				new ModelTexture(loader.loadTexture("white")));

		playerTexturedModel.getTexture().setShineDamper(0);
		playerTexturedModel.getTexture().setReflectivity(0);

		Player player = new Player(playerTexturedModel, new Vector3f(0, 0, 0), 0, 0, 0, 1);

		Camera camera = new Camera(player);
		
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

		while (!Display.isCloseRequested()) {

			camera.move();
			player.move();
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);

			for (Entity entity : entities) {
				renderer.processEntity(entity);
			}

			renderer.render(light, camera);

			DisplayManager.updateDisplay();

		}

		// Clean Up

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

}