import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.IOException;

public class GraphTester {

	@Test
	public void test_Nodes_11_2_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/tests/Nodes_11_OutTree.dot", 2));
		assertEquals(350, solution.getTotalTime());

	} 
	
	@Test
	public void test_Nodes_10_2_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/tests/Nodes_10_Random.dot", 2));
		assertEquals(50, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_9_2_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/tests/Nodes_9_SeriesParallel.dot", 2));
		assertEquals(55, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_8_2_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/tests/Nodes_8_Random.dot", 2));
		assertEquals(581, solution.getTotalTime());

	} 
	
	@Test
	public void test_Nodes_7_2_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/tests/Nodes_7_OutTree.dot", 2));
		assertEquals(28, solution.getTotalTime());
	} 
	
	//===================================================================================
	//===================================================================================
	//====================				  4 Processors 		     ========================
	//===================================================================================
	//===================================================================================
	
	@Test
	public void test_Nodes_10_4_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/tests/Nodes_10_Random.dot", 4));
		assertEquals(50, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_11_4_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/tests/Nodes_11_OutTree.dot", 4));
		assertEquals(227, solution.getTotalTime());
	}
	
	@Test
	public void test_Nodes_9_4_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/tests/Nodes_9_SeriesParallel.dot", 4));
		assertEquals(55, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_8_4_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/tests/Nodes_8_Random.dot", 4));
		assertEquals(581, solution.getTotalTime());
	}
	
	@Test
	public void test_Nodes_7_4_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/tests/Nodes_7_OutTree.dot", 4));
		assertEquals(22, solution.getTotalTime());
	} 
	
}
