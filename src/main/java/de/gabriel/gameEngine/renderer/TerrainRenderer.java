package de.gabriel.gameEngine.renderer;

import de.gabriel.gameEngine.models.RawModel;
import de.gabriel.gameEngine.shaders.TerrainShader;
import de.gabriel.gameEngine.terrain.Terrain;
import de.gabriel.gameEngine.textures.TerrainTexturePack;
import de.gabriel.gameEngine.utils.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;


/**
 * Rendert ein Terrain in der Szene.
 */
public class TerrainRenderer {

    /**
     * Shader Programm zum Rendern des Terrains.
     */
    private final TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }

    /**
     * Rendert eine Liste an Terrains in der Szene.
     *
     * @param terrains die Terrains, die gerendert werden sollen.
     */
    public void render(List<Terrain> terrains) {
        for (Terrain terrain : terrains) {
            prepareTerrain(terrain);
            loadModelMatrix(terrain);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().vertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();
        }
    }

    /**
     * Bereitet das Terrain zum Rendern vor.
     *
     * @param terrain das Terrain, das zum Rendern vorbereitet werden soll.
     */
    private void prepareTerrain(Terrain terrain) {
        RawModel rawModel = terrain.getModel();
        GL30.glBindVertexArray(rawModel.vaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        bindTextures(terrain);
        shader.loadShineVariables(1, 0);

    }

    /**
     * Bindet die Texturen des Terrains (inklusive der Blend Map) in den OpenGL-Context.
     *
     * <p>
     * Dabei werden die Texturen wie folgt an die Texture Units gebunden:
     *     <ul>
     *         <li>Background Texture: Texture Unit 0</li>
     *         <li>Red Texture: Texture Unit 1</li>
     *         <li>Green Texture: Texture Unit 2</li>
     *         <li>Blue Texture: Texture Unit 3</li>
     *         <li>Blend Map: Texture Unit 4</li>
     *      </ul>
     * </p>
     *
     * @param terrain das Terrain, dessen Texturen gebunden werden sollen.
     */
    private void bindTextures(Terrain terrain) {
        TerrainTexturePack texturePack = terrain.getTexturePack(); // Texturen des Terrains
        // Binden der einzelnen Texturen zu den Texture Units
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.backgroundTexture().textureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.rTexture().textureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.gTexture().textureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.bTexture().textureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().textureID());
    }

    /**
     * Entfernt das texturierte Modell aus dem OpenGL-Context.
     */
    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    /**
     * Lädt die Transformationsmatrix des Terrains in die Uniform Variable im Shader.
     *
     * @param terrain das Terrain, dessen Transformationsmatrix in die Uniform Variable geladen werden soll.
     */
    private void loadModelMatrix(Terrain terrain) {
        Matrix4f transformationMatrix = Maths
                .createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
        shader.loadTransformationMatrix(transformationMatrix);
    }

}