import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Task_99 {
	public static void main(String[] args) {
		Labyrinth test = new Labyrinth();
		try(Formatter output = new Formatter("OUTPUT.TXT")) {
			output.format(test.toString());
		}catch (FileNotFoundException | FormatterClosedException e) {
			e.printStackTrace();
		}
	}

	//-----------------------------------------------------------------------------
	/*public*/static class Labyrinth {
		//-----------------------------------------------------------------------------fields
		private ArrayList <LabyrinthsArea> levelsOfLabyrinth;//array from stand-alone levels
		private int levels;
		private int rows;
		private int columns;
		static int princeRaw;
		static int princeColumn;
		static int princessRaw;
		static int princessColumn;
		//-----------------------------------------------------------------------------constructors
		/*public*/ private Labyrinth(final String path)
		{
			this.levelsOfLabyrinth = new ArrayList<>();
			try(final Scanner input = new Scanner(Paths.get(path))) {
				//-----------------------------------------------------------------------------
				if(input.hasNext()) {
					//-----------------------------------------------------------------------------
					String parametersOfLabyrinth = input.nextLine();//read data from file
					//-----------------------------------------------------------------------------
					if(this.isCorrectParametersOfLabyrinth(parametersOfLabyrinth)) {
						String[] tokensParametersOfLabyrinth = parametersOfLabyrinth.split(" ");
						int numberOfParameter = 0;
						this.levels = Integer.valueOf(tokensParametersOfLabyrinth[numberOfParameter++]);
						this.rows = Integer.valueOf(tokensParametersOfLabyrinth[numberOfParameter++]);
						this.columns = Integer.valueOf(tokensParametersOfLabyrinth[numberOfParameter]);
					}
					//-----------------------------------------------------------------------------
					else {
						throw new IOException("Incorrect value in file!");
					}
					//-----------------------------------------------------------------------------
				}
				//-----------------------------------------------------------------------------
				else {
					throw new IOException("File is empty!");
				}
				//-----------------------------------------------------------------------------
				//create all levels of labyrinth
				for(int currentLevel = 0; currentLevel < this.levels; currentLevel++) {
					this.levelsOfLabyrinth.add(new LabyrinthsArea(input, this.rows, this.columns));
					if(input.hasNext())	input.nextLine(); //delete interval between last string previous area and first string next area
				}
				//-----------------------------------------------------------------------------
			} catch (IOException | NoSuchElementException e) {
				e.printStackTrace();
			}
		}

		/*public*/ Labyrinth()
		{
			this("INPUT.TXT");
		}
		//-----------------------------------------------------------------------------methods for constructors
		private boolean isCorrectParametersOfLabyrinth(final String s)
		{
			if(s.matches("[1-9]\\d? [1-9]\\d? [1-9]\\d?")) {
				for(String token : s.split(" ")) {
					if((Integer.valueOf(token) < 2) || (Integer.valueOf(token) > 50)) {
						return false;
					}
				}
			}
			return true;
		}
		//-----------------------------------------------------------------------------
		//-----------------------------------------------------------------------------methods
		/*public*/private void setDownPoints()
		//find point where we can go to lower level
		{
			for(int currentLevel = 0; currentLevel < this.levels - 1; currentLevel++) {
				LabyrinthsArea upperArea = this.levelsOfLabyrinth.get(currentLevel);
				LabyrinthsArea lowerArea = this.levelsOfLabyrinth.get(currentLevel + 1);
				//-----------------------------------------------------------------------------
				for (int row = 0; row < this.rows; row++) {
					for(int column = 0; column < this.columns; column++) {
						if(FieldsInLabyrinth.COLUMN != upperArea.getElementOfArea(row, column) &&
								FieldsInLabyrinth.COLUMN != lowerArea.getElementOfArea(row, column)) {
							upperArea.changeElementOfArea(row, column);
						}
					}
				}
				//-----------------------------------------------------------------------------
			}
		}

		/*public*/private int way()
		{
			this.setDownPoints();
			int[][][] allLabyrinth = new int[this.levels][this.rows][this.columns];
			for(int i = 0; i < this.levels; i++) {
				for(int[] k : allLabyrinth[i]) {
					Arrays.fill(k, -2);
				}
			}
			//-----------------------------------------------------------------------------
			this.fillPaces(this.levelsOfLabyrinth, allLabyrinth, Labyrinth.princeRaw, Labyrinth.princeColumn,
					(this.rows - 1), (this.columns - 1), 0, 0);
			//-----------------------------------------------------------------------------
			return allLabyrinth[this.levels - 1][Labyrinth.princessRaw][Labyrinth.princessColumn] * 5;
		}

		private void fillPaces(final ArrayList <LabyrinthsArea> area, final int[][][] array, final int currentRow, final int currentColumn,
							   final int maxRow, final int maxColumn, final int currentPace, final int currentLevel)
		{
			//recursive method of bypass of one area of labyrinth
			//area - current area
			//array - representation of area as array, where element mean count of steps
			//prince need to go to land this element
			//currentRow & currentColumn mean that's mean
			//maxRow & maxColumn - scope of array
			//currentPace - amount of paces from prince to this point

			boolean canGoSouth = false;
			boolean canGoNorth = false;
			boolean canGoWest = false;
			boolean canGoEast = false;
			boolean canGoDown = false;


			if((currentLevel != (this.levels - 1)) && (area.get(currentLevel + 1).getElementOfArea(currentRow, currentColumn) != FieldsInLabyrinth.COLUMN)) {
				if((array[currentLevel + 1][currentRow][currentColumn] > currentPace + 1) || (array[currentLevel + 1][currentRow][currentColumn] == -2)) {
					canGoDown = true;
					array[currentLevel + 1][currentRow][currentColumn] = currentPace + 1;
				}

			}
			if(canGoDown) {
				this.fillPaces(area, array, currentRow, currentColumn, maxRow, maxColumn, (currentPace + 1), currentLevel + 1);
			}
			//-----------------------------------------------------------------------------
			if((currentRow != 0) && (area.get(currentLevel).getElementOfArea(currentRow - 1, currentColumn) != FieldsInLabyrinth.COLUMN)) {
				if((array[currentLevel][currentRow - 1][currentColumn] > currentPace + 1) || (array[currentLevel][currentRow - 1][currentColumn] == -2)) {
					canGoNorth = true;
					array[currentLevel][currentRow - 1][currentColumn] = currentPace + 1;
				}

			}
			if(canGoNorth) {
				this.fillPaces(area, array, currentRow-1, currentColumn, maxRow, maxColumn, (currentPace + 1), currentLevel);
			}
			//-----------------------------------------------------------------------------
			if((currentRow != maxRow) && (area.get(currentLevel).getElementOfArea(currentRow + 1, currentColumn) != FieldsInLabyrinth.COLUMN)) {
				if(array[currentLevel][currentRow + 1][currentColumn] > currentPace + 1 || (array[currentLevel][currentRow + 1][currentColumn] == -2)) {
					canGoSouth = true;
					array[currentLevel][currentRow + 1][currentColumn] = currentPace + 1;
				}
			}
			if(canGoSouth) {
				this.fillPaces(area, array, currentRow+1, currentColumn, maxRow, maxColumn, (currentPace + 1), currentLevel);
			}
			//-----------------------------------------------------------------------------
			if((currentColumn != 0) && (area.get(currentLevel).getElementOfArea(currentRow, currentColumn - 1) != FieldsInLabyrinth.COLUMN)) {
				if(array[currentLevel][currentRow][currentColumn - 1] > currentPace + 1 || (array[currentLevel][currentRow][currentColumn - 1] == -2)) {
					canGoWest = true;
					array[currentLevel][currentRow][currentColumn - 1] = currentPace + 1;
				}
			}
			if(canGoWest) {
				this.fillPaces(area, array, currentRow, currentColumn-1, maxRow, maxColumn, (currentPace + 1), currentLevel);
			}
			//-----------------------------------------------------------------------------
			if((currentColumn != maxColumn) && (area.get(currentLevel).getElementOfArea(currentRow, currentColumn + 1) != FieldsInLabyrinth.COLUMN)) {
				if(array[currentLevel][currentRow][currentColumn + 1] > currentPace + 1 || (array[currentLevel][currentRow][currentColumn + 1] == -2)) {
					canGoEast = true;
					array[currentLevel][currentRow][currentColumn + 1] = currentPace + 1;
				}
			}
			if(canGoEast) {
				this.fillPaces(area, array, currentRow, currentColumn+1, maxRow, maxColumn, (currentPace + 1), currentLevel);
			}
			//-----------------------------------------------------------------------------
		}

		@Override
		public String toString()
		{
			return String.valueOf(this.way());
		}
	}
	//-----------------------------------------------------------------------------
	/*public*/static class LabyrinthsArea {
		//-----------------------------------------------------------------------------fields
		private FieldsInLabyrinth[][] levelInLabyrinth;
		//-----------------------------------------------------------------------------constructors
		/*public*/ LabyrinthsArea(final Scanner input, final int rows, final int columns)
		{
			String[] tokens = new String[rows];//gets area symbols
			this.levelInLabyrinth = new FieldsInLabyrinth[rows][columns];
			try {
				for (int row = 0; row < rows; row++) {
					if (input.hasNext()) {
						tokens[row] = input.nextLine();
					}
					else {
						throw new IOException("Insufficient data in file");
					}
					if (!(this.isCorrectContentOfLabyrinth(tokens[row], columns))) {
						throw new IOException("Incorrect value in file!");
					}
				}
			} catch (IOException | NoSuchElementException e) {
				e.printStackTrace();
			}
			//-----------------------------------------------------------------------------
			this.fillLevelInLabyrinth(tokens, rows, columns);
			//-----------------------------------------------------------------------------
		}
		//-----------------------------------------------------------------------------methods for constructors
		private boolean isCorrectContentOfLabyrinth(final String s, final int columns)
		{
			return ((s.matches("[12o.\n]+")) && (columns == s.length()));
		}

		private void fillLevelInLabyrinth(final String[] tokens, final int rows, final int columns)
		{
			for(int row = 0; row < rows; row++) {
				for(int column = 0; column < columns; column++) {
					switch (tokens[row].charAt(column)) {
						case '.':
							this.levelInLabyrinth[row][column] = FieldsInLabyrinth.EMPTY;
							break;
						case 'o':
							this.levelInLabyrinth[row][column] = FieldsInLabyrinth.COLUMN;
							break;
						case '1':
							this.levelInLabyrinth[row][column] = FieldsInLabyrinth.PRINCE;
							Labyrinth.princeRaw = row;
							Labyrinth.princeColumn = column;
							break;
						case '2':
							this.levelInLabyrinth[row][column] = FieldsInLabyrinth.PRINCESS;
							Labyrinth.princessRaw = row;
							Labyrinth.princessColumn = column;
							break;
					}
				}
			}
		}
		//-----------------------------------------------------------------------------
		//-----------------------------------------------------------------------------methods

		/*public*/ FieldsInLabyrinth getElementOfArea(final int raw, final int column)
		{
			return this.levelInLabyrinth[raw][column];
		}

		/*public*/ void changeElementOfArea(final int raw, final int column)
		{
			this.levelInLabyrinth[raw][column] = FieldsInLabyrinth.DOWNPOINT;
		}
	}
	//-----------------------------------------------------------------------------
	/*public*/enum FieldsInLabyrinth {
		EMPTY, COLUMN, PRINCE, PRINCESS, DOWNPOINT
	}
}