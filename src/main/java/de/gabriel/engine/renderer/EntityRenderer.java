package de.gabriel.engine.renderer;

import de.gabriel.engine.entities.Entity;
import de.gabriel.engine.models.RawModel;
import de.gabriel.engine.models.TexturedModel;
import de.gabriel.engine.shaders.StaticShader;
import de.gabriel.engine.textures.ModelTexture;
import de.gabriel.engine.utils.Maths;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

/**
 * Rendert alle Entities in der Szene. Optimiert mit Batches für Entities mit derselben Textur
 * (und möglicherweise unterschiedlichen Transformationen).
 */
@Slf4j
public class EntityRenderer {

    /**
     * Das Shader Programm für statische Objekte, bzw. Entities.
     */
    private final StaticShader shader;

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    /**
     * Rendert die Entities die dasselbe {@link TexturedModel} verwenden in Batches,
     * um die Anzahl der Aufrufe von OpenGL zu minimieren.
     *
     * @param entities die Entities und ihr texturiertes Modell, die gerendert werden sollen.
     */
    public void render(Map<TexturedModel, List<Entity>> entities) {
        for (TexturedModel model : entities.keySet()) { // Für jedes texturierte Modell
            prepareTexturedModel(model); // Bereite das texturierte Modell vor
            List<Entity> batch = entities.get(model); // Batch mit Entities, die dasselbe texturierte Modell verwenden
            for (Entity Entity : batch) { // Für jedes Entity in dem Batch
                prepareInstance(Entity); // Bereite das Entity vor
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.rawModel().vertexCount(), GL11.GL_UNSIGNED_INT, 0); // Rendert das Entity
            }
            unbindTexturedModel();
        }
    }

    /**
     * Bereitet das texturierte Modell zum Rendern vor.
     *
     * @param model das texturierte Modell, das zum Rendern vorbereitet werden soll.
     */
    private void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.rawModel();
        GL30.glBindVertexArray(rawModel.vaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        ModelTexture texture = model.texture();
        shader.loadNumberOfRows(texture.getNumberOfRows());
        if (texture.isHasTransparency()) {
            MasterRenderer.disableCulling(); // Backface Culling für Objekte mit transparenten Texturen deaktivieren
        }
        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.texture().getTextureID());
    }

    /**
     * Entfernt das texturierte Modell aus dem OpenGL-Context.
     */
    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    /**
     * Bereitet die Instanz einer Entity vor, indem die Transformations-Matrix für diese Entities geladen wird
     * und der optionale Offset für Texturatlasse geladen wird.
     *
     * @param Entity das Entity, für das die Transformation geladen werden soll.
     */
    private void prepareInstance(Entity Entity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(Entity.getPosition(), Entity.getRotX(),
                Entity.getRotY(), Entity.getRotZ(), Entity.getScale());

        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(Entity.getTextureXOffset(), Entity.getTextureYOffset());
    }

}
