/**
 * re:Invent Snake!
 * In-App Purchasing Code Challenge
 *
 * � 2012, Amazon.com, Inc. or its affiliates.
 * All Rights Reserved.
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.amazon.example.snake.game;

import java.io.InputStream;
import java.util.ArrayList;

import com.amazon.example.snake.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.View;

enum SoundID {
	BITE, CRASH, APPEAR;
}

public class GameView extends View {
	GameController controller;
	int screenWidth = 0;
	int screenHeight = 0;
	int blocksWide = 0;
	int blocksHigh = 0;
	int blockSize = 32;

	Point ul = new Point();
	Point lr = new Point();

	// Bitmaps
	Bitmap bg = null;
	Bitmap block = null;
	Bitmap apple = null;
	Bitmap body = null;
	Bitmap[] head = new Bitmap[5];

	// Sounds
	SoundPool sounds = null;
	int biteSound;
	int crashSound;
	int appearSound;

	private boolean initialized = false;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		controller = GameController.CONTROLLER;
		controller.setView(this);

		// loadBitmaps();
		// loadSounds();

	}

	public void loadBitmaps() {
		// load assets (bitmaps, etc.)
		block = loadBitmap(R.drawable.snake_block);
		apple = loadBitmap(R.drawable.snake_apple);
		body = loadBitmap(R.drawable.snake_body);
		head[0] = loadBitmap(R.drawable.snake_head_u);
		head[1] = loadBitmap(R.drawable.snake_head_d);
		head[3] = loadBitmap(R.drawable.snake_head_l);
		head[4] = loadBitmap(R.drawable.snake_head_r);
	}

	private Bitmap loadBitmap(int imageId) {
		InputStream is = this.getResources().openRawResource(imageId);
		return BitmapFactory.decodeStream(is);
	}

	public void loadBitmaps(String directory) {
		// load assets (bitmaps, etc.)
		bg = loadBitmapFromFile(directory, "snake_bg.png");
		block = loadBitmapFromFile(directory, "snake_block.png");
		apple = loadBitmapFromFile(directory, "snake_apple.png");
		body = loadBitmapFromFile(directory, "snake_body.png");
		head[0] = loadBitmapFromFile(directory, "snake_head_u.png");
		head[1] = loadBitmapFromFile(directory, "snake_head_d.png");
		head[3] = loadBitmapFromFile(directory, "snake_head_l.png");
		head[4] = loadBitmapFromFile(directory, "snake_head_r.png");

	}

	private Bitmap loadBitmapFromFile(String directory, String filename) {
		return BitmapFactory.decodeFile(directory + "/" + filename);
	}

	public void loadSounds() {
		// Sounds
		sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		biteSound = sounds.load(getContext(), R.raw.apple, 1);
		crashSound = sounds.load(getContext(), R.raw.crash, 1);
		appearSound = sounds.load(getContext(), R.raw.appear, 1);
	}

	public void loadSounds(String directory) {
		// Sounds
		sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		biteSound = sounds.load(directory + "/apple.wav", 1);
		crashSound = sounds.load(directory + "/crash.wav", 1);
		appearSound = sounds.load(directory + "/appear.wav", 1);
	}

	public void playSound(SoundID id) {
		switch (id) {
		case BITE:
			sounds.play(biteSound, 0.99f, 0.99f, 0, 0, 1.0f);
			break;
		case CRASH:
			sounds.play(crashSound, 0.99f, 0.99f, 0, 0, 1.5f);
			break;
		case APPEAR:
		default:
			sounds.play(appearSound, 0.99f, 0.99f, 0, 0, 1.0f);
			break;
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (initialized || w == 0)
			return;

		// these are all things that rely on the width and height
		// of the View, which is not available until the Activity
		// performs the layout.

		screenWidth = w;
		screenHeight = h;

		blocksHigh = h / blockSize;
		blocksWide = w / blockSize;

		computeBoundary();
		controller.getModel().setPlayfield(blocksWide, blocksHigh);
		controller.getModel().setObstacles(createObstacles());

		initialized = true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (bg != null) {
			canvas.drawBitmap(Bitmap.createScaledBitmap(bg, screenWidth, screenHeight, false), 0, 0, null);
		} else
			canvas.drawColor(Color.BLACK);
		drawAll(canvas);
	}

	private void computeBoundary() {
		int extraHeight = screenHeight % blockSize;
		int extraWidth = screenHeight % blockSize;

		ul.x = extraWidth / 2;
		ul.y = extraHeight / 2;
		lr.x = screenWidth - extraWidth / 2;
		lr.y = screenHeight - extraHeight / 2;
	}

	private ArrayList<GridPoint> createObstacles() {
		ArrayList<GridPoint> o = new ArrayList<GridPoint>();

		for (int i = 0; i < blocksWide; i++) {
			o.add(new GridPoint(i, 0));
			o.add(new GridPoint(i, blocksHigh - 1)); // was "- 1"
		}
		for (int j = 1; j < blocksHigh - 1; j++) { // was "- 1"
			o.add(new GridPoint(0, j));
			o.add(new GridPoint(blocksWide - 1, j));
		}

		return o;
	}

	private void drawAll(Canvas canvas) {

		// Draw Snake
		Snake s = controller.getModel().getSnake();

		// Draw Obstacles
		for (GridPoint p : controller.getModel().getObstacles())
			drawBlock(canvas, block, p);

		// head
		ArrayList<GridPoint> list = s.getList();
		drawBlock(canvas, head[s.going().asInt()], list.get(0));

		// body
		for (int i = 1; i < list.size(); i++)
			drawBlock(canvas, body, list.get(i));

		// Draw Apple
		if (controller.getModel().getApples().isEmpty()) {
			controller.makeApples(blocksWide, blocksHigh);
		}

		for (GridPoint a : controller.getModel().getApples())
			drawBlock(canvas, apple, a);
	}

	private void drawBlock(Canvas c, Bitmap b, GridPoint p) {
		float x = (p.getX() * blockSize);
		float y = (p.getY() * blockSize);

		c.drawBitmap(b, x, y, null);
	}
}