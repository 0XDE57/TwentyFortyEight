package com.twentyfortyeight;

import java.util.Random;

public class GameGrid {
	
	int[][] grid; // stores the numbers
	boolean[][] hasCombined; // stores whether a cell has been combined
	boolean[][] newCell; // marks whether a cell is new or not
	
	public GameGrid(int size) {
		
		initializeGame(size);
	}
	
	/** Initialize game. Place 2 instances of 2 anywhere on the board. */
	public void initializeGame(int gridSize) {
		// clear grid
		resetGrid(gridSize);
		resetMoved(gridSize);

		// generate two new cells at beginning of game
		generateNextCell();
		generateNextCell();
	}
	
	public int getSize() {
		return grid.length;
	}
	
	public boolean IsCellNew(int x, int y) {
		return newCell[x][y];
	}

	public int getCell(int x, int y) {
		return grid[x][y];
	}
	
	/** Reset grid. */
	private void resetGrid(int gridSize) {
		grid = new int[gridSize][gridSize];
	}

	/** Reset moved(consumable) flags. */
	public void resetMoved(int gridSize) {
		hasCombined = new boolean[gridSize][gridSize];
		newCell = new boolean[gridSize][gridSize];
	}

	/** Check if there are any free cells. */
	public boolean isGridFull() {
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid.length; y++) {
				if (grid[x][y] == 0)
					return false;
			}
		}
		return true;
	}

	/** Generates a new value and sets it in an empty cell. */
	public void generateNextCell() {
		Random rng = new Random();
		if (!isGridFull()) {
			int newX, newY;
			do {
				newX = rng.nextInt(grid.length);
				newY = rng.nextInt(grid.length);
			} while (grid[newX][newY] != 0);
			// make sure position is free, generate 2 or 4
			grid[newX][newY] = rng.nextBoolean() ? 2 : 4;
			//grid[newX][newY] = rng.nextBoolean() ? 3 : 6;
			newCell[newX][newY] = true;
		}
	}
	
	public boolean canMoveUp() {
		for (int gridX = 0; gridX < grid.length; gridX++) {
			for (int gridY = 1; gridY < grid.length; gridY++) {

				// skip empty cells
				if (grid[gridX][gridY] == 0) {
					continue;
				}

				if (grid[gridX][gridY - 1] == 0) {
		
					return true;

				} else if (grid[gridX][gridY - 1] == grid[gridX][gridY]
						&& !hasCombined[gridX][gridY - 1] && !hasCombined[gridX][gridY]) {

					return true;
				}
			}
		}
		return false;
	}
	
	public boolean canMoveDown() {
		for (int gridX = 0; gridX < grid.length; gridX++) {
			for (int gridY = grid.length - 2; gridY >= 0; gridY--) {
			
				// skip empty cells
				if (grid[gridX][gridY] == 0) {
					continue;
				}

				if (grid[gridX][gridY + 1] == 0) {
					return true;			
				} else if (grid[gridX][gridY + 1] == grid[gridX][gridY]
						&& !hasCombined[gridX][gridY + 1] && !hasCombined[gridX][gridY]) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean canMoveRight() {
		for (int gridY = 0; gridY < grid.length; gridY++) {
			for (int gridX = grid.length - 2; gridX >= 0; gridX--) {
		
				// skip empty cells
				if (grid[gridX][gridY] == 0) {
					continue;
				}

				if (grid[gridX + 1][gridY] == 0) {
					return true;

				} else if (grid[gridX + 1][gridY] == grid[gridX][gridY]
						&& !hasCombined[gridX + 1][gridY] && !hasCombined[gridX][gridY]) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean canMoveLeft() {
		for (int gridY = 0; gridY < grid.length; gridY++) {
			for (int gridX = 1; gridX < grid.length; gridX++) {
				
				// skip empty cells
				if (grid[gridX][gridY] == 0) {
					continue;
				}

				if (grid[gridX - 1][gridY] == 0) {
					return true;

				} else if (grid[gridX - 1][gridY] == grid[gridX][gridY]
						&& !hasCombined[gridX - 1][gridY] && !hasCombined[gridX][gridY]) {
					return true;
				}
			}
		}
		return false;
	}

	/** Move all cells right and combined equal cells. */
	public void moveRight() {

		// ------ MOVE GRID RIGHT --------
		// 0 | 0 | 0 | 0 <--start 1st row, work down
		// 0 | 0 | 0 | 0
		// 0 | 0 | 0 | 0
		// 0 | 0 | 0 | 0
		for (int gridY = 0; gridY < grid.length; gridY++) {

			// for each item in Y row, start at 2nd-last column, work left
			// 0 | 0 | 0 | 0
			// 0 | 0 | 0 | 0
			// 0 | 0 | 0 | 0
			// 0 | 0 | 0 | 0
			// ^
			// |
			// start at 2nd-last column, work left
			for (int gridX = grid.length - 2; gridX >= 0; gridX--) {
				// keep within array
				if (gridX < 0) {
					gridX = 0;
				}
				if (gridX > grid.length - 2) {
					gridX = grid.length - 2;
				}

				// skip empty cells
				if (grid[gridX][gridY] == 0) {
					continue;
				}

				if (grid[gridX + 1][gridY] == 0) {
					// if right cell is free, move right
					grid[gridX + 1][gridY] = grid[gridX][gridY];
					// move combined marker right
					hasCombined[gridX + 1][gridY] = hasCombined[gridX][gridY];

					// clear old cell
					grid[gridX][gridY] = 0;
					hasCombined[gridX][gridY] = false;

					// return to right
					gridX = grid.length;

				} else if (grid[gridX + 1][gridY] == grid[gridX][gridY]
						&& !hasCombined[gridX + 1][gridY] && !hasCombined[gridX][gridY]) {
					// if right cell == current cell, and both cells have not
					// been combined, then combine
					grid[gridX + 1][gridY] += grid[gridX][gridY];
					// mark as combined
					hasCombined[gridX + 1][gridY] = true;

					// clear old cell
					grid[gridX][gridY] = 0;
					hasCombined[gridX][gridY] = false;

					// return to right
					gridX = grid.length;
				}
			}
		}

	}

	/** Move all cells down and combined equal cells. */
	public void moveDown() {

		// ------ MOVE GRID DOWN --------
		// 0 | 0 | 0 | 0
		// 0 | 0 | 0 | 0
		// 0 | 0 | 0 | 0
		// 0 | 0 | 0 | 0
		// ^
		// |
		// start 1st column, work right
		for (int gridX = 0; gridX < grid.length; gridX++) {

			// for each item in X column, start at 2nd last row
			// 0 | 0 | 0 | 0
			// 0 | 0 | 0 | 0
			// 0 | 0 | 0 | 0 <--start this row, work up
			// 0 | 0 | 0 | 0
			for (int gridY = grid.length - 2; gridY >= 0; gridY--) {
				// keep within array
				if (gridY < 0) {
					gridY = 0;
				}
				if (gridY > grid.length - 2) {
					gridY = grid.length - 2;
				}

				// skip empty cells
				if (grid[gridX][gridY] == 0) {
					continue;
				}

				if (grid[gridX][gridY + 1] == 0) {
					// if below cell is free, move down
					grid[gridX][gridY + 1] = grid[gridX][gridY];
					// move combined marker down
					hasCombined[gridX][gridY + 1] = hasCombined[gridX][gridY];

					// clear old cell
					grid[gridX][gridY] = 0;
					hasCombined[gridX][gridY] = false;

					// return to bottom
					gridY = grid.length;

				} else if (grid[gridX][gridY + 1] == grid[gridX][gridY]
						&& !hasCombined[gridX][gridY + 1] && !hasCombined[gridX][gridY]) {
					// if below cell == current cell, and both cells have not
					// been combined, then combine
					grid[gridX][gridY + 1] += grid[gridX][gridY];
					// mark as combined
					hasCombined[gridX][gridY + 1] = true;

					// clear old cell
					grid[gridX][gridY] = 0;
					hasCombined[gridX][gridY] = false;

					// return to bottom
					gridY = grid.length;
				}
			}
		}
	}

	/** Move all cells up and combined equal cells. */
	public void moveUp() {

		// ------ MOVE GRID UP --------
		// 0 | 0 | 0 | 0
		// 0 | 0 | 0 | 0
		// 0 | 0 | 0 | 0
		// 0 | 0 | 0 | 0
		// ^
		// |
		// start this column, work right
		for (int gridX = 0; gridX < grid.length; gridX++) {

			// for each item in X column, start at 2nd row
			// 0 | 0 | 0 | 0
			// 0 | 0 | 0 | 0 <--start this row, work down
			// 0 | 0 | 0 | 0
			// 0 | 0 | 0 | 0
			for (int gridY = 1; gridY < grid.length; gridY++) {
				// keep within array
				if (gridY < 1) {
					gridY = 1;
				}
				if (gridY > grid.length) {
					gridY = grid.length;
				}

				// skip empty cells
				if (grid[gridX][gridY] == 0) {
					continue;
				}

				if (grid[gridX][gridY - 1] == 0) {
					// if above cell is free, move up
					grid[gridX][gridY - 1] = grid[gridX][gridY];
					// move combined marker up
					hasCombined[gridX][gridY - 1] = hasCombined[gridX][gridY];

					// clear old cell
					grid[gridX][gridY] = 0;
					hasCombined[gridX][gridY] = false;

					// return to top
					gridY = 0;

				} else if (grid[gridX][gridY - 1] == grid[gridX][gridY]
						&& !hasCombined[gridX][gridY - 1] && !hasCombined[gridX][gridY]) {
					// if above cell == current cell, and both cells have not
					// been combined, then combine
					grid[gridX][gridY - 1] += grid[gridX][gridY];
					// mark as combined
					hasCombined[gridX][gridY - 1] = true;

					// clear old cell
					grid[gridX][gridY] = 0;
					hasCombined[gridX][gridY] = false;

					// return to top
					gridY = 0;
				}
			}
		}
	}

	/** Move all cells left and combined equal cells. */
	public void moveLeft() {

		// ------ MOVE GRID LEFT --------
		// 0 | 0 | 0 | 0 <--start this row, work down
		// 0 | 0 | 0 | 0
		// 0 | 0 | 0 | 0
		// 0 | 0 | 0 | 0
		for (int gridY = 0; gridY < grid.length; gridY++) {

			// for each item in Y row, start at 2nd column
			// 0 | 0 | 0 | 0
			// 0 | 0 | 0 | 0
			// 0 | 0 | 0 | 0
			// 0 | 0 | 0 | 0
			// ^
			// |
			// start this column, work right
			for (int gridX = 1; gridX < grid.length; gridX++) {
				// keep within array
				if (gridX < 1) {
					gridX = 1;
				}
				if (gridX > grid.length) {
					gridX = grid.length;
				}

				// skip empty cells
				if (grid[gridX][gridY] == 0) {
					continue;
				}

				if (grid[gridX - 1][gridY] == 0) {
					// if left cell is free, move left
					grid[gridX - 1][gridY] = grid[gridX][gridY];
					// move combined marker left
					hasCombined[gridX - 1][gridY] = hasCombined[gridX][gridY];

					// clear old cell
					grid[gridX][gridY] = 0;
					hasCombined[gridX][gridY] = false;

					// return to left
					gridX = 0;

				} else if (grid[gridX - 1][gridY] == grid[gridX][gridY]
						&& !hasCombined[gridX - 1][gridY] && !hasCombined[gridX][gridY]) {
					// if above cell == current cell, and both cells have not
					// been combined, then combine
					grid[gridX - 1][gridY] += grid[gridX][gridY];
					// mark as combined
					hasCombined[gridX - 1][gridY] = true;

					// clear old cell
					grid[gridX][gridY] = 0;
					hasCombined[gridX][gridY] = false;

					// return to left
					gridX = 0;
				}
			}
		}
	}

}
