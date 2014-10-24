package com.amazon.example.snake.game;


import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class SwipeListener implements OnTouchListener {
	private final GameView view;
	static final int MIN_DISTANCE = 30;
	private float downX, downY, upX, upY;

	public SwipeListener(GameView view) {
		this.view = view;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			downX = event.getX();
			downY = event.getY();
			return true;
		}
		case MotionEvent.ACTION_UP: {
			upX = event.getX();
			upY = event.getY();

			float deltaX = downX - upX;
			float deltaY = downY - upY;

			float absX = Math.abs(deltaX);
			float absY = Math.abs(deltaY);

			boolean isHorizontal = (absX > absY);

			// swipe horizontal?
			if (isHorizontal && absX > MIN_DISTANCE) {
				// left or right
				if (deltaX < 0) {
					view.controller.updateMove(SnakeDirection.RIGHT);
					return true;
				}
				if (deltaX > 0) {
					view.controller.updateMove(SnakeDirection.LEFT);
					return true;
				}
			}

			// swipe vertical?
			if (!isHorizontal && absY > MIN_DISTANCE) {
				// top or down
				if (deltaY < 0) {
					view.controller.updateMove(SnakeDirection.DOWN);
					return true;
				}
				if (deltaY > 0) {
					view.controller.updateMove(SnakeDirection.UP);
					return true;
				}
			}
			

			return true;
		}
		}
		return false;
	}
}
