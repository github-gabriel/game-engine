package de.gabriel.gameEngine.skybox;

import de.gabriel.gameEngine.converter.obj.Loader;
import de.gabriel.gameEngine.entities.Camera;
import de.gabriel.gameEngine.models.RawModel;
import de.gabriel.gameEngine.skybox.time.TimeCycle;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class SkyboxRenderer {

    /**
     * Die Größe der Skybox.
     */
    private static final float SIZE = 500f;

    /**
     * Vertex Positionen der Skybox als Würfel.
     */
    private static final float[] VERTICES = {
            -SIZE, SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            -SIZE, SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, SIZE
    };

    private static final String[] TEXTURE_FILES = {"skybox/day/right", "skybox/day/left", "skybox/day/top", "skybox/day/bottom", "skybox/day/back", "skybox/day/front"};
    private static final String[] NIGHT_TEXTURE_FILES = {"skybox/night/right", "skybox/night/left", "skybox/night/top", "skybox/night/bottom", "skybox/night/back", "skybox/night/front"};

    private final RawModel cube;
    private final int texture;
    private final int nightTexture;
    private final SkyboxShader shader;
    private final TimeCycle timeCycle;
    private final Vector3f NIGHT_COLOR;
    private final Vector3f MORNING_COLOR;

    public SkyboxRenderer(Loader loader, Matrix4f projectionMatrix, TimeCycle timeCycle) {
        cube = loader.loadToVAO(VERTICES, 3);
        texture = loader.loadCubeMap(TEXTURE_FILES);
        nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES);

        MORNING_COLOR = new Vector3f(0.3f, 0.4f, 0.5f);
        NIGHT_COLOR = new Vector3f(0, 0, 0);

        this.timeCycle = timeCycle;
        shader = new SkyboxShader();
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Camera camera, float r, float g, float b) {
        shader.start();
        shader.loadViewMatrix(camera);
        shader.loadFogColor(r, g, b);
        GL30.glBindVertexArray(cube.vaoID());
        GL20.glEnableVertexAttribArray(0);
        bindTextures();
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, cube.vertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    /**
     * Lädt die Tag/Nacht Skybox Texturen in die entsprechenden Texture Units und
     * lädt den Blend-Faktor für die beiden Skyboxen als Uniform-Variable in den Shader.
     * <p>
     * Dabei werden die Tag/Nacht Skybox Texturen wie folgt an die Texture Units gebunden:
     * <ul>
     *      <li>Tag Skybox Texture: Texture Unit 0</li>
     *      <li>Nacht Skybox Texture: Texture Unit 1</li>
     * </ul>
     * </p>
     */
    private void bindTextures() {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, nightTexture);
        shader.loadBlendFactor(timeCycle.getTime());
    }

    public Vector3f getCurrentSkyColor() {
        float timeFactor = timeCycle.getTime();
        Vector3f interpolatedColor = new Vector3f(MORNING_COLOR);
        return interpolatedColor.lerp(NIGHT_COLOR, timeFactor);
    }

}
