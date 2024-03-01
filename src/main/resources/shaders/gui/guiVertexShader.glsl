#version 140

in vec2 position; // Eingehende Vertex Position in 2D wegen flachen Texturen

out vec2 textureCoords;

uniform mat4 transformationMatrix;

void main(void){

	gl_Position = transformationMatrix * vec4(position, 0.0, 1.0); // Z = 0 = wird auf dem Bildschirm als UI gerendert
	textureCoords = vec2((position.x+1.0)/2.0, 1 - (position.y+1.0)/2.0);
}