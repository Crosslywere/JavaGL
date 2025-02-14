#version 330 core
layout (location = 0) out vec4 oCol;
varying vec2 texCoord;
uniform sampler2D texture0;
void main() {
    oCol = texture2D(texture0, texCoord);
}