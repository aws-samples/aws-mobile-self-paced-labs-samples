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

public class GridPoint {
	private int x = 0;
	private int y = 0;

	public GridPoint(final int col, final int row) {
		this.x = col;
		this.y = row;
		
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object gp) {
		return (this.hashCode() == ((GridPoint) gp).hashCode());
	}

	@Override
	public int hashCode() {
		// will work for reasonable grid sizes
		return ((this.x * 10000) + this.y);
	}
}
