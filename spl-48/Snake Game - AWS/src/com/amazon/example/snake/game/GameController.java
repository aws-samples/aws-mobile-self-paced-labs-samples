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

import android.os.Handler;
import android.os.Message;
import android.util.Log;

// use Enum as preferred Singleton Implementation
public enum GameController {
	CONTROLLER();

	// create model
	private GameModel gameModel = null;
	private GameView gameView = null;
	private GameStateListener gameStateListener = null;
	private boolean metronomeStarted = false;

	private boolean isGameRunning = false;
	private static final Object TIMERLOCK = new Object();
	private static final Object FLAGLOCK = new Object();

	// UI Thread Message Handler
	private final Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			endTick();
		}
	};

	private GameController() {
		gameModel = new GameModel();
	}

	public void setStartingGameLevel(int startingLevel, int startingSpeed) {
		gameModel.setStartingGameLevelNumber(startingLevel, startingSpeed);
	}

	public void start() {
		gameModel.resetModel();
		createMetronome();
	}

	public GameStateListener getGameStateListener() {
		return gameStateListener;
	}

	public void setGameStateListener(GameStateListener gameStateListener) {
		this.gameStateListener = gameStateListener;
	}

	public GameModel getModel() {
		return gameModel;
	}

	// updating model with last move
	public void updateMove(SnakeDirection move) {
		if (gameModel.moveRule(move))
			gameModel.getSnake().go(move);
	}

	public void setView(GameView s) {
		gameView = s;
	}

	// Timer Control
	private void createMetronome() {

		if (!metronomeStarted) {
			metronomeStarted = true;

			Runnable metronome = new Runnable() {
				boolean running = false;

				@Override
				public void run() {
					while (true) {

						synchronized (FLAGLOCK) {
							running = isGameRunning;
						}

						if (running) {
							try {
								Thread.sleep(gameModel.getCurrentLevel()
										.getSpeed());
							} catch (InterruptedException e) {
								Log.v(this.getClass().getSimpleName(),
										"sleep thread interrupted");
								break;
							}

							try {
								synchronized (TIMERLOCK) {
									uiHandler.sendEmptyMessage(0);
									TIMERLOCK.wait();
								}

							} catch (InterruptedException e) {
								Log.v(this.getClass().getSimpleName(),
										"waiting thread interrupted");
								break;
							}

						}
					}

					endGame();
				}
			};

			Thread t = new Thread(metronome);
			t.start();

		}
	}

	public void endTick() {
		Snake s = gameModel.getSnake();

		// Move Snake forward, based on last input
		s.moveHead(s.going());

		// detect collision
		if (isHittingWall(s.getHead())) {
			gameView.playSound(SoundID.CRASH);
			endGame();
		}

		// detect eat
		if (canHazCheezburger(s)) {
			gameView.playSound(SoundID.BITE);
			removeApple(s.getHead());
			gameModel.getCurrentLevel().incrementScore();

			if (gameModel.getApples().isEmpty()) {
				this.gameStateListener.onLevelFinished(gameModel
						.getCurrentLevel());

				// If they want to play next level then
				this.gameStateListener.onLevelStarted(gameModel.nextLevel());

				// trim
				gameModel.getSnake().trim();

				// ready to render
				gameView.invalidate();

				synchronized (TIMERLOCK) {
					TIMERLOCK.notifyAll();
				}
			} else {
				s.changeLength(gameModel.getCurrentLevel().getGrowLength());
				// changeSpeed(-15);
				// trim
				gameModel.getSnake().trim();

				// ready to render
				gameView.invalidate();

				synchronized (TIMERLOCK) {
					TIMERLOCK.notifyAll();
				}
			}

		} else {
			// trim
			gameModel.getSnake().trim();

			// ready to render
			gameView.invalidate();

			synchronized (TIMERLOCK) {
				TIMERLOCK.notifyAll();
			}
		}

	}

	public void startGame() {
		gameModel.resetModel();
		gameStateListener.onGameStarted(gameModel.getCurrentLevel());
		synchronized (FLAGLOCK) {
			isGameRunning = true;
		}
	}

	public void endGame() {
		gameStateListener.onGameFinished(gameModel.getCurrentLevel());
		synchronized (FLAGLOCK) {
			isGameRunning = false;
		}

		Log.v(this.getClass().getSimpleName(), "Game Over");
	}

	// collision detection
	private boolean isHittingWall(GridPoint p) {
		return (gameModel.getObstacles().contains(p) || gameModel.getSnake()
				.contains(p));
	}

	private boolean canHazCheezburger(Snake s) {
		return gameModel.getApples().contains(s.getHead());
	}

	private boolean removeApple(GridPoint a) {
		return gameModel.getApples().remove(a);
	}

	public void makeApples(int blockWidth, int blockHeight) {
		GridPoint apple = null;

		while (null != gameModel.getCurrentLevel()
				&& gameModel.getApples().size() < gameModel.getCurrentLevel()
						.getNumberOfApples()) {
			apple = new GridPoint(randInt(1, blockWidth - 2), randInt(1,
					blockHeight - 2));

			if (!isHittingWall(apple))
				gameModel.getApples().add(apple);
		}
	}

	private int randInt(int low, int high) {
		return (low + (int) (Math.random() * ((high - low) + 1)));
	}

}
