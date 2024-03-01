package de.gabriel.engine.fonts.mesh;

/**
 * Stores the vertex data for all the quads on which a text will be rendered.
 *
 * @author Karl
 */
public class TextMeshData {

    private float[] vertexPositions;
    private float[] textureCoords;

    protected TextMeshData(float[] vertexPositions, float[] textureCoords) {
        this.vertexPositions = vertexPositions;
        this.textureCoords = textureCoords;
    }

    public float[] getVertexPositions() {
        return vertexPositions;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public int getVertexCount() {
        return vertexPositions.length / 2;
    }

}
