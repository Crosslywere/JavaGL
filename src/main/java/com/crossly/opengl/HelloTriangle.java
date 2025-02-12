package com.crossly.opengl;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

public class HelloTriangle {

	private long window;

	public void run() {
		init();
		loop();
		glfwDestroyWindow(window);
		glfwTerminate();
	}

	private void init() {
		if (!glfwInit())
			throw new RuntimeException("Failed to initialize glfw!");

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		window = glfwCreateWindow(800, 600, "Hello Triangle", 0, 0);
		if (window == 0)
			throw new RuntimeException("Failed to create window!");

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
	}

	private void loop() {
		GL.createCapabilities();
		float[] vertices = {
				 0.0f, 0.5f, 1.0f, 0.0f, 0.0f,
				 0.5f,-0.5f, 0.0f, 1.0f, 0.0f,
				-0.5f,-0.5f, 0.0f, 0.0f, 1.0f,
		};
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, Float.BYTES * 5, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, Float.BYTES * 5, Float.BYTES * 2);
		glEnableVertexAttribArray(1);
		String vertSource = """
				#version 330 core
				layout (location = 0) in vec2 aPos;
				layout (location = 1) in vec3 aCol;
				out vec3 fragColor;
				void main() {
					fragColor = aCol;
					gl_Position = vec4(aPos, 0.0, 1.0);
				}""";
		int vs = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vs, vertSource);
		glCompileShader(vs);
		validateShaderCompilation(vs);
		String fragSource = """
				#version 330 core
				layout (location = 0) out vec4 oCol;
				in vec3 fragColor;
				void main() {
					oCol = vec4(fragColor, 1.0);
				}""";
		int fs = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fs, fragSource);
		glCompileShader(fs);
		validateShaderCompilation(fs);
		int program = glCreateProgram();
		glAttachShader(program, vs);
		glAttachShader(program, fs);
		glLinkProgram(program);
		glDeleteShader(vs);
		glDeleteShader(fs);
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT);
			processInput();
			glUseProgram(program);
			glDrawArrays(GL_TRIANGLES, 0, 3);
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
		glDeleteProgram(program);
		glDeleteBuffers(vbo);
		glDeleteVertexArrays(vao);
	}

	private static void validateShaderCompilation(int shader) {
		int[] success = new int[1];
		glGetShaderiv(shader, GL_COMPILE_STATUS, success);
		if (success[0] == 0) {
			String infoLog = glGetShaderInfoLog(shader);
			System.out.println(infoLog);
		}
	}

	private void processInput() {
		if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
			glfwSetWindowShouldClose(window, true);
	}

	public static void main(String[] args) {
		new HelloTriangle().run();
	}
}
