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

public class GameLevel {

	private GameLevel(int level) {
		this.setLevel(level);
		this.setNumberOfApples(fibonacci(level));
		this.setNumberOfObstacles(0);
		this.setGrowLength(1 + level);
		this.resetScore();		
	}
	
	private int fibonacci(int n) {
		return n <=1 ? n : fibonacci(n-1) + fibonacci(n-2);
	}
	public GameLevel(int level, int startingSpeed) {
		this(level);
		this.setSpeed(startingSpeed);
		//this.setSpeed(startingSpeed - 25);
	}

	public GameLevel nextGameLevel() {
		GameLevel gameLevel = new GameLevel(level+1, speed);
		return gameLevel;
	}

	private int level;
	private int numberOfApples;
	private int speed;
	private int numberOfObstacles;
	private int growLength;
	private int gameScore;


	public int getLevelNumber() {
		return level;
	}
	public int getNumberOfApples() {
		return numberOfApples;
	}

	public int getSpeed() {
		return speed;
	}

	public int getNumberOfObstacles() {
		return numberOfObstacles;
	}

	public int getGrowLength() {
		return growLength;
	}
	
	private void setLevel(int level) {
		this.level = level;
	}
	private void setNumberOfApples(int numberOfApples) {
		this.numberOfApples = numberOfApples;
	}

	private void setSpeed(int speed) {
		this.speed = speed <= 0 ? 25 : speed;
	}

	public void incrementSpeed(int speed, int delta) {
		this.speed = speed - delta;
	}

	private void setNumberOfObstacles(int numberOfObstacles) {
		this.numberOfObstacles = numberOfObstacles;
	}
	private void setGrowLength(int growLength) {
		this.growLength = growLength;
	}

	public int getScore() {
		return gameScore;
	}
	public void incrementScore() {
		this.gameScore++;		
	}
	public void resetScore() {
		this.gameScore = 0;		
	}
	public String toString() {
		return level + "";
	}
}
