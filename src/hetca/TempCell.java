package hetca;


/**
 *
 * @author DavidMedernach
 */
public class TempCell {

	public int Xcoor;
	public int Ycoor;
	int rulesnumber;
	

	public TempCell(int X,int Y,LocalCell cell) {
		Xcoor = X;
		Ycoor = Y;
		rulesnumber = cell.rulesnumber;
	}
	

	public TempCell(int X,int Y,int ruleNumber) {
		Xcoor = X;
		Ycoor = Y;
		rulesnumber = ruleNumber;
	}

}
