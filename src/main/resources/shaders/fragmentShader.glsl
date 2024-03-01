//FRAGMENT SHADER

#version 400 core // GLSL Version 4.0

// INPUTS (Output vom Vertex Shader)
in vec2 pass_textureCoordinates; // Eingehende Textur Koordinaten vom Vertex Shader
in vec3 surfaceNormal; // Eingehende Normalen vom Vertex Shader
in vec3 toLightVector[6]; // Eingehender Vektor vom Vertex zur Lichtquelle vom Vertex Shader
in vec3 toCameraVector; // Eingehender Vektor vom Vertex zur Kamera vom Vertex Shader
in float visibility; // Eingehende Sichtbarkeit vom Vertex Shader

// OUTPUT (Farbe des Pixels, RGBA)
out vec4 out_Color;

uniform sampler2D modelTexture; // Textur
uniform vec3 lightColor[6]; // Farbe und Intensität des Lichts
uniform vec3 attenuation[6];
uniform float shineDamper; // Uniform Variable die den Glanz des Objekts bestimmt
uniform float reflectivity; // Uniform Variable die die Reflektivität des Objekts bestimmt
uniform vec3 skyColor; // Uniform Variable die die Farbe des Himmels bestimmt

void main(void){

	vec3 unitNormal = normalize(surfaceNormal); // Vertex Normal als Einheitsvektor
	vec3 unitVectorToCamera = normalize(toCameraVector); // Vektor zur Kamera als Einheitsvektor

    vec3 totalDiffuse = vec3(0.0); // Summe der Diffusen Beleuchtungen (von mehreren Lichtquellen)
    vec3 totalSpecular = vec3(0.0); // Summe der Specular Beleuchtungen (von mehreren Lichtquellen)

	for(int i = 0; i < 6; i++){ // Für alle Lichtquellen ausführen
	    float distance = length(toLightVector[i]); // Abstand zur Lichtquelle
	    float attenuationFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance); // Attenuation, "Lichtdämpfung"
	    vec3 unitLightVector = normalize(toLightVector[i]); // Vektor zur Lichtquelle als Einheitsvektor
    	float nDotl = dot(unitNormal, unitLightVector); // Skalarprodukt zwischen der Normalen und dem Vektor zur Lichtquelle
    	float brightness = max(nDotl, 0.0);
    	vec3 lightDirection = -unitLightVector; // Vektor des einfallenden Lichts (Gegenteil des Vektors zur Lichtquelle)
    	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal); // Reflektiertes Licht
    	float specularFactor = dot(reflectedLightDirection, unitVectorToCamera); // Skalarprodukt zwischen dem reflektierten Licht und dem Vektor zur Kamera
    	specularFactor = max(specularFactor,0.0); // specularFactor > 0
    	float dampedFactor = pow(specularFactor, shineDamper); // Glanz des Objekts
    	totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attenuationFactor; // Finale "Helligkeit" des Pixels
    	totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attenuationFactor; // Finale Reflektion
	}
	totalDiffuse = max(totalDiffuse, 0.35); // Helligkeit zwischen 35 und 100 %; brightness > 0.35, dadurch Ambient Lighting

	vec4 textureColor = texture(modelTexture, pass_textureCoordinates); // Farbe des Pixels entsprechend der Farbe desselben Pixels auf der Textur setzen
	if(textureColor.a<0.5){
		discard; // Wenn der Alpha Wert des Pixels kleiner als 0.5 ist, wird der Pixel "verworfen" bzw. nicht gerendert
	}

	out_Color = vec4(totalDiffuse, 1) * textureColor + vec4(totalSpecular, 1) ;
	out_Color = mix(vec4(skyColor,1),out_Color,visibility); // Mixen der Farbe des Pixels mit der Farbe des Himmels, abhängig von der Sichtbarkeit
	
}