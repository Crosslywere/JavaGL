#version 330 core
layout (location = 0) out vec4 oCol;
uniform vec3 color;
uniform vec3 lightPos;
in vec3 normal;
in vec3 fragPos;
void main() {
    vec3 ambient = color * 0.2;
    vec3 lightDir = normalize(lightPos - fragPos);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = diff * color;
    oCol = vec4(clamp(ambient + diffuse, 0.0, 1.0), 1.0);
}