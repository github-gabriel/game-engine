#version 400 core

in vec2 pass_textureCoordinates;
in vec3 toLightVector[6];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D modelTexture;
uniform sampler2D normalMap;
uniform vec3 lightColor[6];
uniform vec3 attenuation[6];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void){

    vec4 normalMapValue = 2.0 * texture(normalMap,pass_textureCoordinates) - 1.0; // In Normal Map kodierter Normale auf Bereich -1 bis 1

	vec3 unitNormal = normalize(normalMapValue.rgb); // Normale normalisieren
	vec3 unitVectorToCamera = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for(int i=0;i<6;i++){
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
		vec3 unitLightVector = normalize(toLightVector[i]);	
		float nDotl = dot(unitNormal,unitLightVector);
		float brightness = max(nDotl,0.0);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
		float specularFactor = dot(reflectedLightDirection , unitVectorToCamera);
		specularFactor = max(specularFactor,0.0);
		float dampedFactor = pow(specularFactor,shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColor[i])/attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i])/attFactor;
	}
	totalDiffuse = max(totalDiffuse, 0.2);
	
	vec4 textureColor = texture(modelTexture,pass_textureCoordinates);
	if(textureColor.a<0.5){
		discard;
	}

	out_Color =  vec4(totalDiffuse,1.0) * textureColor + vec4(totalSpecular,1.0);
	out_Color = mix(vec4(skyColor,1.0),out_Color, visibility);

}