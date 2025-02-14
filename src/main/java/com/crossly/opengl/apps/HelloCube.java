package com.crossly.opengl.apps;

import com.crossly.opengl.Camera;
import com.crossly.opengl.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class HelloCube {

	private long window;

	private final Camera camera = new Camera();

	private final Vector2f mousePos = new Vector2f(0f);

	float deltaTime = 0f;

	public void run() {
		init();
		loop();
		glfwDestroyWindow(window); // Destroying the window
		glfwTerminate(); // Terminating the glfw instance
	}

	private void init() {
		if (!glfwInit()) // Validating glfw initialization
			throw new RuntimeException("Failed to initialize GLFW!");

		// Setting the opengl version to version 3.3 core
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // No resizing the window
		window = glfwCreateWindow(800, 600, "Hello Cube", 0, 0); // Creating the window
		if (window == 0) // Validating the window creation
			throw new RuntimeException("Failed to create window!");

		glfwMakeContextCurrent(window); // Setting the window as the opengl context
		glfwSwapInterval(1); // Vsync on
		glfwSetCursorPosCallback(window, (window, mx, my) -> {
			// Calculating the offsets
			float xOffset = (float)mx - mousePos.x;
			float yOffset = mousePos.y - (float)my;
			mousePos.x = (float)mx;
			mousePos.y = (float)my;
			if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED); // Hide the mouse whilst the left mouse is pressed
				// incrementing the offsets based on time delta
				camera.setYaw(camera.getYaw() + (xOffset * deltaTime));
				camera.setPitch(camera.getPitch() + (yOffset * deltaTime));
			} else {
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL); // Unhide the mouse when the left mouse is released
			}
		});
	}

	private void processInput() {
		if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
			glfwSetWindowShouldClose(window, true); // Close window on escape pressed
		}
		Vector3f movement = new Vector3f();
		float speed = 2.5f;
		if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
			movement.add(camera.getFront()); // Move forward
		}
		if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
			movement.sub(camera.getFront()); // Move backward
		}
		if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
			movement.sub(camera.getRight()); // Move left
		}
		if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
			movement.add(camera.getRight()); // Move right
		}
		movement.normalize();
		if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
			movement.add(camera.getUp()); // Move up
		}
		if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
			movement.sub(camera.getUp()); // Move down
		}
		if (movement.length() > 0) {
			var pos = camera.getPosition().add(movement.mul(speed * deltaTime));
			camera.setPosition(pos);
		}
	}

	private void loop() {
		GL.createCapabilities(true);
		int vao = glGenVertexArrays(); // Creating the vertex array object
		glBindVertexArray(vao); // Creating the vertex buffer object
		float[] vertices = {
				0.5f,  0.5f, -0.5f,
				0.5f, -0.5f, -0.5f,
			   -0.5f, -0.5f, -0.5f,
			   -0.5f,  0.5f, -0.5f,

				0.5f,  0.5f,  0.5f,
				0.5f, -0.5f,  0.5f,
			   -0.5f, -0.5f,  0.5f,
			   -0.5f,  0.5f,  0.5f,
		};
		int vbo = glGenBuffers(); // Creating the vertex buffer object
		glBindBuffer(GL_ARRAY_BUFFER, vbo); // Binding the vertex buffer object
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW); // Passing the vertex data to the GPU
		int[] indices = {
				0, 1, 2,
				2, 3, 0,
				// ^ Back
				4, 7, 6,
				6, 5, 4,
				// ^ Front
				4, 5, 1,
				1, 0, 4,
				// ^ Left
				7, 3, 2,
				2, 6, 7,
				// ^ Right
				0, 3, 7,
				7, 4, 0,
				// ^ Top
				2, 1, 5,
				5, 6, 2,
				// ^ Bottom
		};
		int ebo = glGenBuffers(); // Creating the element buffer object
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo); // Binding the element buffer object
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW); // Adding indices data
		glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES * 3, 0); // Specifying the vertex layout for position data
		glEnableVertexAttribArray(0); // Enabling the vertex layout for position data
		ShaderProgram shader = ShaderProgram.builder()
				.vertexShaderFile("shader/cube.vert.glsl")   // Attaching the vertex shader
				.geometryShaderFile("shader/cube.geom.glsl") // Attaching the geometry shader
				.fragmentShaderFile("shader/cube.frag.glsl") // Attaching the fragment shader
				.compile(); // Creating the shader from all the attached shader parts
		shader.bind(); // Setting this as the active shader
		shader.setFloat3("lightPos", new Vector3f(1f, 2f, 0.5f));
		shader.setFloat3("color", new Vector3f(1f, 0.5f, 0.13f)); // The color of the cube
		shader.setMat4("projection", new Matrix4f().perspective((float)Math.toRadians(45f), 4f / 3f, 0.1f, 100.0f)); // The rendering projection matrix
		glEnable(GL_CULL_FACE); // Enabling face culling
		glCullFace(GL_BACK); // Cull all clockwise winding order rendered face
		double past = glfwGetTime(); // Storing the time for delta calculation
		while (!glfwWindowShouldClose(window)) {
			// Calculating the time delta
			double now = glfwGetTime();
			deltaTime = (float)(now - past);
			past = now;
			processInput(); // Processing inputs
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clearing the color channel and the depth channel
			shader.setMat4("view", camera.getViewMat()); // The rendering view matrix
			shader.setMat4("model", new Matrix4f()); // The model matrix
			glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0); // Drawing the cube
			glfwSwapBuffers(window); // Swap the front and back buffers
			glfwPollEvents(); // Polling for window events
		}
		shader.delete(); // Deleting the shader
		glDeleteBuffers(ebo); // Deleting the element buffer object
		glDeleteBuffers(vbo); // Deleting the vertex buffer object
		glDeleteVertexArrays(vao); // Deleting the vertex array object
	}

	public static void main(String[] args) {
		new HelloCube().run();
	}
}
