package de.gabriel.gameEngine.objConverter;

/**
 * Dieser Record repräsentiert die Daten eines Models in Form eines VAOs. Anders als bei dem {@link de.gabriel.gameEngine.models.RawModel} Record,
 * hält dieser Record die tatsächlichen Attribute des VAOs. Verwendet wird dieser Record zum Laden von Models aus .obj Dateien mit Hilfe der
 * {@link de.gabriel.gameEngine.objConverter.OBJFileLoader} Klasse.
 *
 * @param vertices      die Vertices des Models
 * @param textureCoords die Textur Koordinaten des Models
 * @param normals       die Normalen des Models
 * @param indices       die Indices des Models
 * @param furthestPoint
 */
public record ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, float furthestPoint) {
}