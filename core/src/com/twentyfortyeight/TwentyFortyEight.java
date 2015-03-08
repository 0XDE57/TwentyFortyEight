package com.twentyfortyeight;

import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;

public class TwentyFortyEight extends ApplicationAdapter {

	// rendering/drawing
	ShapeRenderer shape;
	SpriteBatch batch;

	// font/text
	FreeTypeFontGenerator generator;
	FreeTypeFontParameter parameter;
	BitmapFont fontPressStart;

	Random rng;
	int[][] grid; // stores the numbers
	boolean[][] hasCombined; // stores whether a cell has been combined
	boolean[][] newCell;

	// grid spacing
	int gridSize;
	int gridPosX;
	int gridPosY;
	int gridSpacing;

	int fontSize;

	@Override
	public void create() {

		// grid layout
		fontSize = 80;
		gridSize = 6; // how many cells
		gridSpacing = fontSize * 2; // space between cells

		// TODO adjust spacing, center
		gridPosX = (Gdx.graphics.getWidth() / 2) - ((gridSize * gridSpacing) / 2) + fontSize;
		gridPosY = (Gdx.graphics.getHeight() /2) + ((gridSize * gridSpacing) / 2) - fontSize;

		// graphics
		shape = new ShapeRenderer();
		batch = new SpriteBatch();

		// font
		generator = new FreeTypeFontGenerator(Gdx.files.internal("PressStart2P.ttf"));
		parameter = new FreeTypeFontParameter();
		parameter.size = fontSize;
		fontPressStart = generator.generateFont(parameter);
		generator.dispose();
		rng = new Random();

		// initialize grid
		initializeGame(gridSize);

	}

	@Override
	public void render() {
		// clear screen with white
		Gdx.gl.glClearColor(0.75f, 0.75f, 0.75f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		
		//adjust projection matrix to fix rendering on resize TODO: only needs to be called on resize
		Matrix4 matrix = new Matrix4();
		matrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shape.setProjectionMatrix(matrix);
		batch.setProjectionMatrix(matrix);
		
		
		int gridSpacingX = Gdx.graphics.getWidth() / (grid.length + 1);
		int gridSpacingY = Gdx.graphics.getHeight() / (grid.length + 1);
		

		shape.begin(ShapeType.Line);
		/*
		shape.setColor(0.3f, 0.3f, 0.3f, 1);
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid.length; y++) {
				int xPos = ((gridPosX + (x * gridSpacing)) - String.valueOf(grid[x][y]).length() * 20); // center
				int yPos = gridPosY - (y * gridSpacing);
				int padding = 5;
				shape.circle(xPos, yPos, 5);
				shape.box(xPos-padding, yPos-fontSize+padding, 0, fontSize+padding, fontSize+padding, 0);
			}
		}*/
		
		
		
		//System.out.println(grid.length + ", " + gridSpacingX + ": " + Gdx.graphics.getWidth());
		
		for (int x = 0; x < grid.length; ++x) {
			for (int y = 0; y < grid.length; ++y) {
				
				int posX = (x + 1) * gridSpacingX;
				int posY = (y + 1) * gridSpacingY;
				int boxSize = 64;
				
				shape.setColor(1, 0, 0, 1); //red
				//horizontal center marker
				shape.line(posX - boxSize, posY, posX + boxSize, posY);
				//vertical center market
				shape.line(posX, posY - boxSize, posX, posY + boxSize);
				
				shape.setColor(1, 1, 1, 1); //white
				//shape.circle(posX, posY, boxSize);//temp
				shape.rect(posX-boxSize/2, posY-boxSize/2, boxSize, boxSize);
			}
			
		}
		
		
		shape.end();

		
		
		batch.begin();
		
		// render cell values
		for (int x = 0; x < grid.length; ++x) {
			for (int y = 0; y < grid.length; ++y) {

				//separate colors for new cells, empty cells and regular cells
				if (newCell[x][y]) {
					fontPressStart.setColor(0.3f, 0.3f, 0.6f, 1);
				} else if (grid[x][y] == 0) {
					fontPressStart.setColor(0.3f, 0.3f, 0.3f, 1);
				} else {
					fontPressStart.setColor(0.2f, 0.7f, 0.7f, 1);
				}
				
				int posX = (x + 1) * gridSpacingX;
				int posY = (y + 1) * gridSpacingY;
				posX -= (fontSize * String.valueOf(grid[x][y]).length()) / 2;
				posY += fontSize / 2;
				fontPressStart.draw(batch, Integer.toString(grid[x][y]), posX, posY);
			}
			
		}
		/*
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid.length; y++) {
				int xPos = ((gridPosX + (x * gridSpacing)) - String.valueOf(grid[x][y]).length() * 20); // center
				int yPos = gridPosY - (y * gridSpacing);
				
				//separate colors for new cells, empty cells and regular cells
				if (newCell[x][y]) {
					fontPressStart.setColor(0.3f, 0.3f, 0.6f, 1);
				} else if (grid[x][y] == 0) {
					fontPressStart.setColor(0.3f, 0.3f, 0.3f, 1);
				} else {
					fontPressStart.setColor(0.2f, 0.7f, 0.7f, 1);
				}
				
				fontPressStart.draw(batch, Integer.toString(grid[x][y]), xPos, yPos);
			}

		}*/
		batch.end();

		// TODO
		// check if can move before moving
		// check if no moves can be made = game over

		// controls
		handleInput();

	}


	private void handleInput() {
		// move up
		if (Gdx.input.isKeyJustPressed(Keys.W) || Gdx.input.isKeyJustPressed(Keys.UP)) {
			moveDown();
			resetMoved(gridSize);
			generateNextCell();
		}

		// move down
		if (Gdx.input.isKeyJustPressed(Keys.S) || Gdx.input.isKeyJustPressed(Keys.DOWN)) {
			moveUp();
			resetMoved(gridSize);
			generateNextCell();
		}

		// move left
		if (Gdx.input.isKeyJustPressed(Keys.A) || Gdx.input.isKeyJustPressed(Keys.LEFT)) {
			moveLeft();
			resetMoved(gridSize);
			generateNextCell();
		}

		// move right
		if (Gdx.input.isKeyJustPressed(Keys.D) || Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
			moveRight();
			resetMoved(gridSize);
			generateNextCell();
		}

		// restart game
		if (Gdx.input.isKeyJustPressed(Keys.R)) {
			initializeGame(gridSize);
		}

		// terminate
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}

	/**
	 * Initialize game. Place 2 instances of 2 anywhere on the board.
	 * 
	 * @param gridSize
	 *            size of game
	 */
	private void initializeGame(int gridSize) {
		// clear grid
		resetGrid(gridSize);
		resetMoved(gridSize);

		// generate two new cells at beginning of game
		generateNextCell();
		generateNextCell();
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

	/**
	 * Check if there are any free cells.
	 * 
	 * @return true is grid is full, false otherwise.
	 */
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
		if (!isGridFull()) {
			int newX, newY;
			do {
				newX = rng.nextInt(grid.length);
				newY = rng.nextInt(grid.length);
			} while (grid[newX][newY] != 0);
			// make sure position is free, generate 2 or 4
			grid[newX][newY] = rng.nextBoolean() ? 2 : 4;
			newCell[newX][newY] = true;
		}
	}

	/** Move all cells right and combined equal cells. */
	private void moveRight() {

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
	private void moveDown() {

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
	private void moveUp() {

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
	private void moveLeft() {

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
