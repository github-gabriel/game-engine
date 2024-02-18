package de.gabriel.gameEngine.gui;

import org.joml.Vector2f;

/**
 * Repräsentiert eine Textur, die als GUI Element auf einem einfachen Rechteck auf
 * dem Bildschirm gerendert wird.
 *
 * @param textureID die ID der Textur
 * @param position  die Position des GUI Elements auf dem Bildschirm
 * @param scale     die Skalierung (x, y) der Textur
 */
public record GuiTexture(int textureID, Vector2f position, Vector2f scale) {
}