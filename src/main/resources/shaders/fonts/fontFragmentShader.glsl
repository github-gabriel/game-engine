#version 330

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform vec3 color;
uniform sampler2D fontAtlas;

const float width = 0.5; // Abstand zwischen der Mitte des Buchstaben und dem Rand
const float edge = 0.1; // Breite des (zusätzlichen) Randes des Buchstaben

const float borderWidth = 0.7; // Abstand zwischen der Mitte des Buchstaben und der Outline
//const float borderWidth = 0.4; // Glowing Effect auf Grund der kurzen Breite der Outline
const float borderEdge = 0.1; // Breite des Randes der Outline
//const float borderEdge = 0.5; // Glowing Effect auf Grund des breiten Übergangs der Outline

const vec3 outlineColor = vec3(0.0, 0.0, 1.0); // Farbe der Outline

// Für Offset der Border einen vec2 Offset erstellen und diesen auf die Texture Coordinates addieren -> Padding bei Überschneidungen erhöhen

void main(void) {

    float distance = 1.0 - texture(fontAtlas, pass_textureCoords).a; // Abstand von der Mitte des Buchstaben zum aktuellen Fragment
    float alpha = 1.0 - smoothstep(width, width + edge, distance); // Übergang von dem Rand des Buchstaben zum (zusätzlichen) Rand des Buchstaben

    float distance2 = 1.0 - texture(fontAtlas, pass_textureCoords).a; // Abstand von der Mitte des Buchstaben zum aktuellen Fragment
    float outlineAlpha = 1.0 - smoothstep(borderWidth, borderWidth + borderEdge, distance2); // Übergang vom Anfang der Outline zum Ende der Outline des Buchstaben

    float finalAlpha = alpha + (1.0 - alpha) * outlineAlpha;

    vec3 finalColor = mix(outlineColor, color, alpha / finalAlpha);

    out_Color = vec4(finalColor, finalAlpha);

}