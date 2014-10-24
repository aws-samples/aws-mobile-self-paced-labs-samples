package com.amazon.example.snake.game;

public enum SnakeDirection {
	UP(0), DOWN(1), LEFT(3), RIGHT(4);

	private final int i;

	SnakeDirection(int i) {
		this.i = i;
	}
	

	public int asInt() {
		return i;
	}
}
