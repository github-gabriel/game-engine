package de.gabriel.engine.textures;

import java.nio.ByteBuffer;

public record TextureData(ByteBuffer buffer, int width, int height) {
}
