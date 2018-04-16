package coursework;

import java.util.ArrayList;
import java.util.Collections;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Implements a basic Evolutionary Algorithm to train a Neural Network
 * 
 * You Can Use This Class to implement your EA or implement your own class that extends {@link NeuralNetwork} 
 * 
 */
public class ExampleEvolutionaryAlgorithm extends NeuralNetwork {
	

	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {		
		//Initialise a population of Individuals with random weights
		population = initialise();

		//Record a copy of the best Individual in the population
		best = getBest(population);
		System.out.println("Best From Initialisation " + best);

		/**
		 * main EA processing loop
		 */		
		
		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * this is a skeleton EA - you need to add the methods.
			 * You can also change the EA if you want 
			 * You must set the best Individual at the end of a run
			 * 
			 */

			// Select 2 Individuals from the current population. Currently returns random Individual
			Individual parent1 = select(); 
			Individual parent2 = select();

			// Generate a child by crossover. Not Implemented			
			ArrayList<Individual> children = reproduce(parent1, parent2);			
			
			//mutate the offspring
			mutate(children);
			
			// Evaluate the children
			evaluateIndividuals(children);			

			// Replace children in population
			replace(children);

			// check to see if the best has improved
			best = getBest(population);
			
			// Implemented in NN class. 
			outputStats();
			
			//Increment number of completed generations			
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}

	

	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 * 
	 */
	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}


	/**
	 * Returns a copy of the best individual in the population
	 * 
	 */
	private Individual getBest(ArrayList<Individual> individuals) {
		best = null;;
		for (Individual individual : individuals) {
			if (best == null) {
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}

	/**
	 * Generates a randomly initialised population
	 * 
	 */
	private ArrayList<Individual> initialise() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	/**
	 * Selection --
	 * 
	 * NEEDS REPLACED with proper selection this just returns a copy of a random
	 * member of the population
	 */
	
	// Tournament selection algorithm
	private Individual selectTournament(){
		// List of randomly selected individuals for a tournament of size determined in parameters
		ArrayList<Individual> tournamentList = new ArrayList<>();
		for (int i = 0; i < Parameters.selectTournamentSize; i++) {
			tournamentList.add(population.get(Parameters.random.nextInt(Parameters.popSize)));
		}
		// Retrieve fittest from tournament list
		Individual bestInTournament = getBest(tournamentList);
		return bestInTournament;
	}
	
	// Calls the desired selection method based on parameter input
	private Individual select() {	
		switch (Parameters.selectionAlgorithm) {
		case RANDOM:
			Individual randomParent = population.get(Parameters.random.nextInt(Parameters.popSize));
			return randomParent.copy();
		case TOURNAMENT:
			Individual tournamentParent = selectTournament();
			return tournamentParent.copy();
		default:
			System.out.println("Warning! Selection method not chosen.");
			return null;
		}
	}

	/**
	 * Crossover / Reproduction	
	 * 
	 * NEEDS REPLACED with proper method this code just returns exact copies of the
	 * parents. 
	 */
	
	// 1-Point Crossover reproduction algorithm
	private ArrayList<Individual> onePtCrossover(Individual parent1, Individual parent2){
		// New array of 2 child individuals
		ArrayList<Individual> children = new ArrayList<Individual>();
		Individual child1 = new Individual();
		Individual child2 = new Individual();
		// Randomly chosen cut point along chromosome length
		int cutPoint = Parameters.random.nextInt(parent1.chromosome.length);	
		// Children's chromosomes made of the different genes sections of the respective parent
		for (int i = 0; i < cutPoint; i++){
			child1.chromosome[i] = parent1.chromosome[i];
			child2.chromosome[i] = parent2.chromosome[i];
		}
		for (int i = cutPoint; i < parent1.chromosome.length; i++){
			child1.chromosome[i] = parent2.chromosome[i];
			child2.chromosome[i] = parent1.chromosome[i];
		}
		children.add(child1.copy());
		children.add(child2.copy());		
		return children;
	}
	
	// n-Point Crossover reproduction algorithm
	private ArrayList<Individual> nPtCrossover(Individual parent1, Individual parent2){
		// New array of 2 child individuals
		ArrayList<Individual> children = new ArrayList<Individual>();
		Individual child1 = new Individual();
		Individual child2 = new Individual();
		// Array list of random cut points along chromosome length using number of cut points parameter
		ArrayList<Integer> cutPoints = new ArrayList<Integer>();
		for (int i = 0; i < Parameters.numberOfCutPoints; i++) {
			cutPoints.add(Parameters.random.nextInt(parent1.chromosome.length));
		}
		// Children's chromosomes made of the different genes sections of the respective parent
		// List sorted in ascending order, incremented in steps of two, provided a check for first and last list elements for chromosome cut-off points
		Collections.sort(cutPoints);
		int limit;
		for (int i = 0; i < cutPoints.size(); i+=2) {
			if (i != 0) {
				limit = cutPoints.get(i-1);
			} else {
				limit = 0;
			}
			for (int j = limit; j < cutPoints.get(i); j++){
				
				child1.chromosome[j] = parent1.chromosome[j];
				child2.chromosome[j] = parent2.chromosome[j];
			}
			if (i != cutPoints.size()-1) {
				limit = cutPoints.get(i+1);
			} else {
				limit = parent1.chromosome.length;
			}
			for (int j = cutPoints.get(i); j < limit ; j++){
				child1.chromosome[j] = parent2.chromosome[j];
				child2.chromosome[j] = parent1.chromosome[j];
			}
		}	
		children.add(child1.copy());
		children.add(child2.copy());		
		return children;
	}
	
	
	// Uniform Crossover reproduction algorithm
	private ArrayList<Individual> uniformCrossover(Individual parent1, Individual parent2){
		// New array of 2 child individuals
		ArrayList<Individual> children = new ArrayList<Individual>();
		Individual child1 = new Individual();
		Individual child2 = new Individual();
		// Children's chromosomes made by using random parent's gene at each position
		for (int i = 0; i < parent1.chromosome.length; i++){
			if (Math.random() <= 0.5){
				child1.chromosome[i] = parent1.chromosome[i];
				child2.chromosome[i] = parent2.chromosome[i];
			}
			else {
				child1.chromosome[i] = parent2.chromosome[i];
				child2.chromosome[i] = parent1.chromosome[i];
			}
		}
		children.add(child1.copy());
		children.add(child2.copy());		
		return children;
	}
	
	// Arithmetic Crossover reproduction algorithm
		private ArrayList<Individual> arithmeticCrossover(Individual parent1, Individual parent2){
			// New array of 2 child individuals
			ArrayList<Individual> children = new ArrayList<Individual>();
			Individual child1 = new Individual();
			Individual child2 = new Individual();
			// Two identical children whose chromosomes are made by averaging parent's gene values at each position
			for (int i = 0; i < parent1.chromosome.length; i++){
				child1.chromosome[i] = (parent1.chromosome[i]+parent2.chromosome[i]) / 2;
				child2.chromosome[i] = (parent1.chromosome[i]+parent2.chromosome[i]) / 2;
			}
			children.add(child1.copy());
			children.add(child2.copy());		
			return children;
		}
	
	// Calls the desired reproduction method based on parameter input
	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		switch (Parameters.reproductionAlgorithm){
		case EXACTCOPIES:
			ArrayList<Individual> copiesChildren = new ArrayList<>();
			copiesChildren.add(parent1.copy());
			copiesChildren.add(parent2.copy());		
			return copiesChildren;
		case ONEPTCROSSOVER:
			ArrayList<Individual> onePtCrossoverChildren = onePtCrossover(parent1, parent2);
			return onePtCrossoverChildren;
		case NPTCROSSOVER:
			ArrayList<Individual> nPtCrossoverChildren = nPtCrossover(parent1, parent2);
			return nPtCrossoverChildren;
		case UNIFORMCROSSOVER:
			ArrayList<Individual> uniformCrossoverChildren = uniformCrossover(parent1, parent2);
			return uniformCrossoverChildren;
		case ARITHMETICCROSSOVER:
			ArrayList<Individual> arithmeticCrossoverChildren = arithmeticCrossover(parent1, parent2);
			return arithmeticCrossoverChildren;
		default:
			System.out.println("Warning! Reproduction method not chosen.");
			return null;
		}
	} 
	
	/**
	 * Mutation
	 * 
	 * 
	 */
	private void mutate(ArrayList<Individual> individuals) {		
		for(Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < Parameters.mutateRate) {
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Parameters.mutateChange);
					} else {
						individual.chromosome[i] -= (Parameters.mutateChange);
					}
				}
			}
		}		
	}

	/**
	 * 
	 * Replaces the worst member of the population 
	 * (regardless of fitness)
	 * 
	 */
	
	// Tournament replacement algorithm
	private void replaceTournament(Individual individual){
		// List of randomly selected individuals for a tournament of size determined in parameters
		ArrayList<Individual> tournamentList = new ArrayList<>();
		Individual worstInTournament = new Individual();
		for (int i = 0; i < Parameters.replaceTournamentSize; i++) {
			tournamentList.add(population.get(Parameters.random.nextInt(Parameters.popSize)));
		}
		// Retrieve worst from tournament list
		int tournamentIdx = getWorstIndex(tournamentList);
		worstInTournament = tournamentList.get(tournamentIdx);
		// Remove tournament loser from population
		population.set(population.indexOf(worstInTournament), individual);
	}
	
	// Calls the desired replacement method based on parameter input
	private void replace(ArrayList<Individual> individuals) {
		switch (Parameters.replacementAlgorithm){
		case RANDOM:
			for(Individual individual : individuals) {
				population.set(Parameters.random.nextInt(Parameters.popSize), individual);
			}
			break;
		case WORST:
			for(Individual individual : individuals) {
				int idx = getWorstIndex(population);		
				population.set(idx, individual);
			}
			break;
		case TOURNAMENT:
			for(Individual individual : individuals) {
				replaceTournament(individual);
			}
			break;
		default:
			break;
		}
	}

	

	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex(ArrayList<Individual> individuals) {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < individuals.size(); i++) {
			Individual individual = individuals.get(i);
			if (worst == null) {
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness) {
				worst = individual;
				idx = i; 
			}
		}
		return idx;
	}	

	@Override
	public double activationFunction(double x) {
		if (x < -20.0) {
			return -1.0;
		} else if (x > 20.0) {
			return 1.0;
		}
		return Math.tanh(x);
	}
}
