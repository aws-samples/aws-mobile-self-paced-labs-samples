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

import java.util.ArrayList;

 
public class GameModel {	
	// state
	private final ArrayList<GridPoint> appleList = new ArrayList<GridPoint>();
	private ArrayList<GridPoint> obstacleList = new ArrayList<GridPoint>();
	
	private GameLevel currentGameLevel = null;
	private Snake snake = null;
	private int playfieldWidth = 0;
	private int playfieldHeight = 0;
	
	
	public GameModel() {
		resetModel();		
	}

	public void setStartingGameLevelNumber(int startingGameLevelNumber, int startingSpeed) {
	//	if (null == this.currentGameLevel)
			this.currentGameLevel = new GameLevel(startingGameLevelNumber, startingSpeed);
	}

	public GameLevel getCurrentLevel() {
		return currentGameLevel;
	}

	public void resetModel() {
		
		this.snake = new Snake(playfieldWidth, playfieldHeight);
		this.appleList.clear();
		if (null != this.currentGameLevel) {
			this.currentGameLevel.resetScore();
		}
	}

	public boolean moveRule(SnakeDirection newDir) {
		return (Math.abs(newDir.asInt() - snake.going().asInt()) != 1);
	}

	public void setPlayfield(int widthInBlocks, int heightInBlocks) {
		playfieldWidth = widthInBlocks;
		playfieldHeight = heightInBlocks;

		// this should only be called when the screen view
		// resizes, which for this game, happens at start.
		resetModel();
	}

	public ArrayList<GridPoint> getApples() {
		return appleList;
	}

	public ArrayList<GridPoint> getObstacles() {
		return obstacleList;
	}

	public void setObstacles(final ArrayList<GridPoint> obstacles) {
		this.obstacleList = obstacles;
	}

	public Snake getSnake() {
		return snake;
	}

	public void setSnake(final Snake snake) {
		this.snake = snake;
	}

	public GameLevel nextLevel() {
		currentGameLevel = currentGameLevel.nextGameLevel();
		return currentGameLevel;
	}
}
