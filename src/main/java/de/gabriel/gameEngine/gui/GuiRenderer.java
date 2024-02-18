package de.gabriel.gameEngine.gui;

import de.gabriel.gameEngine.converter.obj.Loader;
import de.gabriel.gameEngine.models.RawModel;
import de.gabriel.gameEngine.utils.Maths;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class GuiRenderer {

    /**
     * Das Rechteck, auf dem die GUI Elemente als Textur gerendert werden.
     */
    private final RawModel quad;

    private GuiShader shader;

    public GuiRenderer(Loader loader) {
        float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1}; // Nutzen von Triangle Strips; Automatische Generation von Dreiecken nach Angaben der nächsten Vertex für das nächste Dreieck
        quad = loader.loadToVAO(positions, 2);
        shader = new GuiShader();
    }

    /**
     * Rendert die GUI Elemente.
     *
     * @param guis die Texturen, die als GUI Elemente gerendert werden sollen.
     */
    public void render(List<GuiTexture> guis) {
        shader.start();
        GL30.glBindVertexArray(quad.vaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        for (GuiTexture gui : guis) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.textureID());

            Matrix4f matrix = Maths.createTransformationMatrix(gui.position(), gui.scale());
            shader.loadTransformation(matrix);

            GL20.glDrawArrays(GL20.GL_TRIANGLE_STRIP, 0, quad.vertexCount()); // Rendert das GUI Element
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }

}
