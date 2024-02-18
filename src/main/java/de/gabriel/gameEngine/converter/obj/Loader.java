package de.gabriel.gameEngine.converter.obj;

import de.gabriel.gameEngine.Main;
import de.gabriel.gameEngine.models.RawModel;
import de.gabriel.gameEngine.textures.TextureData;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static de.gabriel.gameEngine.Main.RESOURCES_PATH;
import static org.lwjgl.stb.STBImage.stbi_load;

/**
 * Das Ziel des Loaders ist es VBOs in einem VAO zu speichern und dieses mit der zugehörigen
 * ID als {@link RawModel} mit der Anzahl der Vertices zurückzugeben.
 *
 * @see RawModel
 */
@Slf4j
public class Loader {

    /**
     * Eine Liste, der in der Sitzung erstellten VAOs, um diese beim Beenden der Sitzung zu löschen.
     *
     * @see Loader#cleanUp()
     */
    private final List<Integer> vaos = new ArrayList<Integer>();

    /**
     * Eine Liste, der in der Sitzung erstellten VBOs, um diese beim Beenden der Sitzung zu löschen.
     *
     * @see Loader#cleanUp()
     */
    private final List<Integer> vbos = new ArrayList<Integer>();

    /**
     * Eine Liste, der in der Sitzung erstellten Texturen, um diese beim Beenden der Sitzung zu löschen.
     *
     * @see Loader#cleanUp()
     */
    private final List<Integer> textures = new ArrayList<Integer>();

    /**
     * Erstellt ein VAO mit den VBOs, wofür das VAO am Ende unbounded werden muss und dann als {@link RawModel}
     * mit der Anzahl der Vertices und der VAO ID zurückgegeben wird.
     *
     * @param positions     die Positionen der Vertices.
     * @param textureCoords die Texture Coordinates des Vertex.
     * @param normals       die Normals der Vertices.
     * @param indices       der Index-Buffer.
     * @return Ein {@link RawModel} mit der zugehörigen VAO ID und der Anzahl der Vertices.
     */
    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();

        log.info("Successfully created VAO; {[VaoId={}], [VertexPositionLength={}], [TextureCoordsLength={}], [NormalCoordsLength={}], [Indices={}]}",
                vaoID, positions.length, textureCoords.length, normals.length, indices.length);

        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        storeDataInAttributeList(3, 3, tangents);
        unbindVAO();

        log.info("Successfully created VAO; {[VaoId={}], [VertexPositionLength={}], [TextureCoordsLength={}], [NormalCoordsLength={}], [Indices={}]}",
                vaoID, positions.length, textureCoords.length, normals.length, indices.length);

        return new RawModel(vaoID, indices.length);
    }

    /**
     * Erstellt ein VAO mit den VBOs, wofür das VAO am Ende unbounded werden muss und dann als {@link RawModel}
     * mit der Anzahl der Vertices und der VAO ID zurückgegeben wird.
     *
     * @param positions die Positionen der Vertices.
     * @return Ein {@link RawModel} mit der zugehörigen VAO ID und der Anzahl der Vertices.
     */
    public RawModel loadToVAO(float[] positions, int dimensions) {
        int vaoID = createVAO();
        this.storeDataInAttributeList(0, dimensions, positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length / dimensions);
    }

    /**
     * Diese Methode lädt eine Textur und gibt die ID der Textur zurück.
     * Dabei wird immer von einer PNG Datei für die Textur ausgegangen.
     *
     * @param fileName der Dateiname der Textur (PNG).
     * @return die ID der Textur.
     */
    public int loadTexture(String fileName) {
        TextureData image = decodeTexture(fileName);

        int id = GL11.glGenTextures();

        textures.add(id);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        if (image != null) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.width(), image.height(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image.buffer());
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -2.5f);
            STBImage.stbi_image_free(image.buffer());
        } else {
            log.error("Failed to load texture; {[TextureId={}], [TextureFileName={}]}",
                    id, fileName);
            System.exit(-1);
        }

        log.info("Successfully loaded texture; {[TextureId={}], [TextureWidth={}], [TextureHeight={}], [TextureFileName={}]}",
                id, image.width(), image.height(), fileName);

        return id;
    }

    public int loadCubeMap(String[] textureFiles) {
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

        for (int i = 0; i < textureFiles.length; i++) {
            TextureData image = decodeTexture(textureFiles[i]);
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, // inkrementieren von GL_TEXTURE_CUBE_MAP_POSITIVE_X, da die weiteren Seiten ebenfalls Integer sind, die aufeinander folgen
                    0, GL11.GL_RGBA8, image.width(), image.height(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image.buffer());
            STBImage.stbi_image_free(image.buffer());
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        textures.add(texID);

        log.info("Successfully loaded cube map; {[CubeMapId={}], [TextureFiles={}]}",
                texID, textureFiles);

        return texID;
    }

    /**
     * Diese Methode lädt eine Textur. Dabei wird immer von einer PNG Datei für die Textur ausgegangen.
     *
     * @param fileName der Dateiname der Textur (PNG).
     * @return die ID der Textur.
     */
    public TextureData decodeTexture(String fileName) {
        int width, height;
        ByteBuffer image;
        String path = RESOURCES_PATH + "textures/" + fileName + ".png";

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            image = stbi_load(path, w, h, comp, 4);

            width = w.get();
            height = h.get();
        }

        if (image == null) {
            log.error("Failed to load texture; {[TextureWidth={}], [TextureHeight={}], [TextureFileName={}], [AbsolutePath={}]}",
                    width, height, fileName, path);
        }

        log.info("Successfully loaded texture; {[TextureWidth={}], [TextureHeight={}], [TextureFileName={}], [AbsolutePath={}]}",
                width, height, fileName, path);

        return new TextureData(image, width, height);
    }

    /**
     * Diese Methode löscht alle VAOs, VBOs und Texturen, die in der Sitzung erstellt wurden,
     * nach dem Beenden der Sitzung.
     *
     * @see Main#main(String[])
     */
    public void cleanUp() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL15.glDeleteBuffers(vbo);
        }
        for (int texture : textures) {
            GL11.glDeleteTextures(texture);
        }
        log.info("Cleaned up all VAOs, VBOs and Textures;");
    }

    /**
     * Diese Methode erstellt zuerst ein leeres VAO und merkt sich die ID dieses VAOs dann, worüber es dann aktiviert wird.
     *
     * @return Die ID des VAOs.
     */
    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    /**
     * Speichert VBOs in der Attribute List des VAOs. Zum Ende wird
     * das VBO noch unbounded, damit es später beim Bearbeiten von
     * weiteren VBOs nicht zu falschen Zuweisungen kommt.
     *
     * @param attributeNumber die Nummer des Attributes, wo das VBO gespeichert werden soll.
     * @param coordinateSize  die Größe der Daten, die gespeichert werden sollen.
     * @param data            die Daten, die als VBO als Attribut des VAOs gespeichert werden.
     */
    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        log.trace("Successfully stored data in attribute list of VAO; {[AttributeNumber={}], [CoordinateSize={}], [VboId={}], [DataLength={}]}",
                attributeNumber, coordinateSize, vboID, data.length);
    }

    /**
     * Diese Methode unbounded das aktuelle VAO, um zu verhindern, dass beim Erstellen eines neuen VAOs immer noch das alte gebundene VAO bearbeitet wird.
     */
    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }


    /**
     * Diese Methode speichert Indices in einem VBO, der dann dem VAO hinzugefügt wird.
     *
     * @param indices die Indices, die in dem VBO gespeichert werden sollen.
     */
    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }


    /**
     * Diese Methoden erstellt einen Int-Buffer, der dann die Daten für das VBO hält.
     *
     * @param data die Daten, die in den Buffer gespeichert werden sollen.
     * @return der Int-Buffer mit den Daten.
     * @see IntBuffer
     * @see Loader#bindIndicesBuffer(int[])
     */
    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Diese Methoden erstellt einen Float-Buffer, der dann die Daten für das VBO hält.
     *
     * @param data die Daten, die in den Buffer gespeichert werden sollen.
     * @return der Float-Buffer mit den Daten.
     * @see FloatBuffer
     * @see Loader#storeDataInAttributeList(int, int, float[])
     */
    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

}
