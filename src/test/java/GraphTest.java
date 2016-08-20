import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GraphTest {
	/*
	 * Create test timer to measure runtime of each test
	 * Reference: http://stackoverflow.com/questions/17552779/record-time-it-takes-junit-tests-to-run
	 */
	@Rule
	public TestRule timer = new TestWatcher() {
		long startTime;
		@Override
		protected void starting(Description description) {
			//record the start time
			startTime=System.currentTimeMillis();
		}

		@Override
		protected void finished(Description description) {
			long runTime=System.currentTimeMillis()-startTime;
			//print the finish time
			System.out.println(description);
			System.out.println("Runtime: "+runTime+"ms");
			System.out.println("==================================================");
		}
	};

	@Test
	public void test_Nodes_11_2_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/test/resources/Nodes_11_OutTree.dot", 2));
		assertEquals(350, solution.getTotalTime());

	} 
	
	@Test
	public void test_Nodes_10_2_Proccessors() throws IOException {
		Schedule solution = new ParallelBranchAndBound(2).schedule(new Graph("src/test/resources/Nodes_10_Random.dot", 2));
		assertEquals(50, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_9_2_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/test/resources/Nodes_9_SeriesParallel.dot", 2));
		assertEquals(55, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_8_2_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/test/resources/Nodes_8_Random.dot", 2));
		assertEquals(581, solution.getTotalTime());

	} 
	
	@Test
	public void test_Nodes_7_2_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/test/resources/Nodes_7_OutTree.dot", 2));
		assertEquals(28, solution.getTotalTime());
	} 
	
	//===================================================================================
	//===================================================================================
	//====================				  4 Processors 		     ========================
	//===================================================================================
	//===================================================================================
	
	@Test
	public void test_Nodes_10_4_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/test/resources/Nodes_10_Random.dot", 4));
		assertEquals(50, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_11_4_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/test/resources/Nodes_11_OutTree.dot", 4));
		assertEquals(227, solution.getTotalTime());
	}
	
	@Test
	public void test_Nodes_9_4_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/test/resources/Nodes_9_SeriesParallel.dot", 4));
		assertEquals(55, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_8_4_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/test/resources/Nodes_8_Random.dot", 4));
		assertEquals(581, solution.getTotalTime());
	}
	
	@Test
	public void test_Nodes_7_4_Proccessors() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new Graph("src/test/resources/Nodes_7_OutTree.dot", 4));
		assertEquals(22, solution.getTotalTime());
	} 
	
}
