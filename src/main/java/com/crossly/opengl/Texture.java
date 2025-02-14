package com.crossly.opengl;

import java.net.URL;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

	private final int textureId;

	private final int width;

	private final int height;

	public Texture(String textureFilePath) {
		int[] w = new int[1], h = new int[1], ch = new int[1];
		String filepath = textureFilePath;
		URL url = Texture.class.getClassLoader().getResource(textureFilePath);
		if (url != null)
			filepath = url.getPath().substring(1);

		ByteBuffer data = stbi_load(filepath, w, h, ch, 0);
		if (data == null)
			throw new RuntimeException("Texture error : " + stbi_failure_reason() + " with " + textureFilePath);

		width = w[0];
		height = h[0];
		textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
		glGenerateMipmap(GL_TEXTURE_2D);
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, textureId);
	}

	public void bind(int index) {
		glActiveTexture(GL_TEXTURE0 + index);
		bind();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void delete() {
		glDeleteTextures(textureId);
	}
}
