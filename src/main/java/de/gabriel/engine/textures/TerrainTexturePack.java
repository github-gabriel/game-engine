package de.gabriel.engine.textures;

/**
 * Diese Klasse verwaltet die verschiedenen Texturen, die ein Terrain durch eine Blend Map verwendet.
 * Eine Blend Map besteht aus 4 "Farben", die jeweils eine Textur repräsentieren.
 *
 * @param backgroundTexture die Hintergrundtextur der Blend Map (schwarzer Bereich).
 * @param rTexture          die Textur für die rote Farbe der Blend Map.
 * @param gTexture          die Textur für die grüne Farbe der Blend Map.
 * @param bTexture          die Textur für die blaue Farbe der Blend Map.
 */
public record TerrainTexturePack(TerrainTexture backgroundTexture, TerrainTexture rTexture, TerrainTexture gTexture,
                                 TerrainTexture bTexture) {
}
