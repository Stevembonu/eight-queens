//NAME : Ugochukwu Stephen Mbonu



import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.util.Scanner;
import java.util.Arrays;
import java.io.File;
import java.lang.*;



public class EightQueens {
	
	public static void main(String args[]) throws IOException{ 
		
		Scanner file = null;
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		try {
			file = new Scanner(new File("infile.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while(file.hasNext()){
			if (file.hasNextInt()) list.add(file.nextInt());
			else file.next();
		}
		
		
		
		int numberOfTotalStates = list.get(0);
		list.remove(0);
		list.removeAll(Arrays.asList(" "));
		
		try {
			File file2 =new File("outfile.txt");
			FileWriter fw2 = new FileWriter(file2,false); //so it can delete all contents of the existing outfile
			BufferedWriter bw2 = new BufferedWriter(fw2);
			PrintWriter pw2 = new PrintWriter(bw2);
			pw2.println("");
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		List<List<Integer>> ls2d = new ArrayList<List<Integer>>();
		ArrayList<Integer> listTwo = new ArrayList<Integer>();
		int listTwoCounter = 0; //MAYNOT NEED
		int[] statesFromInput = new int[9];
		for(int i = 0; i <  list.size(); i++) {
			if (listTwo.size() == 8) {
				listTwo.add(0,0); //to make it 9 too, we add 0 to 0th position
				statesFromInput = listTwo.stream().mapToInt(Integer::intValue).toArray(); //convert to primitive array
				listTwo.clear();
				CreateChessboard chessb = new CreateChessboard(statesFromInput, numberOfTotalStates);
				chessb.printArray();
						
			}
			listTwo.add(list.get(i));
			
		}
		
		
		listTwo.add(0,0); //to make it 9 too, we add 0 to 0th position
		statesFromInput = listTwo.stream().mapToInt(Integer::intValue).toArray(); //convert to primitive array
		
		CreateChessboard chessb = new CreateChessboard(statesFromInput, numberOfTotalStates);
		chessb.printArray();
		
		
	}
}		
		
		
		
		
class CreateChessboard { 
		
	int[] initialStates;
	int[] numConflicts = new int[9];
	boolean isThereConflict = false;
	boolean weHaveClonedCopyOfState = false;
	boolean haveCheckedQsToMove = false;
	boolean havePrintedSuccessOrFailure = false; //for printSuccessorFailure function
	boolean weWillPrint = true;
	boolean successOrFailure = true;
	int[] copyOfState;
	int generalQMoveCounter = 0;
	int queensThatNeedMoving = 0;
	int[] successArray = {100,1000,0000};
	int[] failureArray = {666,419,911};
	int numberOfTotalStates;
	public static int ourWritingToFileCounter = 1; //public static so that it is same instance for all methods here and our last method can increment it generally
	
		
	CreateChessboard(int[] initialStates, int numberOfTotalStates) {
		this.initialStates = initialStates;    //constructor to hand statesFromInput to our class
		this.numberOfTotalStates = numberOfTotalStates;    
	}
	

	
	
	
	public void printArray() {
		justPrintArray(initialStates);
		checkConflicts(initialStates, numConflicts);
	}	
	
	public void justPrintArray(int [] arrayToPrint) {
		
		sortAndPrint(arrayToPrint);
		for(int i = 1; i < 9; i++) {
			if(i == 8) {
				System.out.println(arrayToPrint[i]);
			}
			else {
				System.out.print(arrayToPrint[i]+" ");
			}
		}
	}
	
	
	
	
	public void checkConflicts(int [] initialStates, int [] numConflicts) {
		for(int i = 0; i < 9; i++) {
			numConflicts[i] = 0;		//set all in numConflicts array to zero bcos of when we call this function a 2nd time after moving a Q
		}
		for (int i = 1; i < 9; i++) {
			for (int j = 1 + i; j < 9; j++) {  //take a state[i] and compare to next ones starting from [j]
				if ((initialStates[i] == initialStates[j]) || (Math.abs(initialStates[i] - initialStates[j]) == Math.abs(i - j))) {
					numConflicts[i]++; //count how many conflicts fot both i and j
					numConflicts[j]++;
					isThereConflict = true;
				
				}
			}
		}
		
		
		if (!haveCheckedQsToMove) {
			for (int i = 1; i < 9; i++) { //check total no. of Qs that need moving so we can set generalQMoveCounter to it
				if (numConflicts[i] > 0) {
					queensThatNeedMoving++;
					haveCheckedQsToMove = true;
				}
			}
		}
		
		
		if(isThereConflict){
			printArrayConflictsAndCheckForMove(numConflicts);
		}
		else {
			
		}
	}
	
	public void printArrayConflictsAndCheckForMove(int [] arrayToPrint) { //we passed numConflicts array to it. so it is printing numConflicts here
		
		int maxConflict = Arrays.stream(arrayToPrint).max().getAsInt(); //get maximum number in array
		
		int maxConflictIndex = findMaxConflictIndex(arrayToPrint, maxConflict); //we want to get the index. ie 8 is max number but which queen has 8 conflicts?
		
		
		if (isThereConflict) { //if boolean isThereConflict is true, move queen with conflict below
			moveQueenWithConflict(initialStates, maxConflictIndex, numConflicts);  //passed the initial state we got, the position of Q with highest conflict and all conflicts array
		}
	
	}
	
	
	public int checkAndReturnConflicts(int indexOfQueenToCheckItsConflict, int[] theState) { //quick check and return conflict without printing
		int countingConflicts = 0;
	
		for (int i = 1; i < 9; i++) {
			if ((theState[i] == theState[indexOfQueenToCheckItsConflict]) 
				|| (Math.abs(theState[i] - theState[indexOfQueenToCheckItsConflict]) == Math.abs(i - indexOfQueenToCheckItsConflict))) {
					countingConflicts++;
				
			}
		}
		countingConflicts = countingConflicts - 1; // because the loop will make it check with itself and count it as conflict
		return countingConflicts;
	}					
	
	
	
	public static int findMaxConflictIndex(int[] arrayToLookInto, int maxConflict) {
		return IntStream.range(0, arrayToLookInto.length)
                    .filter(i -> maxConflict == arrayToLookInto[i])
                    .findFirst()
                    .orElse(-1);    // return -1 if the target is not found
					
	}
	
	
	
	public void moveQueenWithConflict (int[] state, int maxConflictIndex, int []numConflicts) { //this state, this queen eg queen 6, num of conflicts array
		int maxConflict = numConflicts[maxConflictIndex]; //get max conflicts for the Q so we can check when we have a smaller conflict
		
		
		if (!weHaveClonedCopyOfState) { //so we dont copy the original again
			copyOfState = state.clone(); //so we can make changes without touching the original
		}
		
		int lowestConflictNumberForQueen = maxConflict;
		int lowestConflictNumberForQueenRow = state[maxConflictIndex];
		int rowMoveCounter = 0;
		
		weWillPrint = false; //set it to false so that it won't print unless change is made
		
		for(int i = 1; i < 9; i++) {
			//copyOfState[maxConflictIndex] = i; //the Q in the maxConflictIndex of this array eg Q in 6th position. I think I don't need this. using i
			copyOfState[maxConflictIndex] = i; //move the queen to i. this moves Q to every row
							
				if (lowestConflictNumberForQueen >  checkAndReturnConflicts(maxConflictIndex, copyOfState)) {
					lowestConflictNumberForQueenRow = i; //update the row as well as num of conflicts 
					lowestConflictNumberForQueen = checkAndReturnConflicts(maxConflictIndex, copyOfState);
					weHaveClonedCopyOfState = true;
					weWillPrint = true; //if Q is moved, we have to print new array
					
				}
			
		}
		
		
		for(int i = 1; i < 9; i++) {
			if (lowestConflictNumberForQueen <=  checkAndReturnConflicts(maxConflictIndex, copyOfState)){  	//if it checks row i and it is not okay 
				//to move Q there, count++. if counter is 7(cos it already worked on the 1st conflict in this func), then we have checked all rows
					rowMoveCounter++;
					
			}
		}
		
		copyOfState[maxConflictIndex] = lowestConflictNumberForQueenRow; //finally move Q to the lowest conflict row
		
		if(weWillPrint) { //in case there is just one total conflict, so it can still print changed array
			justPrintArray(copyOfState);
		}
		
		
		checkIfToChangeStates(copyOfState, rowMoveCounter, numConflicts);
		
	}
	
	
	
	public void checkIfToChangeStates(int[] copyOfState, int rowMoveCounter, int[] numConflicts) { //tell it to stop when it gets to a 'failure' state
		if (rowMoveCounter > 6) {
			generalQMoveCounter ++; //will count overall Qs that are in best position even if they are not actually moved
			
		}
		
		if(generalQMoveCounter == queensThatNeedMoving) {
	
			if(isThereConflict) {
				
				successOrFailure = false; 
			}
			else {
				
			}
			
		}
		else{
			isThereConflict = false;
			checkConflicts(copyOfState, numConflicts);
		}
		
		printSuccessorFailure(successOrFailure); // func will check what to print what successOrFailure var.
	}
	
	
	public void printSuccessorFailure(boolean successOrFailure) {
		if(!havePrintedSuccessOrFailure) {
			if(successOrFailure) {
			System.out.println("Success\n");
			try {
				usingPrintWriter(successArray);
			}	
			catch(IOException e) {
				e.printStackTrace();
			}
			}
			else {
			System.out.println("Failure\n");
			try {
				usingPrintWriter(failureArray);
			}	
			catch(IOException e) {
				e.printStackTrace();
			}
			}
			havePrintedSuccessOrFailure = true;
			
			
		}
		
		
	}
	
	
	
	public void sortAndPrint(int[] arrayToPrint) {
		try {
			
			usingPrintWriter(arrayToPrint);
		}
		
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void usingPrintWriter(int[] arrayToPrint) throws IOException {
		File file =new File("outfile.txt");
		FileWriter fw = new FileWriter(file,true); //so it can append to the file
    	BufferedWriter bw = new BufferedWriter(fw);
    	PrintWriter pw = new PrintWriter(bw);
		
		if (Arrays.equals(arrayToPrint, successArray)) {
			if(ourWritingToFileCounter < numberOfTotalStates) { //if it has printed enough, print the last success or failure without new line
				ourWritingToFileCounter++;
				pw.println("Success\n");
				
			}
			else {
				ourWritingToFileCounter++;
				pw.print("Success");
				
			}
		}
		else if (Arrays.equals(arrayToPrint, failureArray)) {
			if(ourWritingToFileCounter < numberOfTotalStates) {
				ourWritingToFileCounter++;
				pw.println("Failure\n");
				
				
			}
			else {
				ourWritingToFileCounter++;
				pw.print("Failure");
			}
		}
		
		else {
			for(int i = 1; i<9; i++) {
				if(i == 8) {
					pw.println(arrayToPrint[i]);
				}
				else {
					pw.print(arrayToPrint[i] + " ");
				}
			
			}
		}
		
		pw.close();
	
	}
	
	
}