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

public class Snake {
	private ArrayList<GridPoint> snakeList = null;
	private SnakeDirection snakeDir = SnakeDirection.UP;
	private int length = 3;

	public Snake(int w, int h) {
		snakeList = new ArrayList<GridPoint>();
		snakeList.add(new GridPoint(w / 2, h / 2));
	}

	public ArrayList<GridPoint> getList() {
		return snakeList;
	}

	public boolean contains(GridPoint p) {
		return snakeList.subList(1, snakeList.size()).contains(p);
	}

	public void go(SnakeDirection newDir) {
		snakeDir = newDir;
	}

	public SnakeDirection going() {
		return snakeDir;
	}

	public GridPoint getHead() {
		return snakeList.get(0);
	}

	public void moveHead(SnakeDirection newDir) {
		int x = snakeList.get(0).getX();
		int y = snakeList.get(0).getY();

		switch (newDir) {
		case UP:
			y--;
			break;
		case DOWN:
			y++;
			break;
		case RIGHT:
			x++;
			break;
		case LEFT:
			x--;
			break;
		}

		snakeList.add(0, new GridPoint(x, y));
	}

	public void changeLength(int delta) {
		if (length + delta > 2)
			length += delta;
	}

	public int getLength() {
		return length;
	}

	public void trim() {
		if (snakeList.size() > length) {
			for (int i = (snakeList.size() - 1); i >= length; i--)
				snakeList.remove(i);
		}
	}

}
