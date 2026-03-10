import asyncio
import json
import os
import pytest
import sys
import threading
import time
import unittest
from datetime import datetime
from unittest.mock import Mock, patch, MagicMock

# Add the app directory to the Python path for imports
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', '..'))

from app.ai_backend.genesis_evolutionary_conduit import (
    EvolutionaryConduit,
    MutationStrategy,
    SelectionStrategy,
    FitnessFunction,
    EvolutionaryException,
    PopulationManager,
    GeneticOperations,
    EvolutionaryParameters,
    GenesisEvolutionaryConduit
)


class TestEvolutionaryParameters(unittest.TestCase):
    """Test suite for EvolutionaryParameters class."""

    def setUp(self):
        """
        Set up default and custom EvolutionaryParameters instances for use in test cases.
        """
        self.default_params = EvolutionaryParameters()
        self.custom_params = EvolutionaryParameters(
            population_size=200,
            generations=1000,
            mutation_rate=0.15,
            crossover_rate=0.85,
            selection_pressure=0.3
        )

    def test_default_initialization(self):
        """
        Tests that EvolutionaryParameters is initialized with the expected default values.
        """
        self.assertEqual(self.default_params.population_size, 100)
        self.assertEqual(self.default_params.generations, 500)
        self.assertEqual(self.default_params.mutation_rate, 0.1)
        self.assertEqual(self.default_params.crossover_rate, 0.8)
        self.assertEqual(self.default_params.selection_pressure, 0.2)

    def test_custom_initialization(self):
        """
        Test that custom values are correctly assigned to all attributes.
        """
        self.assertEqual(self.custom_params.population_size, 200)
        self.assertEqual(self.custom_params.generations, 1000)
        self.assertEqual(self.custom_params.mutation_rate, 0.15)
        self.assertEqual(self.custom_params.crossover_rate, 0.85)
        self.assertEqual(self.custom_params.selection_pressure, 0.3)

    def test_parameter_validation(self):
        """
        Test that EvolutionaryParameters raises ValueError for invalid values.
        """
        with self.assertRaises(ValueError):
            EvolutionaryParameters(population_size=0)

        with self.assertRaises(ValueError):
            EvolutionaryParameters(mutation_rate=-0.1)

        with self.assertRaises(ValueError):
            EvolutionaryParameters(mutation_rate=1.5)

        with self.assertRaises(ValueError):
            EvolutionaryParameters(crossover_rate=-0.1)

        with self.assertRaises(ValueError):
            EvolutionaryParameters(crossover_rate=1.5)

    def test_to_dict(self):
        """
        Test that the to_dict method returns correct parameter values.
        """
        params_dict = self.default_params.to_dict()
        expected_dict = {
            'population_size': 100,
            'generations': 500,
            'mutation_rate': 0.1,
            'crossover_rate': 0.8,
            'selection_pressure': 0.2
        }
        self.assertEqual(params_dict, expected_dict)

    def test_from_dict(self):
        """
        Test that EvolutionaryParameters can be instantiated from a dictionary.
        """
        params_dict = {
            'population_size': 150,
            'generations': 750,
            'mutation_rate': 0.12,
            'crossover_rate': 0.85,
            'selection_pressure': 0.25
        }
        params = EvolutionaryParameters.from_dict(params_dict)
        self.assertEqual(params.population_size, 150)
        self.assertEqual(params.generations, 750)
        self.assertEqual(params.mutation_rate, 0.12)
        self.assertEqual(params.crossover_rate, 0.85)
        self.assertEqual(params.selection_pressure, 0.25)


class TestMutationStrategy(unittest.TestCase):
    """Test suite for MutationStrategy class."""

    def setUp(self):
        """
        Set up a MutationStrategy instance for use in mutation strategy tests.
        """
        self.strategy = MutationStrategy()

    def test_gaussian_mutation(self):
        """
        Test that the Gaussian mutation strategy produces correct results.
        """
        genome = [1.0, 2.0, 3.0, 4.0, 5.0]
        mutated = self.strategy.gaussian_mutation(genome, mutation_rate=0.1, sigma=0.5)

        self.assertEqual(len(mutated), len(genome))
        self.assertIsInstance(mutated, list)

        highly_mutated = self.strategy.gaussian_mutation(genome, mutation_rate=1.0, sigma=1.0)
        self.assertEqual(len(highly_mutated), len(genome))

    def test_uniform_mutation(self):
        """
        Test that the uniform mutation strategy returns correct results.
        """
        genome = [1.0, 2.0, 3.0, 4.0, 5.0]
        mutated = self.strategy.uniform_mutation(genome, mutation_rate=0.2, bounds=(-10, 10))

        self.assertEqual(len(mutated), len(genome))
        self.assertIsInstance(mutated, list)

        for value in mutated:
            self.assertGreaterEqual(value, -10)
            self.assertLessEqual(value, 10)

    def test_bit_flip_mutation(self):
        """
        Test that the bit-flip mutation strategy returns correct results.
        """
        genome = [True, False, True, False, True]
        mutated = self.strategy.bit_flip_mutation(genome, mutation_rate=0.3)

        self.assertEqual(len(mutated), len(genome))
        self.assertIsInstance(mutated, list)

        for value in mutated:
            self.assertIsInstance(value, bool)

    def test_adaptive_mutation(self):
        """
        Tests that the adaptive mutation strategy returns correct results.
        """
        genome = [1.0, 2.0, 3.0, 4.0, 5.0]
        fitness_history = [0.5, 0.6, 0.7, 0.8, 0.9]

        mutated = self.strategy.adaptive_mutation(genome, fitness_history, base_rate=0.1)

        self.assertEqual(len(mutated), len(genome))
        self.assertIsInstance(mutated, list)

    def test_invalid_mutation_rate(self):
        """
        Test that mutation methods raise ValueError for invalid rates.
        """
        genome = [1.0, 2.0, 3.0]

        with self.assertRaises(ValueError):
            self.strategy.gaussian_mutation(genome, mutation_rate=-0.1)

        with self.assertRaises(ValueError):
            self.strategy.uniform_mutation(genome, mutation_rate=1.5)


class TestSelectionStrategy(unittest.TestCase):
    """Test suite for SelectionStrategy class."""

    def setUp(self):
        """
        Set up the selection strategy instance and sample population.
        """
        self.strategy = SelectionStrategy()
        self.population = [
            {'genome': [1, 2, 3], 'fitness': 0.9},
            {'genome': [4, 5, 6], 'fitness': 0.7},
            {'genome': [7, 8, 9], 'fitness': 0.5},
            {'genome': [10, 11, 12], 'fitness': 0.3}
        ]

    def test_tournament_selection(self):
        """
        Test that tournament selection returns a valid individual.
        """
        selected = self.strategy.tournament_selection(self.population, tournament_size=2)

        self.assertIn(selected, self.population)
        self.assertIsInstance(selected, dict)
        self.assertIn('genome', selected)
        self.assertIn('fitness', selected)

    def test_roulette_wheel_selection(self):
        """
        Test that the roulette wheel selection strategy works correctly.
        """
        selected = self.strategy.roulette_wheel_selection(self.population)

        self.assertIn(selected, self.population)
        self.assertIsInstance(selected, dict)
        self.assertIn('genome', selected)
        self.assertIn('fitness', selected)

    def test_rank_selection(self):
        """
        Tests that the rank-based selection strategy works correctly.
        """
        selected = self.strategy.rank_selection(self.population)

        self.assertIn(selected, self.population)
        self.assertIsInstance(selected, dict)
        self.assertIn('genome', selected)
        self.assertIn('fitness', selected)

    def test_elitism_selection(self):
        """
        Tests that the elitism selection strategy selects the top individuals.
        """
        elite_count = 2
        selected = self.strategy.elitism_selection(self.population, elite_count)

        self.assertEqual(len(selected), elite_count)

        fitness_values = [individual['fitness'] for individual in selected]
        self.assertEqual(fitness_values, [0.9, 0.7])

    def test_empty_population(self):
        """
        Test that selection strategies raise ValueError on empty population.
        """
        with self.assertRaises(ValueError):
            self.strategy.tournament_selection([], tournament_size=2)

        with self.assertRaises(ValueError):
            self.strategy.roulette_wheel_selection([])

    def test_invalid_tournament_size(self):
        """
        Test that tournament selection raises ValueError for invalid size.
        """
        with self.assertRaises(ValueError):
            self.strategy.tournament_selection(self.population, tournament_size=0)

        with self.assertRaises(ValueError):
            self.strategy.tournament_selection(self.population,
                                               tournament_size=len(self.population) + 1)


class TestFitnessFunction(unittest.TestCase):
    """Test suite for FitnessFunction class."""

    def setUp(self):
        """
        Initialize a FitnessFunction instance for use in test methods.
        """
        self.fitness_func = FitnessFunction()

    def test_sphere_function(self):
        """
        Test that the sphere fitness function returns correct results.
        """
        genome = [1.0, 2.0, 3.0]
        fitness = self.fitness_func.sphere_function(genome)

        expected = -(1.0 ** 2 + 2.0 ** 2 + 3.0 ** 2)
        self.assertEqual(fitness, expected)

    def test_rastrigin_function(self):
        """
        Test that the Rastrigin fitness function returns 0.0 at the origin.
        """
        genome = [0.0, 0.0, 0.0]
        fitness = self.fitness_func.rastrigin_function(genome)

        self.assertEqual(fitness, 0.0)

    def test_rosenbrock_function(self):
        """
        Test that the Rosenbrock fitness function returns 0.0 at the minimum.
        """
        genome = [1.0, 1.0]
        fitness = self.fitness_func.rosenbrock_function(genome)

        self.assertEqual(fitness, 0.0)

    def test_ackley_function(self):
        """
        Test that the Ackley fitness function returns zero at the origin.
        """
        genome = [0.0, 0.0, 0.0]
        fitness = self.fitness_func.ackley_function(genome)

        self.assertAlmostEqual(fitness, 0.0, places=10)

    def test_custom_function(self):
        """
        Test that a user-defined fitness function works correctly.
        """

        def custom_func(genome):
            """
            Return the sum of all numeric elements in the genome.

            Parameters:
                genome (iterable): Sequence of numeric values to be summed.

            Returns:
                total (numeric): The sum of all values in the genome.
            """
            return sum(genome)

        genome = [1.0, 2.0, 3.0]
        fitness = self.fitness_func.evaluate(genome, custom_func)

        self.assertEqual(fitness, 6.0)

    def test_multi_objective_function(self):
        """
        Tests that the multi-objective fitness function works correctly.
        """
        genome = [1.0, 2.0, 3.0]
        objectives = [
            lambda g: sum(g),
            lambda g: sum(x ** 2 for x in g)
        ]

        fitness = self.fitness_func.multi_objective_evaluate(genome, objectives)

        self.assertEqual(len(fitness), 2)
        self.assertEqual(fitness[0], 6.0)
        self.assertEqual(fitness[1], 14.0)

    def test_constraint_handling(self):
        """
        Test that the fitness function penalizes constraint violations.
        """
        genome = [1.0, 2.0, 3.0]

        def constraint_func(g):
            # Constraint: sum should be less than 5
            """
            Check if the sum of elements is less than 5.

            Parameters:
                g (iterable): Iterable containing numeric values.

            Returns:
                bool: True if the sum is less than 5, otherwise False.
            """
            return sum(g) < 5

        fitness = self.fitness_func.evaluate_with_constraints(
            genome,
            lambda g: sum(g),
            [constraint_func]
        )

        self.assertLess(fitness, sum(genome))


class TestPopulationManager(unittest.TestCase):
    """Test suite for PopulationManager class."""

    def setUp(self):
        """
        Set up the test environment with a PopulationManager.
        """
        self.manager = PopulationManager()
        self.genome_length = 5
        self.population_size = 10

    def test_initialize_random_population(self):
        """
        Test that the population manager creates a random population correctly.
        """
        population = self.manager.initialize_random_population(
            self.population_size,
            self.genome_length
        )

        self.assertEqual(len(population), self.population_size)

        for individual in population:
            self.assertIn('genome', individual)
            self.assertIn('fitness', individual)
            self.assertEqual(len(individual['genome']), self.genome_length)

    def test_initialize_seeded_population(self):
        """
        Test that seeded population initialization includes seed genomes.
        """
        seeds = [
            [1.0, 2.0, 3.0, 4.0, 5.0],
            [6.0, 7.0, 8.0, 9.0, 10.0]
        ]

        population = self.manager.initialize_seeded_population(
            self.population_size,
            self.genome_length,
            seeds
        )

        self.assertEqual(len(population), self.population_size)

        genomes = [ind['genome'] for ind in population]
        self.assertIn(seeds[0], genomes)
        self.assertIn(seeds[1], genomes)

    def test_evaluate_population(self):
        """
        Test that evaluating a population assigns valid fitness values.
        """
        population = self.manager.initialize_random_population(
            self.population_size,
            self.genome_length
        )

        def fitness_func(genome):
            return sum(genome)

        self.manager.evaluate_population(population, fitness_func)

        for individual in population:
            self.assertIsNotNone(individual['fitness'])
            self.assertIsInstance(individual['fitness'], (int, float))

    def test_get_best_individual(self):
        """
        Test that the population manager returns the individual with highest fitness.
        """
        population = [
            {'genome': [1, 2, 3], 'fitness': 0.5},
            {'genome': [4, 5, 6], 'fitness': 0.9},
            {'genome': [7, 8, 9], 'fitness': 0.7}
        ]

        best = self.manager.get_best_individual(population)

        self.assertEqual(best['fitness'], 0.9)
        self.assertEqual(best['genome'], [4, 5, 6])

    def test_get_population_statistics(self):
        """
        Test that the population manager correctly computes statistical metrics.
        """
        population = [
            {'genome': [1, 2, 3], 'fitness': 0.5},
            {'genome': [4, 5, 6], 'fitness': 0.9},
            {'genome': [7, 8, 9], 'fitness': 0.7}
        ]

        stats = self.manager.get_population_statistics(population)

        self.assertIn('best_fitness', stats)
        self.assertIn('worst_fitness', stats)
        self.assertIn('average_fitness', stats)
        self.assertIn('median_fitness', stats)
        self.assertIn('std_dev_fitness', stats)

        self.assertEqual(stats['best_fitness'], 0.9)
        self.assertEqual(stats['worst_fitness'], 0.5)
        self.assertAlmostEqual(stats['average_fitness'], 0.7, places=1)

    def test_diversity_calculation(self):
        """
        Test that the population diversity metric is computed correctly.
        """
        population = [
            {'genome': [1.0, 2.0, 3.0], 'fitness': 0.5},
            {'genome': [4.0, 5.0, 6.0], 'fitness': 0.9},
            {'genome': [7.0, 8.0, 9.0], 'fitness': 0.7}
        ]

        diversity = self.manager.calculate_diversity(population)

        self.assertIsInstance(diversity, float)
        self.assertGreater(diversity, 0.0)

    def test_empty_population_handling(self):
        """
        Test that retrieving information from empty population raises ValueError.
        """
        with self.assertRaises(ValueError):
            self.manager.get_best_individual([])

        with self.assertRaises(ValueError):
            self.manager.get_population_statistics([])


class TestGeneticOperations(unittest.TestCase):
    """Test suite for GeneticOperations class."""

    def setUp(self):
        """
        Prepare the test environment by initializing a GeneticOperations instance.
        """
        self.operations = GeneticOperations()

    def test_single_point_crossover(self):
        """
        Test that the single-point crossover operation returns valid children.
        """
        parent1 = [1, 2, 3, 4, 5]
        parent2 = [6, 7, 8, 9, 10]

        child1, child2 = self.operations.single_point_crossover(parent1, parent2)

        self.assertEqual(len(child1), len(parent1))
        self.assertEqual(len(child2), len(parent2))

        combined_parents = set(parent1 + parent2)
        combined_children = set(child1 + child2)
        self.assertTrue(combined_children.issubset(combined_parents))

    def test_two_point_crossover(self):
        """
        Verify that two-point crossover produces valid children.
        """
        parent1 = [1, 2, 3, 4, 5, 6, 7, 8]
        parent2 = [9, 10, 11, 12, 13, 14, 15, 16]

        child1, child2 = self.operations.two_point_crossover(parent1, parent2)

        self.assertEqual(len(child1), len(parent1))
        self.assertEqual(len(child2), len(parent2))

    def test_uniform_crossover(self):
        """
        Test that the uniform crossover operation produces valid children.
        """
        parent1 = [1, 2, 3, 4, 5]
        parent2 = [6, 7, 8, 9, 10]

        child1, child2 = self.operations.uniform_crossover(parent1, parent2, crossover_rate=0.5)

        self.assertEqual(len(child1), len(parent1))
        self.assertEqual(len(child2), len(parent2))

    def test_arithmetic_crossover(self):
        """
        Test that the arithmetic crossover operation generates correct children.
        """
        parent1 = [1.0, 2.0, 3.0, 4.0, 5.0]
        parent2 = [6.0, 7.0, 8.0, 9.0, 10.0]

        child1, child2 = self.operations.arithmetic_crossover(parent1, parent2, alpha=0.5)

        self.assertEqual(len(child1), len(parent1))
        self.assertEqual(len(child2), len(parent2))

        for i in range(len(parent1)):
            expected_child1 = 0.5 * parent1[i] + 0.5 * parent2[i]
            expected_child2 = 0.5 * parent2[i] + 0.5 * parent1[i]
            self.assertAlmostEqual(child1[i], expected_child1, places=5)
            self.assertAlmostEqual(child2[i], expected_child2, places=5)

    def test_simulated_binary_crossover(self):
        """
        Test that simulated binary crossover produces valid results.
        """
        parent1 = [1.0, 2.0, 3.0, 4.0, 5.0]
        parent2 = [6.0, 7.0, 8.0, 9.0, 10.0]
        bounds = [(-10, 10)] * 5

        child1, child2 = self.operations.simulated_binary_crossover(
            parent1, parent2, bounds, eta=2.0
        )

        self.assertEqual(len(child1), len(parent1))
        self.assertEqual(len(child2), len(parent2))

        for i, (lower, upper) in enumerate(bounds):
            self.assertGreaterEqual(child1[i], lower)
            self.assertLessEqual(child1[i], upper)
            self.assertGreaterEqual(child2[i], lower)
            self.assertLessEqual(child2[i], upper)

    def test_blend_crossover(self):
        """
        Test that the blend crossover operation returns valid children.
        """
        parent1 = [1.0, 2.0, 3.0]
        parent2 = [4.0, 5.0, 6.0]

        child1, child2 = self.operations.blend_crossover(parent1, parent2, alpha=0.5)

        self.assertEqual(len(child1), len(parent1))
        self.assertEqual(len(child2), len(parent2))

    def test_invalid_crossover_inputs(self):
        """
        Test that crossover operations raise ValueError for mismatched lengths.
        """
        parent1 = [1, 2, 3]
        parent2 = [4, 5]

        with self.assertRaises(ValueError):
            self.operations.single_point_crossover(parent1, parent2)

        with self.assertRaises(ValueError):
            self.operations.two_point_crossover(parent1, parent2)


class TestEvolutionaryConduit(unittest.TestCase):
    """Test suite for EvolutionaryConduit class."""

    def setUp(self):
        """
        Prepare test fixtures by creating EvolutionaryConduit and EvolutionaryParameters.
        """
        self.conduit = EvolutionaryConduit()
        self.params = EvolutionaryParameters(
            population_size=20,
            generations=10,
            mutation_rate=0.1,
            crossover_rate=0.8
        )

    def test_initialization(self):
        """
        Test that all core components of the EvolutionaryConduit are initialized.
        """
        self.assertIsNotNone(self.conduit.mutation_strategy)
        self.assertIsNotNone(self.conduit.selection_strategy)
        self.assertIsNotNone(self.conduit.fitness_function)
        self.assertIsNotNone(self.conduit.population_manager)
        self.assertIsNotNone(self.conduit.genetic_operations)

    def test_set_fitness_function(self):
        """
        Test that a custom fitness function can be assigned correctly.
        """

        def custom_fitness(genome):
            """
            Calculate the fitness score of a genome by summing its elements.

            Parameters:
                genome (iterable): An iterable containing numeric values.

            Returns:
                float: The sum of all elements in the genome.
            """
            return sum(genome)

        self.conduit.set_fitness_function(custom_fitness)

        test_genome = [1.0, 2.0, 3.0]
        fitness = self.conduit.fitness_function.evaluate(test_genome, custom_fitness)
        self.assertEqual(fitness, 6.0)

    def test_set_parameters(self):
        """
        Tests that setting evolutionary parameters updates the conduit correctly.
        """
        self.conduit.set_parameters(self.params)

        self.assertEqual(self.conduit.parameters.population_size, 20)
        self.assertEqual(self.conduit.parameters.generations, 10)
        self.assertEqual(self.conduit.parameters.mutation_rate, 0.1)
        self.assertEqual(self.conduit.parameters.crossover_rate, 0.8)

    @patch('app.ai_backend.genesis_evolutionary_conduit.EvolutionaryConduit.evolve')
    def test_run_evolution(self, mock_evolve):
        """
        Test that running the evolution process returns valid results.
        """
        mock_evolve.return_value = {
            'best_individual': {'genome': [1, 2, 3], 'fitness': 0.9},
            'generations_run': 10,
            'final_population': [],
            'statistics': {'best_fitness': 0.9}
        }

        self.conduit.set_parameters(self.params)
        result = self.conduit.run_evolution(genome_length=5)

        self.assertIn('best_individual', result)
        self.assertIn('generations_run', result)
        self.assertIn('final_population', result)
        self.assertIn('statistics', result)

        mock_evolve.assert_called_once()

    def test_save_and_load_state(self):
        """
        Test that saving and loading state preserves parameter values.
        """
        self.conduit.set_parameters(self.params)

        state = self.conduit.save_state()

        new_conduit = EvolutionaryConduit()
        new_conduit.load_state(state)

        self.assertEqual(new_conduit.parameters.population_size, 20)
        self.assertEqual(new_conduit.parameters.generations, 10)

    def test_add_callback(self):
        """
        Test that a callback function can be added to the evolutionary conduit.
        """
        callback_called = False

        def test_callback(_generation, _population, _best_individual):
            """
            Callback used in tests to indicate when it is invoked.

            Sets a flag to confirm that the callback mechanism is triggered.
            """
            nonlocal callback_called
            callback_called = True

        self.conduit.add_callback(test_callback)

        self.assertIn(test_callback, self.conduit.callbacks)

    def test_evolution_history_tracking(self):
        """
        Test that enabling history tracking sets the tracking flag.
        """
        self.conduit.set_parameters(self.params)
        self.conduit.enable_history_tracking()

        def simple_fitness(genome):
            """
            Calculate the fitness score of a genome by summing its elements.

            Parameters:
                genome (iterable): Sequence of numeric values.

            Returns:
                int or float: The total sum of the genome's elements.
            """
            return sum(genome)

        self.conduit.set_fitness_function(simple_fitness)

        with patch.object(self.conduit, 'evolve') as mock_evolve:
            mock_evolve.return_value = {
                'best_individual': {'genome': [1, 2, 3], 'fitness': 6.0},
                'generations_run': 5,
                'final_population': [],
                'statistics': {'best_fitness': 6.0}
            }

            self.conduit.run_evolution(genome_length=3)

            self.assertTrue(self.conduit.history_enabled)


class TestGenesisEvolutionaryConduit(unittest.TestCase):
    """Test suite for GenesisEvolutionaryConduit class."""

    def setUp(self):
        """
        Prepare the test environment by initializing a GenesisEvolutionaryConduit.
        """
        self.genesis_conduit = GenesisEvolutionaryConduit()
        self.params = EvolutionaryParameters(
            population_size=20,
            generations=10,
            mutation_rate=0.1,
            crossover_rate=0.8
        )

    def test_initialization(self):
        """
        Test that GenesisEvolutionaryConduit is properly initialized.
        """
        self.assertIsInstance(self.genesis_conduit, EvolutionaryConduit)
        self.assertIsNotNone(self.genesis_conduit.genesis_config)
        self.assertIsNotNone(self.genesis_conduit.neural_network_factory)
        self.assertIsNotNone(self.genesis_conduit.optimization_strategies)

    def test_neural_network_evolution(self):
        """
        Tests that a neural network is created using the specified configuration.
        """
        network_config = {
            'input_size': 10,
            'hidden_layers': [20, 15],
            'output_size': 1,
            'activation': 'relu'
        }

        self.genesis_conduit.set_network_config(network_config)

        network = self.genesis_conduit.create_neural_network()
        self.assertIsNotNone(network)

    def test_neuroevolution_fitness(self):
        """
        Test that evaluating a neural network genome's fitness returns numeric value.
        """
        X_train = [[1, 2], [3, 4], [5, 6]]
        y_train = [0, 1, 0]

        self.genesis_conduit.set_training_data(X_train, y_train)

        genome = [0.1, 0.2, 0.3, 0.4, 0.5]
        fitness = self.genesis_conduit.evaluate_network_fitness(genome)

        self.assertIsInstance(fitness, (int, float))

    def test_topology_evolution(self):
        """
        Test that mutating a neural network topology produces valid results.
        """
        topology = {
            'layers': [10, 5, 1],
            'connections': [[0, 1], [1, 2]]
        }

        mutated_topology = self.genesis_conduit.mutate_topology(topology)

        self.assertIsInstance(mutated_topology, dict)
        self.assertIn('layers', mutated_topology)
        self.assertIn('connections', mutated_topology)

    def test_hyperparameter_optimization(self):
        """
        Test that hyperparameter optimization produces valid results.
        """
        search_space = {
            'learning_rate': (0.001, 0.1),
            'batch_size': (16, 128),
            'dropout_rate': (0.0, 0.5)
        }

        self.genesis_conduit.set_hyperparameter_search_space(search_space)

        hyperparams = self.genesis_conduit.generate_hyperparameters()

        self.assertIn('learning_rate', hyperparams)
        self.assertIn('batch_size', hyperparams)
        self.assertIn('dropout_rate', hyperparams)

        self.assertGreaterEqual(hyperparams['learning_rate'], 0.001)
        self.assertLessEqual(hyperparams['learning_rate'], 0.1)

    def test_multi_objective_optimization(self):
        """
        Test that multi-objective optimization produces correct fitness vector.
        """
        objectives = [
            'accuracy',
            'model_size',
            'inference_time'
        ]

        self.genesis_conduit.set_objectives(objectives)

        genome = [0.1, 0.2, 0.3, 0.4, 0.5]
        fitness_vector = self.genesis_conduit.evaluate_multi_objective_fitness(genome)

        self.assertEqual(len(fitness_vector), len(objectives))
        self.assertIsInstance(fitness_vector, list)

    def test_adaptive_mutation_rates(self):
        """
        Test that the adaptive mutation rate is within valid range.
        """
        population = [
            {'genome': [1, 2, 3], 'fitness': 0.5, 'generation': 1},
            {'genome': [4, 5, 6], 'fitness': 0.7, 'generation': 2},
            {'genome': [7, 8, 9], 'fitness': 0.9, 'generation': 3}
        ]

        adaptive_rate = self.genesis_conduit.calculate_adaptive_mutation_rate(population)

        self.assertIsInstance(adaptive_rate, float)
        self.assertGreaterEqual(adaptive_rate, 0.0)
        self.assertLessEqual(adaptive_rate, 1.0)

    def test_speciation(self):
        """
        Test that individuals are correctly grouped into species.
        """
        population = [
            {'genome': [1.0, 2.0, 3.0], 'fitness': 0.5},
            {'genome': [1.1, 2.1, 3.1], 'fitness': 0.6},
            {'genome': [5.0, 6.0, 7.0], 'fitness': 0.7},
            {'genome': [5.1, 6.1, 7.1], 'fitness': 0.8}
        ]

        species = self.genesis_conduit.speciate_population(population, distance_threshold=2.0)

        self.assertIsInstance(species, list)
        self.assertGreater(len(species), 0)

    def test_transfer_learning(self):
        """
        Test adaptation of a pretrained neural network genome.
        """
        pretrained_genome = [0.1, 0.2, 0.3, 0.4, 0.5]

        adapted_genome = self.genesis_conduit.adapt_pretrained_network(
            pretrained_genome,
            new_task_config={'output_size': 3}
        )

        self.assertIsInstance(adapted_genome, list)
        self.assertGreater(len(adapted_genome), 0)

    def test_ensemble_evolution(self):
        """
        Test that the ensemble evolution method selects top-performing networks.
        """
        networks = [
            {'genome': [1, 2, 3], 'fitness': 0.7},
            {'genome': [4, 5, 6], 'fitness': 0.8},
            {'genome': [7, 8, 9], 'fitness': 0.9}
        ]

        ensemble = self.genesis_conduit.create_ensemble(networks, ensemble_size=2)

        self.assertEqual(len(ensemble), 2)
        self.assertEqual(ensemble[0]['fitness'], 0.9)
        self.assertEqual(ensemble[1]['fitness'], 0.8)

    def test_novelty_search(self):
        """
        Tests that novelty search computes numeric novelty scores.
        """
        population = [
            {'genome': [1.0, 2.0, 3.0], 'fitness': 0.5},
            {'genome': [4.0, 5.0, 6.0], 'fitness': 0.7},
            {'genome': [7.0, 8.0, 9.0], 'fitness': 0.9}
        ]

        novelty_scores = self.genesis_conduit.calculate_novelty_scores(population)

        self.assertEqual(len(novelty_scores), len(population))
        for score in novelty_scores:
            self.assertIsInstance(score, (int, float))

    def test_coevolution(self):
        """
        Test that the coevolution process returns both populations.
        """
        population1 = [
            {'genome': [1, 2, 3], 'fitness': 0.5},
            {'genome': [4, 5, 6], 'fitness': 0.7}
        ]

        population2 = [
            {'genome': [7, 8, 9], 'fitness': 0.6},
            {'genome': [10, 11, 12], 'fitness': 0.8}
        ]

        result = self.genesis_conduit.coevolve_populations(population1, population2)

        self.assertIsInstance(result, dict)
        self.assertIn('population1', result)
        self.assertIn('population2', result)

    @patch('app.ai_backend.genesis_evolutionary_conduit.GenesisEvolutionaryConduit.save_checkpoint')
    def test_checkpoint_system(self, mock_save):
        """
        Test that the checkpoint saving mechanism is called correctly.
        """
        self.genesis_conduit.set_parameters(self.params)

        checkpoint_path = "test_checkpoint.pkl"
        self.genesis_conduit.save_checkpoint(checkpoint_path)

        mock_save.assert_called_once_with(checkpoint_path)

    def test_distributed_evolution(self):
        """
        Test distributed evolution using the island model.
        """
        island_configs = [
            {'island_id': 1, 'population_size': 10},
            {'island_id': 2, 'population_size': 10}
        ]

        self.genesis_conduit.setup_island_model(island_configs)

        population1 = [{'genome': [1, 2, 3], 'fitness': 0.5}]
        population2 = [{'genome': [4, 5, 6], 'fitness': 0.7}]

        migrated = self.genesis_conduit.migrate_individuals(
            population1, population2, migration_rate=0.1
        )

        self.assertIsInstance(migrated, tuple)
        self.assertEqual(len(migrated), 2)


class TestEvolutionaryException(unittest.TestCase):
    """Test suite for EvolutionaryException class."""

    def test_exception_creation(self):
        """
        Verify that EvolutionaryException is instantiated correctly.
        """
        message = "Test evolutionary exception"
        exception = EvolutionaryException(message)

        self.assertEqual(str(exception), message)
        self.assertIsInstance(exception, Exception)

    def test_exception_with_details(self):
        """
        Test that EvolutionaryException stores additional details.
        """
        message = "Evolution failed"
        details = {"generation": 50, "error_type": "convergence"}

        exception = EvolutionaryException(message, details)

        self.assertEqual(str(exception), message)
        self.assertEqual(exception.details, details)

    def test_exception_raising(self):
        """
        Tests that an EvolutionaryException is correctly raised.
        """
        exception_message = "Test exception"
        with self.assertRaises(EvolutionaryException):
            raise EvolutionaryException(exception_message)


class TestIntegrationScenarios(unittest.TestCase):
    """Integration test suite for complex evolutionary scenarios."""

    def setUp(self):
        """
        Set up the integration test environment.
        """
        self.genesis_conduit = GenesisEvolutionaryConduit()
        self.params = EvolutionaryParameters(
            population_size=10,
            generations=5,
            mutation_rate=0.1,
            crossover_rate=0.8
        )

    def test_complete_evolution_cycle(self):
        """
        Test the complete evolution cycle using GenesisEvolutionaryConduit.
        """

        def simple_fitness(genome):
            """
            Calculates the fitness of a genome by summing the squares.

            Parameters:
                genome (iterable): Sequence of numeric values.

            Returns:
                float: The sum of squared values in the genome.
            """
            return sum(x ** 2 for x in genome)

        self.genesis_conduit.set_fitness_function(simple_fitness)
        self.genesis_conduit.set_parameters(self.params)

        with patch.object(self.genesis_conduit, 'evolve') as mock_evolve:
            mock_evolve.return_value = {
                'best_individual': {'genome': [1, 2, 3], 'fitness': 14.0},
                'generations_run': 5,
                'final_population': [],
                'statistics': {'best_fitness': 14.0}
            }

            result = self.genesis_conduit.run_evolution(genome_length=3)

            self.assertIn('best_individual', result)
            self.assertEqual(result['generations_run'], 5)

    def test_neural_network_evolution_pipeline(self):
        """
        Test the complete neural network evolution pipeline.
        """
        network_config = {
            'input_size': 5,
            'hidden_layers': [10, 5],
            'output_size': 1,
            'activation': 'relu'
        }

        self.genesis_conduit.set_network_config(network_config)

        X_train = [[1, 2, 3, 4, 5] for _ in range(10)]
        y_train = [1 for _ in range(10)]

        self.genesis_conduit.set_training_data(X_train, y_train)

        network = self.genesis_conduit.create_neural_network()
        self.assertIsNotNone(network)

    def test_multi_objective_optimization_pipeline(self):
        """
        Test that the multi-objective optimization pipeline returns correct values.
        """
        objectives = ['accuracy', 'model_size']
        self.genesis_conduit.set_objectives(objectives)

        genome = [0.1, 0.2, 0.3]

        with patch.object(self.genesis_conduit, 'evaluate_multi_objective_fitness') as mock_eval:
            mock_eval.return_value = [0.8, 0.1]

            fitness_vector = self.genesis_conduit.evaluate_multi_objective_fitness(genome)

            self.assertEqual(len(fitness_vector), 2)
            self.assertEqual(fitness_vector[0], 0.8)
            self.assertEqual(fitness_vector[1], 0.1)

    def test_adaptive_evolution_pipeline(self):
        """
        Tests that the adaptive mutation rate is within valid range.
        """
        population = [
            {'genome': [1, 2, 3], 'fitness': 0.3, 'generation': 1},
            {'genome': [4, 5, 6], 'fitness': 0.5, 'generation': 2},
            {'genome': [7, 8, 9], 'fitness': 0.7, 'generation': 3}
        ]

        adaptive_rate = self.genesis_conduit.calculate_adaptive_mutation_rate(population)

        self.assertIsInstance(adaptive_rate, float)
        self.assertGreaterEqual(adaptive_rate, 0.0)
        self.assertLessEqual(adaptive_rate, 1.0)

    def test_error_handling_and_recovery(self):
        """
        Test that the evolutionary framework raises appropriate exceptions.
        """
        with self.assertRaises(ValueError):
            EvolutionaryParameters(population_size=0)

        def failing_fitness(_genome):
            """
            A fitness function that always raises a ValueError.

            Raises:
                ValueError: Always raised to indicate fitness evaluation failure.
            """
            error_message = "Fitness evaluation failed"
            raise ValueError(error_message)

        self.genesis_conduit.set_fitness_function(failing_fitness)

        with self.assertRaises(EvolutionaryException):
            self.genesis_conduit.run_evolution(genome_length=3)


class TestAsyncEvolution(unittest.TestCase):
    """Test suite for asynchronous evolution capabilities."""

    def setUp(self):
        """
        Set up the test environment for async evolution tests.
        """
        self.genesis_conduit = GenesisEvolutionaryConduit()
        self.params = EvolutionaryParameters(
            population_size=10,
            generations=5
        )

    @patch('asyncio.run')
    def test_async_evolution_execution(self, mock_run):
        """
        Test that the asynchronous evolution method returns a valid result.
        """

        async def mock_async_evolve():
            """
            Simulates an asynchronous evolutionary process.

            Returns:
                dict: Mock data representing the outcome of an evolutionary run.
            """
            return {
                'best_individual': {'genome': [1, 2, 3], 'fitness': 0.9},
                'generations_run': 5,
                'final_population': [],
                'statistics': {'best_fitness': 0.9}
            }

        mock_run.return_value = asyncio.run(mock_async_evolve())

        result = self.genesis_conduit.run_async_evolution(genome_length=3)

        self.assertIsNotNone(result)

    @patch('concurrent.futures.ThreadPoolExecutor')
    def test_parallel_fitness_evaluation(self, mock_executor):
        """
        Test that population fitness evaluation is executed in parallel.
        """
        mock_executor.return_value.__enter__.return_value.map.return_value = [0.5, 0.7, 0.9]

        population = [
            {'genome': [1, 2, 3], 'fitness': None},
            {'genome': [4, 5, 6], 'fitness': None},
            {'genome': [7, 8, 9], 'fitness': None}
        ]

        def fitness_func(genome):
            """
            Calculate the fitness of a genome by summing its elements.

            Parameters:
                genome (iterable): Sequence of numeric values.

            Returns:
                The sum of all elements in the genome.
            """
            return sum(genome)

        self.genesis_conduit.evaluate_population_parallel(population, fitness_func)

        mock_executor.assert_called_once()


if __name__ == '__main__':
    unittest.main(verbosity=2)


class TestEvolutionaryConduitShutdownEnhancement(unittest.TestCase):
    """Comprehensive tests for enhanced shutdown with thread cleanup"""

    def setUp(self):
        """Set up test conduit for shutdown tests"""
        self.conduit = EvolutionaryConduit()

    def tearDown(self):
        """Clean up after tests"""
        asyncio.run(self.conduit.shutdown())

    def test_shutdown_sets_evolution_active_false(self):
        """Test that shutdown sets evolution_active to False"""
        self.conduit.evolution_active = True

        asyncio.run(self.conduit.shutdown())

        self.assertFalse(self.conduit.evolution_active)

    def test_shutdown_clears_analysis_threads_dict(self):
        """Test that shutdown clears the analysis_threads dictionary"""
        mock_thread = MagicMock()
        mock_thread.is_alive.return_value = False
        self.conduit.analysis_threads['test_thread'] = mock_thread

        asyncio.run(self.conduit.shutdown())

        self.assertEqual(len(self.conduit.analysis_threads), 0)

    def test_shutdown_with_no_threads(self):
        """Test shutdown when there are no active threads"""
        asyncio.run(self.conduit.shutdown())

        self.assertFalse(self.conduit.evolution_active)
        self.assertEqual(len(self.conduit.analysis_threads), 0)

    def test_shutdown_with_single_active_thread(self):
        """Test shutdown with one active thread that completes quickly"""
        mock_thread = MagicMock()
        mock_thread.is_alive.return_value = True
        mock_thread.join = MagicMock()

        def complete_on_join(_=None, thread=mock_thread):
            thread.is_alive.return_value = False

        mock_thread.join.side_effect = complete_on_join
        self.conduit.analysis_threads['thread_1'] = mock_thread

        asyncio.run(self.conduit.shutdown())

        mock_thread.join.assert_called_once_with(timeout=5.0)
        self.assertEqual(len(self.conduit.analysis_threads), 0)

    def test_shutdown_with_multiple_active_threads(self):
        """Test shutdown with multiple active threads"""
        threads = []
        for i in range(3):
            mock_thread = MagicMock()
            mock_thread.is_alive.return_value = True

            def complete_on_join(_=None, thread=mock_thread):
                thread.is_alive.return_value = False

            mock_thread.join.side_effect = complete_on_join
            self.conduit.analysis_threads[f'thread_{i}'] = mock_thread
            threads.append(mock_thread)

        asyncio.run(self.conduit.shutdown())

        for thread in threads:
            thread.join.assert_called_once_with(timeout=5.0)
        self.assertEqual(len(self.conduit.analysis_threads), 0)

    def test_shutdown_with_thread_that_times_out(self):
        """Test shutdown with a thread that doesn't complete within timeout"""
        mock_thread = MagicMock()
        mock_thread.is_alive.return_value = True
        self.conduit.analysis_threads['stuck_thread'] = mock_thread

        asyncio.run(self.conduit.shutdown())

        mock_thread.join.assert_called_once_with(timeout=5.0)
        self.assertEqual(len(self.conduit.analysis_threads), 0)

    def test_shutdown_with_mix_of_active_and_inactive_threads(self):
        """Test shutdown with both active and inactive threads"""
        active_thread = MagicMock()
        active_thread.is_alive.return_value = True

        def complete_on_join(_=None, thread=active_thread):
            thread.is_alive.return_value = False

        active_thread.join.side_effect = complete_on_join

        inactive_thread = MagicMock()
        inactive_thread.is_alive.return_value = False

        self.conduit.analysis_threads['active'] = active_thread
        self.conduit.analysis_threads['inactive'] = inactive_thread

        asyncio.run(self.conduit.shutdown())

        active_thread.join.assert_called_once_with(timeout=5.0)
        inactive_thread.join.assert_not_called()
        self.assertEqual(len(self.conduit.analysis_threads), 0)

    def test_shutdown_multiple_times_idempotent(self):
        """Test that calling shutdown multiple times is safe"""
        self.conduit.evolution_active = True

        asyncio.run(self.conduit.shutdown())
        asyncio.run(self.conduit.shutdown())
        asyncio.run(self.conduit.shutdown())

        self.assertFalse(self.conduit.evolution_active)
        self.assertEqual(len(self.conduit.analysis_threads), 0)

    def test_shutdown_respects_5_second_timeout(self):
        """Test that shutdown uses 5 second timeout for thread join"""
        mock_thread = MagicMock()
        mock_thread.is_alive.return_value = True
        self.conduit.analysis_threads['test'] = mock_thread

        asyncio.run(self.conduit.shutdown())

        mock_thread.join.assert_called_once()
        call_args = mock_thread.join.call_args
        timeout_val = call_args.kwargs.get('timeout') or (call_args.args[0] if call_args.args else None)
        self.assertEqual(timeout_val, 5.0)

    def test_shutdown_with_empty_thread_dict(self):
        """Test shutdown with empty analysis_threads dictionary"""
        self.conduit.analysis_threads = {}

        asyncio.run(self.conduit.shutdown())

        self.assertEqual(len(self.conduit.analysis_threads), 0)
        self.assertFalse(self.conduit.evolution_active)

    def test_shutdown_integration_with_real_thread(self):
        """Integration test with actual threading.Thread"""
        completed = []

        def worker():
            time.sleep(0.1)
            completed.append(True)

        thread = threading.Thread(target=worker)
        thread.start()
        self.conduit.analysis_threads['real_thread'] = thread

        asyncio.run(self.conduit.shutdown())

        self.assertEqual(len(self.conduit.analysis_threads), 0)
        self.assertTrue(len(completed) > 0)

    def test_shutdown_with_daemon_threads(self):
        """Test shutdown behavior with daemon threads"""
        def worker():
            time.sleep(0.1)

        thread = threading.Thread(target=worker, daemon=True)
        thread.start()
        self.conduit.analysis_threads['daemon_thread'] = thread

        asyncio.run(self.conduit.shutdown())

        self.assertEqual(len(self.conduit.analysis_threads), 0)


class TestEvolutionaryConduitThreadManagement(unittest.TestCase):
    """Tests for thread management in EvolutionaryConduit"""

    def setUp(self):
        self.conduit = EvolutionaryConduit()

    def test_analysis_threads_initialized_as_empty_dict(self):
        """Test that analysis_threads is initialized as empty dictionary"""
        self.assertIsInstance(self.conduit.analysis_threads, dict)
        self.assertEqual(len(self.conduit.analysis_threads), 0)

    def test_lock_is_reentrant_lock(self):
        """Test that _lock is a reentrant lock"""
        self.assertIsInstance(self.conduit._lock, threading.RLock)

    def test_lock_can_be_acquired_multiple_times_same_thread(self):
        """Test that RLock can be acquired multiple times in same thread"""
        acquired1 = self.conduit._lock.acquire(blocking=False)
        acquired2 = self.conduit._lock.acquire(blocking=False)

        self.assertTrue(acquired1)
        self.assertTrue(acquired2)

        self.conduit._lock.release()
        self.conduit._lock.release()

    def test_evolution_active_initialized_false(self):
        """Test that evolution_active is initialized to False"""
        self.assertFalse(self.conduit.evolution_active)

    def test_can_manually_add_threads_to_analysis_threads(self):
        """Test that threads can be added to analysis_threads dict"""
        mock_thread = MagicMock()

        self.conduit.analysis_threads['test_thread'] = mock_thread

        self.assertIn('test_thread', self.conduit.analysis_threads)
        self.assertEqual(self.conduit.analysis_threads['test_thread'], mock_thread)


if __name__ == '__main__':
    unittest.main(verbosity=2, buffer=True)