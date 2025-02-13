// Geometry shader that calculates the normal
#version 330 core
layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;
uniform mat4 model;
out vec3 normal;
out vec3 fragPos;
void main() {
	vec3 T = vec3(gl_in[0].gl_Position) - vec3(gl_in[1].gl_Position); // Tangent
	vec3 B = vec3(gl_in[0].gl_Position) - vec3(gl_in[2].gl_Position); // BiNormal
	for (int i = 0; i < 3; i++) {
		gl_Position = gl_in[i].gl_Position;
		normal = vec3(transpose(inverse(model)) * vec4(normalize(cross(B, T)), 1.0));
        fragPos = vec3(transpose(inverse(model)) * gl_Position);
		EmitVertex();
	}
}