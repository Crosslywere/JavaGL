package com.crossly.opengl.apps;

import com.crossly.opengl.ShaderProgram;
import com.crossly.opengl.Texture;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class HelloTexture {

	private long window;

	public void run() {
		init();
		loop();
		glfwDestroyWindow(window);
		glfwTerminate();
	}

	private void init() {
		if (!glfwInit())
			throw new RuntimeException("Failed to initialize glfw");

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		window = glfwCreateWindow(800, 600, "Hello Texture", 0, 0);
		if (window == 0)
			throw new RuntimeException("Failed to create window");

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
	}

	private void processInput() {
		if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
			glfwSetWindowShouldClose(window, true); // Close window on escape pressed
		}
	}

	private void loop() {
		GL.createCapabilities();
		ShaderProgram shader = ShaderProgram.builder()
				.vertexShaderFile("shader/texture.vert.glsl")
				.fragmentShaderFile("shader/texture.frag.glsl")
				.compile();
		Texture texture = new Texture("texture/texture.jpg");
		shader.bind();
		texture.bind();
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);
		float[] vertices = {
				-0.5f,  0.5f, 0f, 1f,
				-0.5f, -0.5f, 0f, 0f,
				 0.5f, -0.5f, 1f, 0f,
				 0.5f,  0.5f, 1f, 1f,
		};
		int vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		int[] indices = {
				0, 1, 2,
				2, 3, 0,
		};
		int ebo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, Float.BYTES * 4, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Float.BYTES * 4, Float.BYTES * 2);
		glEnableVertexAttribArray(1);
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT);
			processInput();
			glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
		texture.delete();
		shader.delete();
		glDeleteBuffers(ebo);
		glDeleteBuffers(vbo);
		glDeleteVertexArrays(vao);
	}

	public static void main(String[] args) {
		new HelloTexture().run();
	}
}
