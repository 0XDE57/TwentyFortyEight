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

	// 2048 cells and logic
	GameGrid grid; 
	
	// font/text
	FreeTypeFontGenerator generator;
	FreeTypeFontParameter parameter;
	BitmapFont fontPressStart;
	int fontSize; //size to render font
	
	//animation/message
	boolean lose = false;

	@Override
	public void create() {
		int gridSize = 4; // how many cells
		grid = new GameGrid(gridSize);
		
		// grid layout
		fontSize = 60;

		// graphics
		shape = new ShapeRenderer();
		batch = new SpriteBatch();

		// font
		generator = new FreeTypeFontGenerator(Gdx.files.internal("PressStart2P.ttf"));
		parameter = new FreeTypeFontParameter();
		parameter.size = fontSize;
		fontPressStart = generator.generateFont(parameter);
		generator.dispose();

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
		
		//calculate spacing in between cells
		int gridSpacing;
		if (Gdx.graphics.getWidth() > Gdx.graphics.getHeight()) {
			gridSpacing = Gdx.graphics.getHeight() / (grid.getSize()  + 1);
		} else {
			gridSpacing = Gdx.graphics.getWidth() / (grid.getSize() + 1);
		}

		shape.begin(ShapeType.Filled);
		//draw boxes to represent grid spaces/positions
		for (int x = 0; x < grid.getSize(); ++x) {
			for (int y = 0; y < grid.getSize(); ++y) {
				//calculate position
				int posX = (x + 1) * gridSpacing;
				int posY = (y + 1) * gridSpacing;
				int boxSize = (int) (fontSize * 2);
				
				shape.setColor(0.7f, 0.7f, 0.7f, 1);
				shape.rect(posX-boxSize/2, posY-boxSize/2, boxSize, boxSize);
				
				//center marker for debug
				//shape.setColor(1, 0, 0, 1); //red
				//shape.line(posX - boxSize/4, posY, posX + boxSize/4, posY);
				//shape.line(posX, posY - boxSize/4, posX, posY + boxSize/4);
			}
		}
		shape.end();

				
		batch.begin();		
		// render cell values
		for (int x = 0; x < grid.getSize(); ++x) {
			for (int y = 0; y < grid.getSize(); ++y) {
				if (grid.getCell(x, y) == 0) {
					continue; //don't render empty cells
				}
				
				//separate colors for new cells and regular cells for visual aid until animation is implemented
				if (grid.IsCellNew(x, y)) {
					fontPressStart.setColor(0.3f, 0.3f, 0.6f, 1);
				} else {
					fontPressStart.setColor(0.5f, 0.3f, 0.5f, 1);
				}
				
				//calculate position and draw value
				int posX = (x + 1) * gridSpacing;
				int posY = (y + 1) * gridSpacing;
				posX -= (fontSize * String.valueOf(grid.getCell(x, y)).length()) / 2;
				posY += fontSize / 2;
				fontPressStart.draw(batch, Integer.toString(grid.getCell(x, y)), posX, posY);
			}
			
			if (lose) {
				fontPressStart.setColor(1, 0.2f, 0.2f, 1);
				fontPressStart.draw(batch, "Game Over", Gdx.graphics.getWidth()/8, Gdx.graphics.getHeight()/2);
				fontPressStart.draw(batch, "Press 'R'", Gdx.graphics.getWidth()/8, Gdx.graphics.getHeight()/2 - fontSize);
			}
			
		}
		
		batch.end();

		// controls
		handleInput();

	}


	private void handleInput() {
		boolean hasMoved = false;
		
		// move up
		if (Gdx.input.isKeyJustPressed(Keys.W) || Gdx.input.isKeyJustPressed(Keys.UP)) {
			if (grid.canMoveDown()) {
				grid.moveDown();
				hasMoved = true;				
			}
			
		}

		// move down
		if (Gdx.input.isKeyJustPressed(Keys.S) || Gdx.input.isKeyJustPressed(Keys.DOWN)) {
			if (grid.canMoveUp()) {
				grid.moveUp();
				hasMoved = true;
			}		
		}

		// move left
		if (Gdx.input.isKeyJustPressed(Keys.A) || Gdx.input.isKeyJustPressed(Keys.LEFT)) {
			if (grid.canMoveLeft()) {
				grid.moveLeft();
				hasMoved = true;
			}
			
		}

		// move right
		if (Gdx.input.isKeyJustPressed(Keys.D) || Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
			if (grid.canMoveRight()) {
				grid.moveRight();
				hasMoved = true;
			}
		}
		
		if (hasMoved) {
			grid.resetMoved(grid.getSize());
			grid.generateNextCell();
			hasMoved = false;
			if (grid.isGridFull()) {
				if (!grid.canMoveDown() && !grid.canMoveUp() && !grid.canMoveLeft() && !grid.canMoveRight()) {
					lose = true;
				}			
			}
		}

		// restart game
		if (Gdx.input.isKeyJustPressed(Keys.R)) {
			grid.initializeGame(grid.getSize());
			lose = false;
		}

		// terminate
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}
}
