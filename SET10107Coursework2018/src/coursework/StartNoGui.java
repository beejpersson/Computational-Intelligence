package coursework;

import coursework.Parameters.Replace;
import coursework.Parameters.Reproduce;
import coursework.Parameters.Select;
import model.Fitness;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Example of how to to run the {@link ExampleEvolutionaryAlgorithm} without the need for the GUI
 * This allows you to conduct multiple runs programmatically 
 * The code runs faster when not required to update a user interface
 *
 */
public class StartNoGui {

	public static void main(String[] args) throws FileNotFoundException { 
		/**
		 * Train the Neural Network using our Evolutionary Algorithm 
		 * 
		 */

		/*
		 * Set the parameters here or directly in the Parameters Class.
		 * Note you should use a maximum of 20,0000 evaluations for your experiments 
		 */
		
		PrintWriter pw = new PrintWriter(new File("Test.csv"));
		StringBuilder sb = new StringBuilder();
		sb.append(Parameters.printParams());
		sb.append('\n');
		sb.append("Test,");
		sb.append("Fitness");
		sb.append('\n');
		
		for (int i = 1; i < 11; i++) {
		
			// Selection parameters
			Parameters.selectionAlgorithm = Select.RANDOM;
			Parameters.selectTournamentSize = 20;
			
			// Reproduction parameters
			Parameters.reproductionAlgorithm = Reproduce.ONEPTCROSSOVER;
			Parameters.numberOfCutPoints = 1;
			
			// Replacement parameters
			Parameters.replacementAlgorithm = Replace.WORST;
			Parameters.replaceTournamentSize = 20;
			
			Parameters.maxEvaluations = 20000; // Used to terminate the EA after this many generations
			Parameters.popSize = 200; // Population Size
	
			//number of hidden nodes in the neural network
			Parameters.setHidden(5);
			
			//Set the data set for training 
			Parameters.setDataSet(DataSet.Training);
			
			
			//Create a new Neural Network Trainer Using the above parameters 
			NeuralNetwork nn = new ExampleEvolutionaryAlgorithm();		
			
			//train the neural net (Go and make a coffee) 
			nn.run();
			
			/* Print out the best weights found
			 * (these will have been saved to disk in the project default directory) 
			 */
			System.out.println(nn.best);
		
		
		
			/**
			 * We now need to test the trained network on the unseen test Set
			 */
			Parameters.setDataSet(DataSet.Test);
			double fitness = Fitness.evaluate(nn);
			System.out.println("Fitness on " + Parameters.getDataSet() + " " + fitness);
			
			sb.append(i);
			sb.append(", ");
			sb.append(fitness);
			sb.append('\n');
		}
		
		pw.write(sb.toString());
		pw.close();
			
		
		/**
		 * Or We can reload the NN from the file generated during training and test it on a data set 
		 * We can supply a filename or null to open a file dialog 
		 * Note that files must be in the project root and must be named *-n.txt
		 * where "n" is the number of hidden nodes
		 * ie  1518461386696-5.txt was saved at timestamp 1518461386696 and has 5 hidden nodes
		 * Files are saved automatically at the end of training
		 *  
		 *  Uncomment the following code and replace the name of the saved file to test a previously trained network 
		 */
		
//		NeuralNetwork nn2 = NeuralNetwork.loadNeuralNetwork("1234567890123-5.txt");
//		Parameters.setDataSet(DataSet.Random);
//		double fitness2 = Fitness.evaluate(nn2);
//		System.out.println("Fitness on " + Parameters.getDataSet() + " " + fitness2);
		
		
		
	}
}
