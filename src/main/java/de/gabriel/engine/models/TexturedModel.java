package de.gabriel.engine.models;


import de.gabriel.engine.textures.ModelTexture;

/**
 * Dieser Record repräsentiert ein Modell mit Daten (VAO als {@link RawModel}) und einer Textur.
 *
 * @param rawModel Das {@link RawModel} repräsentiert das VAO als Modell.
 * @param texture  Die {@link ModelTexture} repräsentiert die Textur des Modells.
 */
public record TexturedModel(RawModel rawModel, ModelTexture texture) {
}
