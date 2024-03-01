#version 400 core

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector[6];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

// 2D Texture Sampler für jede Textur der Blend Map (und die Blend Map selbst)
uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColor[6];
uniform vec3 attenuation[6];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void){

    vec3 unitVectorToCamera = normalize(toCameraVector);

	vec4 blendMapColor = texture(blendMap, pass_textureCoordinates); // Farbe des Pixels auf der Blend Map

	float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b); // Menge der Hintergrund Textur auf der Blend Map (Schwarzer Teil)
	vec2 tiledCoords = pass_textureCoordinates * 40.0; // Die tiled Textur Koordinaten

	// Farbe des entsprechenden Pixels auf jeder der Texturen gewichtet durch das Vorkommen der entsprechenden Farbe auf der Blend Map
	vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColor = texture(rTexture,tiledCoords) * blendMapColor.r;
	vec4 gTextureColor = texture(gTexture,tiledCoords) * blendMapColor.g;
	vec4 bTextureColor = texture(bTexture,tiledCoords) * blendMapColor.b;

	vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor; // Gesamtfarbe des Pixels

    vec3 totalDiffuse = vec3(0.0); // Summe der Diffusen Beleuchtungen (von mehreren Lichtquellen)
    vec3 totalSpecular = vec3(0.0); // Summe der Specular Beleuchtungen (von mehreren Lichtquellen)

    vec3 unitNormal = normalize(surfaceNormal);

	for(int i = 0; i < 6; i++){
	    float distance = length(toLightVector[i]);
	    float attenuationFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
    	vec3 unitLightVector = normalize(toLightVector[i]);

    	float nDotl = dot(unitNormal,unitLightVector);
    	float brightness = max(nDotl,0.0);

    	vec3 lightDirection = -unitLightVector;
    	vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);

    	float specularFactor = dot(reflectedLightDirection , unitVectorToCamera);
    	specularFactor = max(specularFactor,0.0);
    	float dampedFactor = pow(specularFactor,shineDamper);
    	totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attenuationFactor;
    	totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attenuationFactor;
	}
	totalDiffuse = max(totalDiffuse, 0.35); // Helligkeit zwischen 35 und 100 %; brightness > 0.35, dadurch Ambient Lighting

    out_Color =  vec4(totalDiffuse,1.0) * totalColor + vec4(totalSpecular,1.0);
	out_Color = mix(vec4(skyColor,1),out_Color,visibility);

}