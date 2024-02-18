#version 400 core

in vec3 position; // Vertex-Positionen
in vec2 textureCoords; // UV Koordinaten
in vec3 normal; // Vertex-Normals

out vec2 pass_textureCoordinates;
out vec3 surfaceNormal;
out vec3 toLightVector[6];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[6];

const float density = 0.0025;
const float gradient = 5;

void main(void){

	vec4 worldPosition = transformationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	pass_textureCoordinates = textureCoords;

	surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;

	for(int i = 0; i < 6; i++){
	    toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}

	toCameraVector = (inverse(viewMatrix) * vec4(0,0,0,1)).xyz - worldPosition.xyz;

	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density),gradient));
	visibility = clamp(visibility,0,1);
	
}
