import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Task_99 {
	public static void main(String[] args) {
		try(Formatter output = new Formatter("OUTPUT.txt")) {
			Labyrinth test = new Labyrinth();
			test.setDownPoints();
			output.format("%d", test.way());
		}catch (FileNotFoundException | FormatterClosedException e) {
			e.printStackTrace();
		}
	}
}


//-----------------------------------------------------------------------------
/*public*/class Labyrinth {
	//-----------------------------------------------------------------------------fields
	private int levels = 0;
	private int rows = 0;
	private int columns = 0;
	private ArrayList <LabyrinthsArea> levelsOfLabyrinth;//array from stand-alone levels
	static int princeRaw;
	static int princeColumn;
	//-----------------------------------------------------------------------------constructors
	/*public*/ private Labyrinth(String path)
	{
		try(Scanner input = new Scanner(Paths.get(path))) {
			//-----------------------------------------------------------------------------
			if(input.hasNext()) {
				//-----------------------------------------------------------------------------
				String parametersOfLabyrinth = input.nextLine().strip();
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
			//-----------------------------------------------------------------------------
			this.levelsOfLabyrinth = new ArrayList<>(); //create all levels of labyrinth
			for(int i = 0; i < this.levels; i++) {
				this.levelsOfLabyrinth.add(new LabyrinthsArea(input, this.rows, this.columns));
				if(input.hasNext()) {
					input.nextLine();//delete interval between last string previous area and first string next area
				}
			}
			//-----------------------------------------------------------------------------
		} catch (IOException | NoSuchElementException e) {
			e.printStackTrace();
		}
	}

	/*public*/ Labyrinth()
	{
		this("INPUT.txt");
	}
	//-----------------------------------------------------------------------------methods for constructors
	private boolean isCorrectParametersOfLabyrinth(String s)
	{
		if(s.matches("[1-9]\\d? [1-9]\\d? [1-9]\\d?")) {
			String[] tokens = s.split(" ");
			for(String token : tokens) {
				if((Integer.valueOf(token) < 2) || (Integer.valueOf(token) > 50)) {
					return false;
				}
			}
		}
		return true;
	}
	//-----------------------------------------------------------------------------
	/*public*/ void setDownPoints()
	//find point where we can go to lower level
	{
		for(int i = 0; i < this.levels - 1; i++) {
			LabyrinthsArea upperArea = this.levelsOfLabyrinth.get(i);
			LabyrinthsArea lowerArea = this.levelsOfLabyrinth.get(i + 1);
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

	/*public*/ int way()
	{
		ArrayList<DownTable> allDownTables = new ArrayList<>();
		this.fillDownTables(allDownTables, new int[this.rows][this.columns], this.levelsOfLabyrinth.get(0),
				0,0, princeRaw, princeColumn);
		//-----------------------------------------------------------------------------
		return this.findShortWay(allDownTables);
	}

	private void fillDownTables(ArrayList<DownTable> allDownTables, int[][] arrayOfPaces, LabyrinthsArea currentArea,
				   int group, int currentLevel, int princeRaw, int princeColumn)
	{
		this.fillArray(arrayOfPaces);
		arrayOfPaces[princeRaw][princeColumn] = 0;
		this.fillPaces(currentArea, arrayOfPaces, princeRaw, princeColumn,
				this.rows - 1, this.columns - 1, 0);
		//-----------------------------------------------------------------------------
		if(currentLevel != (this.levels - 1)) {
			for (int row = 0; row < this.rows; row++) {
				for (int column = 0; column < this.columns; column++) {
					if(-2 != arrayOfPaces[row][column]) {
						if (FieldsInLabyrinth.DOWNPOINT == currentArea.getElementOfArea(row, column)) {
							allDownTables.add(new DownTable(row, column, arrayOfPaces[row][column], currentLevel, group));
							group++;
						}
					}
				}
			}
			ArrayList <DownTable> thisDownTables = new ArrayList<>(allDownTables);
			for(DownTable downTable : thisDownTables) {
				if(downTable.getLevel() == currentLevel) {
					this.fillDownTables(allDownTables, arrayOfPaces, this.levelsOfLabyrinth.get(currentLevel + 1),
							downTable.getGroup(), currentLevel + 1,
							downTable.getRaw(), downTable.getColumn());
				}
			}
		}
		//-----------------------------------------------------------------------------
		else {
			for (int row = 0; row < this.rows; row++) {
				for (int column = 0; column < this.columns; column++) {
					if(-2 != arrayOfPaces[row][column]) {
						if (FieldsInLabyrinth.PRINCESS == currentArea.getElementOfArea(row, column)) {
							allDownTables.add(new DownTable(row, column, arrayOfPaces[row][column], currentLevel, group));
							group++;
						}
					}
				}
			}
		}
	}

	private void fillPaces(LabyrinthsArea area, int[][] array, int currentRow, int currentColumn,
						  int maxRow, int maxColumn, int currentPace)
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

		if((currentRow != 0) && (area.getElementOfArea(currentRow - 1, currentColumn) != FieldsInLabyrinth.COLUMN)) {
			if((array[currentRow - 1][currentColumn] > currentPace + 1) || (array[currentRow - 1][currentColumn] == -2)) {
				canGoSouth = true;
				array[currentRow - 1][currentColumn] = currentPace + 1;
			}

		}
		if((currentRow != maxRow) && (area.getElementOfArea(currentRow + 1, currentColumn) != FieldsInLabyrinth.COLUMN)) {
			if(array[currentRow + 1][currentColumn] > currentPace + 1 || (array[currentRow + 1][currentColumn] == -2)) {
				canGoNorth = true;
				array[currentRow + 1][currentColumn] = currentPace + 1;
			}
		}
		if((currentColumn != 0) && (area.getElementOfArea(currentRow, currentColumn - 1) != FieldsInLabyrinth.COLUMN)) {
			if(array[currentRow][currentColumn - 1] > currentPace + 1 || (array[currentRow][currentColumn - 1] == -2)) {
				canGoWest = true;
				array[currentRow][currentColumn - 1] = currentPace + 1;
			}
		}
		if((currentColumn != maxColumn) && (area.getElementOfArea(currentRow, currentColumn + 1) != FieldsInLabyrinth.COLUMN)) {
			if(array[currentRow][currentColumn + 1] > currentPace + 1 || (array[currentRow][currentColumn + 1] == -2)) {
				canGoEast = true;
				array[currentRow][currentColumn + 1] = currentPace + 1;
			}
		}
		//-----------------------------------------------------------------------------
		if(canGoSouth) {
			this.fillPaces(area, array, currentRow-1, currentColumn, maxRow, maxColumn, currentPace + 1);
		}
		if(canGoNorth) {
			this.fillPaces(area, array, currentRow+1, currentColumn, maxRow, maxColumn, currentPace + 1);
		}
		if(canGoWest) {
			this.fillPaces(area, array, currentRow, currentColumn-1, maxRow, maxColumn, currentPace + 1);
		}
		if(canGoEast) {
			this.fillPaces(area, array, currentRow, currentColumn+1, maxRow, maxColumn, currentPace + 1);
		}

	}

	private int findShortWay(ArrayList<DownTable> allDownTables)
	{
		//-----------------------------------------------------------------------------
		ArrayList<HashMap<Integer, Integer>> ALH = new ArrayList<>();
		this.fillALH(allDownTables, new HashMap<>(), ALH, 0, (this.levels - 1));
		//-----------------------------------------------------------------------------
		Integer count = 0;
		ArrayList<Integer> counts = new ArrayList<>();
		for(HashMap<Integer, Integer> i : ALH) {
			for(Integer j : i.values()) {
				count += j;
			}
			counts.add(count);
			count = 0;
		}

		return (Collections.min(counts) + (this.levels - 1)) * 5;
	}

	private void fillALH(ArrayList<DownTable> group, HashMap<Integer, Integer> groupMap,
				 ArrayList<HashMap<Integer, Integer>> ALH, int groupIterator, int maxLevel)
	{
		//recursive method for filling ArrayList of HashMap's
		//group - ArrayList where store all possible places of bypasses to lower level (also there we store position of princess for lowest level)
		//groupMap - HashMap where store overall bypass from Prince to Princess for one particular case
		//ALH - all available bypasses from Prince to Princess
		//groupIterator - help choose current particular group
		//-----------------------------------------------------------------------------
		if(maxLevel != group.get(groupIterator).getLevel()) {
			Integer temp = groupMap.put(group.get(groupIterator).getLevel(), group.get(groupIterator).getPaces());
			//groupMap exchange variable, but we need it, and store it in temp
			//-----------------------------------------------------------------------------
			if (temp != null) {
				HashMap<Integer, Integer> tempMap = new HashMap<>(groupMap);
				tempMap.put(group.get(groupIterator).getLevel(), temp);
				this.fillALH(group, groupMap, ALH, groupIterator + 1, maxLevel);
				this.fillALH(group, tempMap, ALH, groupIterator + 1, maxLevel);
			}
			//-----------------------------------------------------------------------------
			else {
				this.fillALH(group, groupMap, ALH, groupIterator + 1, maxLevel);
			}
			group.remove(groupIterator);
			//-----------------------------------------------------------------------------
		}
		//-----------------------------------------------------------------------------
		else {
			groupMap.put(group.get(group.size() - 1).getLevel(), group.get(group.size() - 1).getPaces());
			ALH.add(new HashMap<>(groupMap));
			group.remove(group.get(group.size() - 1));
		}
		//-----------------------------------------------------------------------------
	}

	private void fillArray(int[][] array)
	{
		for(int[] ar : array) {
			Arrays.fill(ar, -2);
		}
	}

}
//-----------------------------------------------------------------------------
/*public*/class LabyrinthsArea {
	//-----------------------------------------------------------------------------fields
	private FieldsInLabyrinth[][] levelInLabyrinth;
	//-----------------------------------------------------------
	/*public*/ LabyrinthsArea(Scanner input, int rows, int columns)
	{
		String[] tokens = new String[rows];//gets area symbols
		try {
			for (int i = 0; i < rows; i++) {
				if (input.hasNext()) {
					tokens[i] = input.nextLine().strip();
				}
				else {
					throw new IOException("Insufficient data in file");
				}
				if (!this.isCorrectContentOfLabyrinth(tokens[i], columns)) {
					throw new IOException("Incorrect value in file!");
				}
			}
		} catch (IOException | NoSuchElementException e) {
			e.printStackTrace();
		}
		//-----------------------------------------------------------------------------
		this.levelInLabyrinth = new FieldsInLabyrinth[rows][columns];
		//-----------------------------------------------------------------------------
		for(int j = 0; j < rows; j++) {
			for(int k = 0; k < columns; k++) {
				switch (tokens[j].charAt(k)) {
					case '.':
						this.levelInLabyrinth[j][k] = FieldsInLabyrinth.EMPTY;
						break;
					case 'o':
						this.levelInLabyrinth[j][k] = FieldsInLabyrinth.COLUMN;
						break;
					case '1':
						this.levelInLabyrinth[j][k] = FieldsInLabyrinth.PRINCE;
						Labyrinth.princeRaw = j;
						Labyrinth.princeColumn = k;
						break;
					case '2':
						this.levelInLabyrinth[j][k] = FieldsInLabyrinth.PRINCESS;
						break;
				}
			}
		}
		//-----------------------------------------------------------------------------
	}
	//-----------------------------------------------------------------------------methods for constructors
	private boolean isCorrectContentOfLabyrinth(String s, int columns)
	{
		return ((s.matches("[12o.\n]+")) && (columns == s.length()));
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------methods

	/*public*/ FieldsInLabyrinth getElementOfArea(int raw, int column)
	{
		return this.levelInLabyrinth[raw][column];
	}

	/*public*/ void changeElementOfArea(int raw, int column)
	{
		this.levelInLabyrinth[raw][column] = FieldsInLabyrinth.DOWNPOINT;
	}
}
//-----------------------------------------------------------------------------

/*public*/class DownTable {
	//-----------------------------------------------------------------------------fields
	private int raw;
	private int column;
	private int paces;
	private int level;
	private int group;

	/*public*/ DownTable(int raw, int column, int paces, int level, int group)
	{
		this.raw = raw;
		this.column = column;
		this.paces = paces;
		this.level = level;
		this.group = group;
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------methods
	/*public*/ int getRaw()
	{
		return this.raw;
	}

	/*public*/ int getColumn()
	{
		return this.column;
	}

	/*public*/ int getPaces() {
		return this.paces;
	}

	/*public*/ int getLevel() {
		return this.level;
	}

	/*public*/ int getGroup() {
		return this.group;
	}
}
//-----------------------------------------------------------------------------
/*public*/enum FieldsInLabyrinth {
	EMPTY, COLUMN, PRINCE, PRINCESS, DOWNPOINT
}
