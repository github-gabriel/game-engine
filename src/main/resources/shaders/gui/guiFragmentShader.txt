#version 140

in vec2 textureCoords; // Eingehende Texturkoordianten

out vec4 out_Color; // Ausgehende Farbe des Pixels

uniform sampler2D guiTexture; // Textur

void main(void){

	out_Color = texture(guiTexture,textureCoords); // Farbe des Pixels wird anhand der Texturkoordinaten auf der Textur bestimmt

}