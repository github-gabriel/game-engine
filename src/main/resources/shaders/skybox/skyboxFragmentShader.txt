#version 400

in vec3 textureCoords;
out vec4 out_Color;

uniform samplerCube cubeMap;
uniform samplerCube cubeMap2;
uniform float blendFactor; // Faktor zum Mischen der beiden Skyboxen
uniform vec3 fogColor;

const float lowerLimit = 0.0; // Ab 0 beginnt der Nebel, der langsam in die Farbe der Skybox übergeht, damit die Gegenstände auf die der Nebel schon angewandt wird auch irgendwo rein faden können, anstatt einen Kontrast auf der Skybox zu bilden
const float upperLimit = 30.0; // Bis 30 erscheint der Nebel, darüber entspricht die Farbe des Pixels vollständig der Farbe der Skybox

void main(void){
    vec4 texture1 = texture(cubeMap, textureCoords); // Farbe des Pixels der Skybox 1 bestimmen
    vec4 texture2 = texture(cubeMap2, textureCoords); // Farbe des Pixels der Skybox 2 bestimmen

    vec4 finalColor = mix(texture1, texture2, blendFactor); // Mischen der beiden Skyboxen mit Hilfe des Faktors blendFactor

    float factor = (textureCoords.y - lowerLimit) / (upperLimit - lowerLimit); // Der Faktor, der bestimmt, wie stark der Nebel auf den Pixel angewandt wird
    factor = clamp(factor, 0.0, 1.0); // Der Faktor wird auf 0 bis 1 begrenzt
    out_Color = mix(vec4(fogColor, 1.0), finalColor, factor); // Mixen der Nebelfarbe mit der Farbe des Pixels (Skybox) mit Hilfe des Faktors, der angibt, wie stark der Nebel auf den Pixel angewandt wird
}