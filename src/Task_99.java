import java.awt.geom.Area;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Task_99 {

	public static void main(String[] args) {
		Labyrinth L = new Labyrinth();
		L.setDownPoints();
		int w = L.way();
	}
}

class Labyrinth {

	private int levels = 0;
	private int rows = 0;
	private int columns = 0;
	//-----------------------------------------------------------
	private ArrayList <LabyrinthsArea> levelsOfLabyrinth;
	//-----------------------------------------------------------
	public Labyrinth(String path)
	{

		try(Scanner input = new Scanner(Paths.get(path))) {
			//-----------------------------------------------------------
			if(input.hasNext()) {
				String parametersOfLabyrinth = input.nextLine().strip();
				if(isCorrectParametersOfLabyrinth(parametersOfLabyrinth)) {
					String[] tokensParametersOfLabyrinth = parametersOfLabyrinth.split(" ");
					levels = Integer.valueOf(tokensParametersOfLabyrinth[0]);//сделать через цикл
					rows = Integer.valueOf(tokensParametersOfLabyrinth[1]);
					columns = Integer.valueOf(tokensParametersOfLabyrinth[2]);
				}
			}
			else {

			}
			//-----------------------------------------------------------
			if((this.levels < 2) || (this.levels > 50) ||
					(this.rows < 2) || (this.rows > 50) ||
					(this.columns < 2) || (this.columns > 50)) {
				//нехорошо
			}
			//-----------------------------------------------------------


			levelsOfLabyrinth = new ArrayList<>();
			for(int i = 0; i < levels; i++) {
				levelsOfLabyrinth.add(new LabyrinthsArea(input, rows, columns));
				if(input.hasNext()) {
					input.nextLine();
				}
			} // проверить на соответствие levels, rows, columns


		} catch (IOException e) {

		}


	}

	public Labyrinth()
	{
		this("INPUT.txt");
	}

	public void setDownPoints()
	{

		//levelsOfLabyrinth.get(0).setAmountOfDownPoints(1); //
		for(int i = 0; i < levels - 1; i++) {
			int countOfDownPoints = 0; //
			LabyrinthsArea upperArea = levelsOfLabyrinth.get(i);
			LabyrinthsArea lowerArea = levelsOfLabyrinth.get(i + 1);
			for (int j = 0; j < this.rows; j++) {
				for(int k = 0; k < this.columns; k++) {
					if(FieldsInLabyrinth.COLUMN != upperArea.getPlace(j, k) &&
							FieldsInLabyrinth.COLUMN != lowerArea.getPlace(j, k)) {
						upperArea.change(FieldsInLabyrinth.DOWNPOINT,j, k);
						countOfDownPoints++;
					}
				}
				lowerArea.setAmountOfDownPoints(countOfDownPoints);
			}
		}
	}

	public int way()
	{
		int princeRaw = this.levelsOfLabyrinth.get(0).getPrinceRaw();
		int princeColumn = this.levelsOfLabyrinth.get(0).getPrinceColumn();
		LabyrinthsArea currentArea = this.levelsOfLabyrinth.get(0);
		ArrayList<DownTable> pointsOfDownTablesOfOneArea = new ArrayList<>();

		int[][] arrayOfPaces = new int[this.rows][this.columns];
		fillArray(arrayOfPaces);
		arrayOfPaces[princeRaw][princeColumn] = 0;

		recursive(currentArea, arrayOfPaces,
				princeRaw, princeColumn,
				this.rows - 1, this.columns - 1, 0);

//---------------------------------------------------------—
		int groupe = 0;
		for (int m = 0; m < this.rows; m++) {
			for (int n = 0; n < this.columns; n++) {
				if(-2 != arrayOfPaces[m][n]) {
					if (FieldsInLabyrinth.DOWNPOINT == currentArea.getPlace(m, n) ||
							FieldsInLabyrinth.PRINCESS == currentArea.getPlace(m, n)) {
						pointsOfDownTablesOfOneArea.add(new DownTable(m, n, arrayOfPaces[m][n], 0, groupe));
						groupe++;
					}
				}
				else {

				}
			}
		}

		ArrayList<ArrayList<DownTable>> allDownTablesOfOneLevel = new ArrayList<>();
		allDownTablesOfOneLevel.add(pointsOfDownTablesOfOneArea);

		ArrayList<ArrayList<ArrayList<DownTable>>> allDownTables = new ArrayList<>();
		allDownTables.add(allDownTablesOfOneLevel);


		for(int i = 1; i < this.levels; i++) {
			ArrayList<ArrayList<DownTable>> tempAllDownTablesOfOneLevel = new ArrayList<>();
			allDownTablesOfOneLevel = allDownTables.get(i - 1);
			for (int j = 0; j < allDownTablesOfOneLevel.size(); j++) {
				pointsOfDownTablesOfOneArea = allDownTablesOfOneLevel.get(j);
				for(int k = 0;k < pointsOfDownTablesOfOneArea.size(); k++) {
					groupe = pointsOfDownTablesOfOneArea.get(k).getGroupe();
					ArrayList<DownTable> tempPoints = new ArrayList<>();
					princeRaw = pointsOfDownTablesOfOneArea.get(k).getRaw();
					princeColumn = pointsOfDownTablesOfOneArea.get(k).getColumn();
					currentArea = this.levelsOfLabyrinth.get(i);

					arrayOfPaces = new int[this.rows][this.columns];
					fillArray(arrayOfPaces);
					arrayOfPaces[princeRaw][princeColumn] = 0;

					recursive(currentArea, arrayOfPaces,
							princeRaw, princeColumn,
							this.rows - 1, this.columns - 1, 0);

//---------------------------------------------------------—
					for (int m = 0; m < this.rows; m++) {
						for (int n = 0; n < this.columns; n++) {
							if(-2 != arrayOfPaces[m][n]) {
								if (FieldsInLabyrinth.DOWNPOINT == currentArea.getPlace(m, n) ||
										FieldsInLabyrinth.PRINCESS == currentArea.getPlace(m, n)) {
									tempPoints.add(new DownTable(m, n, arrayOfPaces[m][n], i, groupe));
								}
							}
						}
					}


					tempAllDownTablesOfOneLevel.add(tempPoints);
				}

			}

			allDownTables.add(tempAllDownTablesOfOneLevel);
		}

		return shortWay(allDownTables);
	}

	public void recursive(LabyrinthsArea Area, int[][] Array, int i, int j, int maxI, int maxJ, int current)
	{
		boolean canGoSouth = false;
		boolean canGoNorth = false;
		boolean canGoWest = false;
		boolean canGoEast = false;

		if((i != 0) && (Area.getPlace(i - 1, j) != FieldsInLabyrinth.COLUMN)) {
			if((Array[i - 1][j] > current + 1) || (Array[i - 1][j] == -2)) {
				canGoSouth = true;
				Array[i - 1][j] = current + 1;
			}

		}
		if((i != maxI) && (Area.getPlace(i + 1, j) != FieldsInLabyrinth.COLUMN)) {
			if(Array[i + 1][j] > current + 1 || (Array[i + 1][j] == -2)) {
				canGoNorth = true;
				Array[i + 1][j] = current + 1;
			}
		}
		if((j != 0) && (Area.getPlace(i, j - 1) != FieldsInLabyrinth.COLUMN)) {
			if(Array[i][j - 1] > current + 1 || (Array[i][j - 1] == -2)) {
				canGoWest = true;
				Array[i][j - 1] = current + 1;
			}
		}
		if((j != maxJ) && (Area.getPlace(i, j + 1) != FieldsInLabyrinth.COLUMN)) {
			if(Array[i][j + 1] > current + 1 || (Array[i][j + 1] == -2)) {
				canGoEast = true;
				Array[i][j + 1] = current + 1;
			}
		}
		//-----------------------------------------------------------
		if(canGoSouth) {
			recursive(Area, Array, i-1, j, maxI, maxJ, current+1);
		}
		if(canGoNorth) {
			recursive(Area, Array, i+1, j, maxI, maxJ, current+1);
		}
		if(canGoWest) {
			recursive(Area, Array, i, j-1, maxI, maxJ, current+1);
		}
		if(canGoEast) {
			recursive(Area, Array, i, j+1, maxI, maxJ, current+1);
		}

	}

	int shortWay(ArrayList<ArrayList<ArrayList<DownTable>>> allDownTables)
	{
		int shortWay = 0;
		ArrayList<DownTable> groupe = new ArrayList<>();

		TreeMap<Integer, Integer> groupeMap = new TreeMap<>();
		int number = 0;
		for(int i = 0; i < allDownTables.size(); i++) {
			for(int j = 0; j < allDownTables.get(i).size(); j++) {
				for(int k = 0; k < allDownTables.get(i).get(j).size(); k++) {
					if(allDownTables.get(i).get(j).get(k).getGroupe() == number){
						groupe.add(allDownTables.get(i).get(j).get(k));
					}
				}
			}
		}
		ArrayList<HashMap<Integer, Integer>> ALH = new ArrayList<>();

		shortSum(groupe, new HashMap<>(), ALH, 0, groupe.get(groupe.size()-1).getLevel());

		Integer min = 99999999;
		Integer count = 0;
		for(HashMap<Integer, Integer> i : ALH) {
			for(int j = 0; j < i.size(); j++) {
				count  += i.get(j);
			}
			if(count < min) {
				min = count;
			}
			count = 0;
		}

		return (min + (this.levels - 1)) * 5;
	}

	void shortSum(ArrayList<DownTable> groupe, HashMap<Integer, Integer> groupeMap,
				 ArrayList<HashMap<Integer, Integer>> ALH, int i, int level)
	{


		if(level != groupe.get(i).getLevel()) {
			Integer temp = groupeMap.put(groupe.get(i).getLevel(), groupe.get(i).getPaces());
			if (temp != null) {
				HashMap<Integer, Integer> tempMap = new HashMap<>(groupeMap);
				tempMap.put(groupe.get(i).getLevel(), temp);
				shortSum(groupe, groupeMap, ALH, i + 1, level);
				shortSum(groupe, tempMap, ALH, i + 1, level);
			}

			else {
				shortSum(groupe, groupeMap, ALH, i + 1, level);
			}
			groupe.remove(i);
		}
		else {
			groupeMap.put(groupe.get(groupe.size() - 1).getLevel(), groupe.get(groupe.size() - 1).getPaces());
			ALH.add(new HashMap<>(groupeMap));
			groupe.remove(groupe.get(groupe.size() - 1));
		}
	}


	boolean isCorrectParametersOfLabyrinth(String s)
	{
		return s.matches("[1-9]\\d? [1-9]\\d? [1-9]\\d?");
	}

	public void fillArray(int[][] Array)
	{
		for(int i = 0; i < this.rows; i++) {
			Arrays.fill(Array[i], -2);
		}
	}

}

class LabyrinthsArea {
	private int princeRaw;
	private int princeColumn;
	private int amountOfDownPoints;
	//-----------------------------------------------------------
	private FieldsInLabyrinth[][] levelInLabyrinth;
	//-----------------------------------------------------------
	public LabyrinthsArea(Scanner input, int rows, int columns)
	{
		String[] tokens = new String[rows];

		for(int i = 0; i < rows; i++) {
			if(input.hasNext()) {
				tokens[i] = input.nextLine().strip();
			}
			else {
				;
			}
			if(!isCorrectContentOfLabyrinth(tokens[i])) {
				;
			}
		}



		levelInLabyrinth = new FieldsInLabyrinth[rows][columns];
		//-----------------------------------------------------------
		for(int j = 0; j < rows; j++) {
			for(int k = 0; k < columns; k++) {
				switch (tokens[j].charAt(k)) {
					case '.':
						levelInLabyrinth[j][k] = FieldsInLabyrinth.EMPTY;
						break;
					case 'o':
						levelInLabyrinth[j][k] = FieldsInLabyrinth.COLUMN;
						break;
					case '1':
						levelInLabyrinth[j][k] = FieldsInLabyrinth.PRINCE;
						princeRaw = j;
						princeColumn = k;
						break;
					case '2':
						levelInLabyrinth[j][k] = FieldsInLabyrinth.PRINCESS;
						break;
				}
			}
		}
		//-----------------------------------------------------------
	}

	boolean isCorrectContentOfLabyrinth(String s)
	{
		return s.matches("([1]*|[2]*)[o.\n]+");
	}

	public int getPrinceRaw()
	{
		return this.princeRaw;
	}

	public int getPrinceColumn()
	{
		return this.princeColumn;
	}

	public int getAmountOfDownPoints()
	{
		return this.amountOfDownPoints;
	}

	public void setAmountOfDownPoints(int amountOfDownPoints)
	{
		this.amountOfDownPoints = amountOfDownPoints;
	}

	FieldsInLabyrinth getPlace(int Raw, int Column)
	{
		return this.levelInLabyrinth[Raw][Column];
	}

	void change(FieldsInLabyrinth Place,int Raw, int Column)
	{
		this.levelInLabyrinth[Raw][Column] = Place;
	}
}


class DownTable {
	private int raw;
	private int column;
	private int paces;

	private int level;
	private int groupe;

	public DownTable(int raw, int column, int paces, int level, int groupe)
	{
		this.raw = raw;
		this.column = column;
		this.paces = paces;
		this.level = level;
		this.groupe = groupe;
	}

	public int getRaw()
	{
		return this.raw;
	}

	public int getColumn()
	{
		return column;
	}

	public int getPaces() {
		return paces;
	}

	public int getLevel() {
		return level;
	}

	public int getGroupe() {
		return groupe;
	}
}

enum FieldsInLabyrinth {
	EMPTY, COLUMN, PRINCE, PRINCESS, DOWNPOINT
}
