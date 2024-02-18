package de.gabriel.gameEngine.terrain;


import de.gabriel.gameEngine.converter.obj.Loader;
import de.gabriel.gameEngine.models.RawModel;
import de.gabriel.gameEngine.textures.TerrainTexture;
import de.gabriel.gameEngine.textures.TerrainTexturePack;
import de.gabriel.gameEngine.utils.Maths;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static de.gabriel.gameEngine.Main.RESOURCES_PATH;

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
     * Die maximale Höhe des Terrains als Angabe für die Height Map.
     */
    private static final int MAX_HEIGHT = 40;

    /**
     * Die maximale Farbe eines Pixels auf der Height Map.
     * (256, da es für jeden Farbkanal[RGB] Werte von 0 bis 255, also 256 Werte, gibt)
     */
    private static final int MAX_PIXEL_COLOR = 256 * 256 * 256;

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

    /**
     * Eine Tabelle mit der Höhe jeder Vertex.
     */
    private float[][] heights;

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMapFileName) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(loader, heightMapFileName);
    }

    /**
     * Generiert ein Terrain Modell.
     *
     * @param loader            der Loader der final verwendet wird, um ein Modell zu laden.
     * @param heightMapFileName der Name der Height Map, die verwendet werden soll.
     * @return das generierte Terrain Modell.
     */
    private RawModel generateTerrain(Loader loader, String heightMapFileName) {

        BufferedImage image = null;
        try {
            String absolutePath = RESOURCES_PATH + "textures/" + heightMapFileName + ".png";
            image = ImageIO.read(new File(absolutePath));
            log.info("Successfully loaded heightmap: {[FileName={}], [AbsolutePath={}]}",
                    heightMapFileName, absolutePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int VERTEX_COUNT = image.getHeight(); // Vertices einer Reihe entsprechen der Höhe der Height Map (in Pixeln)

        heights = new float[VERTEX_COUNT][VERTEX_COUNT];

        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
                float height = getHeight(j, i, image); // Höhe des Punktes mithilfe der Height Map bestimmen
                heights[j][i] = height;
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(j, i, image); // Normal des Vertex berechnen, der durch die Height Map beeinflusst wird
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
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

    /**
     * Gibt die neuen Normals des durch die Heightmap
     * beeinflussten Terrains zurück.
     *
     * @param x     die X Position der Vertex, für die die Normal berechnet werden soll.
     * @param y     die Y Position der Vertex, für die die Normal berechnet werden soll.
     * @param image die Height Map.
     * @return die neue Normal des Vertex.
     */
    private Vector3f calculateNormal(int x, int y, BufferedImage image) {
        float heightL = getHeight(x - 1, y, image); // Höhe des Punktes links von dem Vertex
        float heightR = getHeight(x + 1, y, image); // Höhe des Punktes rechts von dem Vertex
        float heightD = getHeight(x, y - 1, image); // Höhe des Punktes unter dem Vertex
        float heightU = getHeight(x, y + 1, image); // Höhe des Punktes über dem Vertex
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalize(); // Einheitsvektor
        return normal;
    }

    /**
     * Gibt die Höhe eines von dem Pixel auf der Height Map
     * dargestellten Punktes zurück.
     *
     * @param x     die X Position des Punktes auf der Height Map.
     * @param y     die Y Position des Punktes auf der Height Map.
     * @param image die Height Map.
     * @return die Höhe des Punktes, basierend auf der Farbe des Pixels auf der Height Map.
     */
    private float getHeight(int x, int y, BufferedImage image) {
        if (x < 0 || x >= image.getHeight() || y < 0 || y >= image.getHeight()) { // Positionen befinden sich außerhalb der Height Map
            return 0;
        }
        float height = image.getRGB(x, y);
        height += MAX_PIXEL_COLOR / 2f;
        height /= MAX_PIXEL_COLOR / 2f; // Damit die Höhe zwischen 0 und 1 liegt
        height *= MAX_HEIGHT; // Damit die Höhe nicht zu groß ist
        return height;
    }

    public float getHeightOfTerrain(float worldX, float worldZ) {
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float) heights.length - 1); // Länge einer Seite eines Quadrats des Terrains
        int gridX = (int) Math.floor(terrainX / gridSquareSize); // X Position des Quadrats, in dem sich der Punkt befindet
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize); // Z Position des Quadrats, in dem sich der Punkt befindet
        if (gridX + 1 >= heights.length || gridZ + 1 >= heights.length || gridX < 0 || gridZ < 0) {
            return 0;
        }
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize; // X Position des Punktes innerhalb des Quadrats
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize; // Z Position des Punktes innerhalb des Quadrats
        float answer;
        if (xCoord <= (1 - zCoord)) {
            answer = Maths
                    .barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ], 0), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            answer = Maths
                    .barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                            heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                            heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        return answer;
    }

}