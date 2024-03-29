//VERTEX SHADER

#version 400 core // GLSL Version 4.0

// INPUTS (VAO)
in vec3 position; // Vertex-Position
in vec2 textureCoordinates; // Textur Koordinaten
in vec3 normal; // Vertex-Normals


// OUTPUTS
out vec2 pass_textureCoordinates; // Ausgehende Textur Koordinaten
out vec3 surfaceNormal; // Ausgehende Vertex-Normals
out vec3 toLightVector[6]; // Ausgehender Vektoren zur Lichtquelle (Entity kann von 6 Lichtquellen beeinflusst werden)
out vec3 toCameraVector; // Vektor von der Vertex-Position zur Kamera
out float visibility; // Sichtbarkeit der Vertex (abhängig von der Distanz zur Kamera); Simuliert Nebel

uniform mat4 transformationMatrix; // Entity spezielle Transformation Matrix
uniform mat4 projectionMatrix; // Projektionsmatrix mit Infos über die Kamera
uniform mat4 viewMatrix; // View Matrix zum Bewegen der Welt für eine Illusion der Kamera-Bewegung
uniform vec3 lightPosition[6]; // Position der Lichtquellen

uniform float useFakeLighting; // Gibt an, ob die Normals "gefaked" werden sollen

uniform float numberOfRows; // Anzahl der Textur Reihen für Texturatlasse
uniform vec2 offset; // Offset für die Textur im Texturatlas

const float density = 0.003; // Dichte des Nebels
const float gradient = 6; // Gradient des Nebels

void main(void){

	vec4 worldPosition = transformationMatrix * vec4(position,1.0); // Homogene Koordinate 1, da Position; Transformation der Positionen mit der Transformation Matrix -> Position im Weltkoordinatensystem
	vec4 positionRelativeToCam = viewMatrix * worldPosition; // Vertex Position relativ zur Kamera
	gl_Position = projectionMatrix * positionRelativeToCam;  // Position der zu rendernden Vertex
	pass_textureCoordinates = (textureCoordinates/numberOfRows) + offset; // Eingehende Textur Koordinaten an Fragment Shader weitergeben (werden dabei interpoliert); Textur Koordinaten für Texturatlasse (wenn nötig) berechnen

	vec3 actualNormal = normal; // Originale Normals, falls die Normals für Fake Lighting gefaked werden müssen

	if(useFakeLighting > 0.5){
		actualNormal = vec3(0,1,0); // Normals "faken", falls Fake Lighting verwendet wird; Normals zeigen nach oben (y-Achse)
	}

	surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz; // Homogene Koordinate 0, da Normalen ein Richtungsvektor sind; Transformation der Normalen mit der Transformation Matrix

	for(int i = 0; i < 6; i++){ // Für alle Lichtquellen ausführen
        toLightVector[i] = lightPosition[i] - worldPosition.xyz; // Vektor von der Vertex-Position zur Lichtquelle
    }
	toCameraVector = (inverse(viewMatrix) * vec4(0,0,0,1)).xyz - worldPosition.xyz; // Invertieren der View Matrix um die Kamera Position (Projektionsmatrix) zu erhalten, dann abziehen von der Vertex-Position im Weltkoordinatensystem, um den Vektor zur Kamera zu erhalten

	float distance = length(positionRelativeToCam.xyz); // Distanz vom Vertex zur Kamera
	visibility = exp(-pow((distance*density),gradient)); // Sichtbarkeit der Vertex berechnen; Sichtbarkeit nimmt mit steigender Entfernung zur Kamera ab
	visibility = clamp(visibility,0,1); // Sichtbarkeit auf einen Wert von 0 bis 1 begrenzen
	
}