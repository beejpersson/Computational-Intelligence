package coursework;

import java.lang.reflect.Field;
import java.util.Random;
import model.LunarParameters;
import model.NeuralNetwork;
import model.LunarParameters.DataSet;

public class Parameters {
 
	/**
	 * These parameter values can be changed 
	 * You may add other Parameters as required to this class 
	 * 
	 */
	private static int numHidden = 5;	
	private static int numGenes = calculateNumGenes();
	public static double minGene = -3; // specifies minimum and maximum weight values 
	public static double maxGene = +3;
	
	// Selection parameters
	public enum Select {
		RANDOM,
		TOURNAMENT
	}
	public static Select selectionAlgorithm = Select.TOURNAMENT;
	public static double selectTournamentSize = 20;
	
	// Reproduce parameters
	public enum Reproduce {
		EXACTCOPIES,
		ONEPTCROSSOVER,
		NPTCROSSOVER,
		UNIFORMCROSSOVER,
		ARITHMETICCROSSOVER
	}
	public static Reproduce reproductionAlgorithm = Reproduce.NPTCROSSOVER;
	public static int numberOfCutPoints = 5;
	
	// Replacement parameters
	public enum Replace {
		RANDOM,
		WORST,
		TOURNAMENT
	}
	public static Replace replacementAlgorithm = Replace.RANDOM;
	public static double replaceTournamentSize = 20;
		
	public static int popSize = 200;
	public static int maxEvaluations = 10000;
	
	// Parameters for mutation 
	// Rate = probability of changing a gene
	// Change = the maximum +/- adjustment to the gene value
	public static double mutateRate = 0.01; // mutation rate for mutation operator
	public static double mutateChange = 0.05; // delta change for mutation operator
	
	//Random number generator used throughout the application
	public static long seed = System.currentTimeMillis();
	public static Random random = new Random(seed);

	//set the NeuralNetwork class here to use your code from the GUI
	public static Class neuralNetworkClass = ExampleEvolutionaryAlgorithm.class;
	
	/**
	 * Do not change any methods that appear below here.
	 * 
	 */
	
	public static int getNumGenes() {					
		return numGenes;
	}

	
	private static int calculateNumGenes() {
		int num = (NeuralNetwork.numInput * numHidden) + (numHidden * NeuralNetwork.numOutput) + numHidden + NeuralNetwork.numOutput;
		return num;
	}

	public static int getNumHidden() {
		return numHidden;
	}
	
	public static void setHidden(int nHidden) {
		numHidden = nHidden;
		numGenes = calculateNumGenes();		
	}

	public static String printParams() {
		String str = "";
		for(Field field : Parameters.class.getDeclaredFields()) {
			String name = field.getName();
			Object val = null;
			try {
				val = field.get(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str += name + ", " + val + "\r\n";
			
		}
		return str;
	}
	
	public static void setDataSet(DataSet dataSet) {
		LunarParameters.setDataSet(dataSet);
	}
	
	public static DataSet getDataSet() {
		return LunarParameters.getDataSet();
	}
	
	public static void main(String[] args) {
		printParams();
	}
}
