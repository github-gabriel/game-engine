package de.gabriel.gameEngine.terrain;


import de.gabriel.gameEngine.models.RawModel;
import de.gabriel.gameEngine.renderEngine.Loader;
import de.gabriel.gameEngine.textures.TerrainTexture;
import de.gabriel.gameEngine.textures.TerrainTexturePack;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Repräsentiert ein Terrain in der Szene.
 */
@Getter
@Slf4j
public class Terrain {

    /**
     * Größe des Terrains.
     */
    private static final float SIZE = 800;

    /**
     * Anzahl der Vertices pro Seite.
     */
    private static final int VERTEX_COUNT = 128;

    /**
     * X Position des Terrains.
     */
    private final float x;

    /**
     * Z Position des Terrains.
     */
    private final float z;

    /**
     * Das Modell des Terrains.
     */
    private final RawModel model;

    /**
     * Ein Paket an Texturen, die das Terrain verwenden wird.
     */
    private final TerrainTexturePack texturePack;

    /**
     * Die Blend Map des Terrains, die angibt, wo die verschiedenen Texturen gerendert werden.
     */
    private final TerrainTexture blendMap;

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(loader);
    }

    /**
     * Generiert ein Terrain Modell.
     *
     * @param loader der Loader der final verwendet wird, um ein Modell zu laden.
     * @return das generierte Terrain Modell.
     */
    private RawModel generateTerrain(Loader loader) {
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
                vertices[vertexPointer * 3 + 1] = 0;
                vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2] = 0;
                textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        log.info("Generated terrain; {[VertexCount={}], [TextureCoordsLength={}], [NormalsCoordsLength={}], [IndicesLength={}]}",
                vertices.length, textureCoords.length, normals.length, indices.length);
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

}