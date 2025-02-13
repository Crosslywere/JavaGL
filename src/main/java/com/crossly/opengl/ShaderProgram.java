package com.crossly.opengl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL33.*;

public class ShaderProgram {

	private final int program;

	private final Map<String, Integer> uniformMap;

	private ShaderProgram(int program) {
		this.program = program;
		this.uniformMap = new HashMap<>();
	}

	public static void validateShaderCompilation(int shader) {
		int[] success = new int[1];
		glGetShaderiv(shader, GL_COMPILE_STATUS, success);
		if (success[0] == 0) {
			String infoLog = glGetShaderInfoLog(shader);
			glGetShaderSource(shader);
			System.err.println(infoLog);
		}
	}

	public void delete() {
		glDeleteProgram(program);
	}

	public void bind() {
		glUseProgram(program);
	}

	public void setFloat(String name, float v0) {
		int loc = getUniformLocation(name);
		glUniform1f(loc, v0);
	}

	public void setFloat3(String name, Vector3f v3f) {
		int loc  = getUniformLocation(name);
		glUniform3f(loc, v3f.x, v3f.y, v3f.z);
	}

	public void setMat4(String name, Matrix4f m4x4) {
		int loc = getUniformLocation(name);
		float[] matrix = new float[4 * 4];
		m4x4.get(matrix);
		glUniformMatrix4fv(loc, false, matrix);
	}

	private int getUniformLocation(String name) {
		if (uniformMap.containsKey(name)) {
			return uniformMap.get(name);
		}
		int loc = glGetUniformLocation(program, name);
		if (loc < 0) {
			System.err.println("uniform " + name + " isn't available!");
		}
		uniformMap.put(name, loc);
		return loc;
	}

	public static ShaderProgramBuilder builder() {
		return new ShaderProgramBuilder();
	}

	public static class ShaderProgramBuilder {

		private final int program;

		private ShaderProgramBuilder() {
			program = glCreateProgram();
		}

		public ShaderProgramBuilder vertexShaderSource(String source) {
			int shader = glCreateShader(GL_VERTEX_SHADER);
			glShaderSource(shader, source);
			glCompileShader(shader);
			validateShaderCompilation(shader);
			glAttachShader(program, shader);
			return this;
		}

		public ShaderProgramBuilder vertexShaderFile(String sourceFilePath) {
			try {
				InputStream shaderStream = ShaderProgram.class.getClassLoader().getResourceAsStream(sourceFilePath);
				assert shaderStream != null;
				String shader = new String(shaderStream.readAllBytes());
				shaderStream.close();
				return this.vertexShaderSource(shader);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public ShaderProgramBuilder fragmentShaderSource(String source) {
			int shader = glCreateShader(GL_FRAGMENT_SHADER);
			glShaderSource(shader, source);
			glCompileShader(shader);
			validateShaderCompilation(shader);
			glAttachShader(program, shader);
			return this;
		}

		public ShaderProgramBuilder fragmentShaderFile(String sourceFilePath) {
			try {
				InputStream shaderStream = ShaderProgram.class.getClassLoader().getResourceAsStream(sourceFilePath);
				assert shaderStream != null;
				String shader = new String(shaderStream.readAllBytes());
				shaderStream.close();
				return this.fragmentShaderSource(shader);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public ShaderProgramBuilder geometryShaderSource(String source) {
			int shader = glCreateShader(GL_GEOMETRY_SHADER);
			glShaderSource(shader, source);
			glCompileShader(shader);
			validateShaderCompilation(shader);
			glAttachShader(program, shader);
			return this;
		}

		public ShaderProgramBuilder geometryShaderFile(String sourceFilePath) {
			try {
				InputStream shaderStream = ShaderProgram.class.getClassLoader().getResourceAsStream(sourceFilePath);
				assert shaderStream != null;
				String shader = new String(shaderStream.readAllBytes());
				shaderStream.close();
				return this.geometryShaderSource(shader);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public ShaderProgram compile() {
			glLinkProgram(program);
			return new ShaderProgram(program);
		}
	}
}
