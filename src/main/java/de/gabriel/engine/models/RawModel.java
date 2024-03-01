package de.gabriel.engine.models;

/**
 * Dieser Record repräsentiert ein VAO mit einer einzigartigen VAO ID und einem Vertex Count.
 *
 * @param vaoID       Die VAO ID ist die ID des VAOs, welches das Modell repräsentiert.
 * @param vertexCount Der Vertex Count gibt an, wie viele Vertices sich in dem Modell befinden.
 */
public record RawModel(int vaoID, int vertexCount) {
}
