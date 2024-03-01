package de.gabriel.engine.converter.normals;

import de.gabriel.engine.converter.Loader;
import de.gabriel.engine.models.RawModel;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.gabriel.engine.Main.RESOURCES_PATH;


public class NormalMappedObjLoader {

    private static final List<VertexNM> vertices = new ArrayList<VertexNM>();

    public static RawModel loadOBJ(String objFileName, Loader loader) {
        FileReader isr = null;

        File objFile = new File(RESOURCES_PATH + "models/" + objFileName + ".obj");

        try {
            isr = new FileReader(objFile);
        } catch (IOException e) {
            System.err.println("File not found in res; don't use any extention");
        }

        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Integer> indices = new ArrayList<Integer>();

        BufferedReader reader = new BufferedReader(isr);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(" ");
                switch (split[0]) {
                    case "v" -> {
                        Vector3f vertex = new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
                        VertexNM newVertex = new VertexNM(vertices.size(), vertex);
                        vertices.add(newVertex); // Vertex passes breakpoint -> Doesn't contain NaN
                    }
                    case "vt" -> {
                        Vector2f texture = new Vector2f(Float.parseFloat(split[1]), Float.parseFloat(split[2]));
                        textures.add(texture);
                    }
                    case "vn" -> {
                        Vector3f normal = new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
                        normals.add(normal);
                    }
                    case "f" -> {
                        String[] vertex1 = split[1].split("/"); // Checking the list here reveals NaNs in the list
                        String[] vertex2 = split[2].split("/");
                        String[] vertex3 = split[3].split("/");
                        VertexNM v0 = processVertex(vertex1, vertices, indices);
                        VertexNM v1 = processVertex(vertex2, vertices, indices);
                        VertexNM v2 = processVertex(vertex3, vertices, indices);
                        calculateTangents(v0, v1, v2, textures);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file");
        } finally {
            try {
                reader.close();
                isr.close();
            } catch (IOException e) {
                System.err.println("Error closing the stuff");
            }
        }

        removeUnusedVertices(vertices);

        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float[] tangentsArray = new float[vertices.size() * 3];
        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
                texturesArray, normalsArray, tangentsArray);
        int[] indicesArray = convertIndicesListToArray(indices);

        return loader.loadToVAO(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray);
    }

    private static void calculateTangents(VertexNM v0, VertexNM v1, VertexNM v2,
                                          List<Vector2f> textures) {
        Vector3f deltaPos1 = v1.getPosition().sub(v0.getPosition(), new Vector3f());
        Vector3f deltaPos2 = v2.getPosition().sub(v0.getPosition(), new Vector3f());

        Vector2f uv0 = textures.get(v0.getTextureIndex());
        Vector2f uv1 = textures.get(v1.getTextureIndex());
        Vector2f uv2 = textures.get(v2.getTextureIndex());

        Vector2f deltaUv1 = uv1.sub(uv0, new Vector2f());
        Vector2f deltaUv2 = uv2.sub(uv0, new Vector2f());

        float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);

        deltaPos1.mul(deltaUv2.y);
        deltaPos2.mul(deltaUv1.y);
        Vector3f tangent = deltaPos1.sub(deltaPos2, new Vector3f());
        tangent.mul(r);
        // Tangent ist für alle Vertices gleich
        v0.addTangent(tangent);
        v1.addTangent(tangent);
        v2.addTangent(tangent);
    }

    private static VertexNM processVertex(String[] vertex, List<VertexNM> vertices,
                                          List<Integer> indices) {
        int index = (Integer.parseInt(vertex[0])) - 1;
        VertexNM currentVertex = vertices.get(index); // This vertex sometimes contains NaNs
        int textureIndex = (Integer.parseInt(vertex[1])) - 1;
        int normalIndex = (Integer.parseInt(vertex[2])) - 1;

        if (currentVertex.isNotSet()) {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
            return currentVertex;
        } else {
            return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
                    vertices);
        }
    }

    private static int[] convertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }

    private static float convertDataToArrays(List<VertexNM> vertices, List<Vector2f> textures,
                                             List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
                                             float[] normalsArray, float[] tangentsArray) {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            VertexNM currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            Vector3f tangent = currentVertex.getAverageTangent();
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoord.x;
            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
            tangentsArray[i * 3] = tangent.x;
            tangentsArray[i * 3 + 1] = tangent.y;
            tangentsArray[i * 3 + 2] = tangent.z;
        }
        return furthestPoint;
    }

    private static VertexNM dealWithAlreadyProcessedVertex(VertexNM previousVertex, int newTextureIndex,
                                                           int newNormalIndex, List<Integer> indices, List<VertexNM> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex()); // NaNs are in the list
            return previousVertex;
        } else {
            VertexNM anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex,
                        newNormalIndex, indices, vertices);
            } else {
                VertexNM duplicateVertex = previousVertex.duplicate(vertices.size());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
                return duplicateVertex;
            }
        }
    }

    private static void removeUnusedVertices(List<VertexNM> vertices) {
        for (VertexNM vertex : vertices) {
            vertex.averageTangents();
            if (vertex.isNotSet()) {
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }
}