package de.gabriel.engine.renderer;


import de.gabriel.engine.entities.Camera;
import de.gabriel.engine.entities.Entity;
import de.gabriel.engine.entities.Light;
import de.gabriel.engine.models.RawModel;
import de.gabriel.engine.models.TexturedModel;
import de.gabriel.engine.shaders.NormalMappingShader;
import de.gabriel.engine.textures.ModelTexture;
import de.gabriel.engine.utils.Maths;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

public class NormalMappingRenderer {

    private NormalMappingShader shader;

    public NormalMappingRenderer(Matrix4f projectionMatrix) {
        this.shader = new NormalMappingShader();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render(Map<TexturedModel, List<Entity>> entities, List<Light> lights, Camera camera) {
        shader.start();
        prepare(lights, camera);
        for (TexturedModel model : entities.keySet()) {
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            for (Entity Entity : batch) {
                prepareInstance(Entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.rawModel().vertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel();
        }
        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }

    private void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.rawModel();
        GL30.glBindVertexArray(rawModel.vaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        ModelTexture texture = model.texture();
        shader.loadNumberOfRows(texture.getNumberOfRows());
        if (texture.isHasTransparency()) {
            MasterRenderer.disableCulling();
        }
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.texture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.texture().getNormalMapID());
    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity Entity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(Entity.getPosition(), Entity.getRotX(),
                Entity.getRotY(), Entity.getRotZ(), Entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(Entity.getTextureXOffset(), Entity.getTextureYOffset());
    }

    private void prepare(List<Light> lights, Camera camera) {
        shader.loadSkyColor(MasterRenderer.RED, MasterRenderer.GREEN, MasterRenderer.BLUE);
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);

        shader.loadLights(lights, viewMatrix);
        shader.loadViewMatrix(viewMatrix);
    }

}
