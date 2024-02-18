#version 400

in vec3 position;
out vec3 textureCoords; // 3 dimensionale Texturkoordianten zum Samplen der Farbe des Pixels von der Cube Map

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){
	
	gl_Position = projectionMatrix * viewMatrix * vec4(position, 1.0);
	textureCoords = position; // Durch das Samplen der Farbe der Cube Map anhand der 3 dimensionalen Texturkoordianten, gilt Texturkoordianten = (Vertex-) Position
	
}