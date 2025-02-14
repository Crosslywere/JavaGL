package com.crossly.opengl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

	private static final Vector3f WORLD_UP = new Vector3f(0f, 1f, 0f);

	private Vector3f position;
	private final Vector3f front = new Vector3f();
	private Vector3f right;

	private float pitch = 0f;
	private float yaw = 90f;

	public Camera() {
		position = new Vector3f(0f, 0f, -4f);
		update();
	}

	private void update() {
		front.x = (float)(Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		front.y = (float)(Math.sin(Math.toRadians(pitch)));
		front.z = (float)(Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		front.normalize();
		right = new Vector3f(front).cross(WORLD_UP).normalize();
	}

	public Matrix4f getViewMat() {
		Vector3f center = new Vector3f(position).add(front);
		return new Matrix4f().lookAt(position, center, WORLD_UP);
	}

	public Vector3f getPosition() {
		return new Vector3f(position);
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getFront() {
		return new Vector3f(front);
	}

	public Vector3f getUp() {
		return new Vector3f(WORLD_UP);
	}

	public Vector3f getRight() {
		return new Vector3f(right);
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = Math.clamp(pitch, -89f, 89f);
		update();
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
		update();
	}
}
