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

	//===================================================================================
	//===================================================================================
	//====================				  2 Processors 		     ========================
	//==================================Series Non Visual================================
	//===================================================================================
	
	@Test
	public void test_Nodes_11_2_Proccessors_Serial() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new TaskGraph("src/test/resources/Nodes_11_OutTree.dot", 2));
		assertEquals(350, solution.getTotalTime());

	} 
	
	@Test
	public void test_Nodes_10_2_Proccessors_Serial() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new TaskGraph("src/test/resources/Nodes_10_Random.dot", 2));
		assertEquals(50, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_9_2_Proccessors_Serial() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new TaskGraph("src/test/resources/Nodes_9_SeriesParallel.dot", 2));
		assertEquals(55, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_8_2_Proccessors_Serial() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new TaskGraph("src/test/resources/Nodes_8_Random.dot", 2));
		assertEquals(581, solution.getTotalTime());

	} 
	
	@Test
	public void test_Nodes_7_2_Proccessors_Serial() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new TaskGraph("src/test/resources/Nodes_7_OutTree.dot", 2));
		assertEquals(28, solution.getTotalTime());
	} 
	
	@Test
	public void test_forkjoin9_2_Proccessors_Serial() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new TaskGraph("src/test/resources/forkjoin9.dot", 2));
		assertEquals(23, solution.getTotalTime());
	} 
	
	//===================================================================================
	//===================================================================================
	//====================				  4 Processors 		     ========================
	//==================================Series Non Visual================================
	//===================================================================================
	
	@Test
	public void test_Nodes_11_4_Proccessors_Serial() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new TaskGraph("src/test/resources/Nodes_11_OutTree.dot", 4));
		assertEquals(227, solution.getTotalTime());
	}
	
	@Test
	public void test_Nodes_10_4_Proccessors_Serial() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new TaskGraph("src/test/resources/Nodes_10_Random.dot", 4));
		assertEquals(50, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_9_4_Proccessors_Serial() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new TaskGraph("src/test/resources/Nodes_9_SeriesParallel.dot", 4));
		assertEquals(55, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_8_4_Proccessors_Serial() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new TaskGraph("src/test/resources/Nodes_8_Random.dot", 4));
		assertEquals(581, solution.getTotalTime());
	}
	
	@Test
	public void test_Nodes_7_4_Proccessors_Serial() throws IOException {
		Schedule solution = new BranchAndBoundAlgorithm().schedule(new TaskGraph("src/test/resources/Nodes_7_OutTree.dot", 4));
		assertEquals(22, solution.getTotalTime());
	} 
	
	//===================================================================================
	//===================================================================================
	//====================				  2 Processors 		     ========================
	//=================================2 Parallel Non Visual=============================
	//===================================================================================
	
	
	@Test
	public void test_Nodes_11_2_Proccessors_Parallel_2Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(2).schedule(new TaskGraph("src/test/resources/Nodes_11_OutTree.dot", 2));
		assertEquals(350, solution.getTotalTime());

	} 
	
	@Test
	public void test_Nodes_10_2_Proccessors_Parallel_2Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(2).schedule(new TaskGraph("src/test/resources/Nodes_10_Random.dot", 2));
		assertEquals(50, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_9_2_Proccessors_Parallel_2Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(2).schedule(new TaskGraph("src/test/resources/Nodes_9_SeriesParallel.dot", 2));
		assertEquals(55, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_8_2_Proccessors_Parallel_2Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(2).schedule(new TaskGraph("src/test/resources/Nodes_8_Random.dot", 2));
		assertEquals(581, solution.getTotalTime());

	} 
	
	@Test
	public void test_Nodes_7_2_Proccessors_Parallel_2Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(2).schedule(new TaskGraph("src/test/resources/Nodes_7_OutTree.dot", 2));
		assertEquals(28, solution.getTotalTime());
	} 
	
	@Test
	public void test_forkjoin9_2_Proccessors_Parallel_2Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(2).schedule(new TaskGraph("src/test/resources/forkjoin_9middle.dot", 2));
		assertEquals(23, solution.getTotalTime());
	} 
	
	//===================================================================================
	//===================================================================================
	//====================				  4 Processors 		     ========================
	//=================================2 Parallel Non Visual=============================
	//===================================================================================
	
	@Test
	public void test_Nodes_11_4_Proccessors_Parallel_2Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(2).schedule(new TaskGraph("src/test/resources/Nodes_11_OutTree.dot", 4));
		assertEquals(227, solution.getTotalTime());
	}
	
	@Test
	public void test_Nodes_10_4_Proccessors_Parallel_2Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(2).schedule(new TaskGraph("src/test/resources/Nodes_10_Random.dot", 4));
		assertEquals(50, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_9_4_Proccessors_Parallel_2Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(2).schedule(new TaskGraph("src/test/resources/Nodes_9_SeriesParallel.dot", 4));
		assertEquals(55, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_8_4_Proccessors_Parallel_2Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(2).schedule(new TaskGraph("src/test/resources/Nodes_8_Random.dot", 4));
		assertEquals(581, solution.getTotalTime());
	}
	
	@Test
	public void test_Nodes_7_4_Proccessors_Parallel_2Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(2).schedule(new TaskGraph("src/test/resources/Nodes_7_OutTree.dot", 4));
		assertEquals(22, solution.getTotalTime());
	} 
	

	//===================================================================================
	//===================================================================================
	//====================				  2 Processors 		     ========================
	//=================================4 Parallel Non Visual=============================
	//===================================================================================
	
	
	@Test
	public void test_Nodes_11_2_Proccessors_Parallel_4Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(4).schedule(new TaskGraph("src/test/resources/Nodes_11_OutTree.dot", 2));
		assertEquals(350, solution.getTotalTime());

	} 
	
	@Test
	public void test_Nodes_10_2_Proccessors_Parallel_4Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(4).schedule(new TaskGraph("src/test/resources/Nodes_10_Random.dot", 2));
		assertEquals(50, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_9_2_Proccessors_Parallel_4Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(4).schedule(new TaskGraph("src/test/resources/Nodes_9_SeriesParallel.dot", 2));
		assertEquals(55, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_8_2_Proccessors_Parallel_4Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(4).schedule(new TaskGraph("src/test/resources/Nodes_8_Random.dot", 2));
		assertEquals(581, solution.getTotalTime());

	} 
	
	@Test
	public void test_Nodes_7_2_Proccessors_Parallel_4Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(4).schedule(new TaskGraph("src/test/resources/Nodes_7_OutTree.dot", 2));
		assertEquals(28, solution.getTotalTime());
	} 
	
	@Test
	public void test_forkjoin9_2_Proccessors_Parallel_4Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(4).schedule(new TaskGraph("src/test/resources/forkjoin_9middle.dot", 2));
		assertEquals(23, solution.getTotalTime());
	} 
	
	//===================================================================================
	//===================================================================================
	//====================				  4 Processors 		     ========================
	//=================================4 Parallel Non Visual=============================
	//===================================================================================
	
	@Test
	public void test_Nodes_11_4_Proccessors_Parallel_4Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(4).schedule(new TaskGraph("src/test/resources/Nodes_11_OutTree.dot", 4));
		assertEquals(227, solution.getTotalTime());
	}
	
	@Test
	public void test_Nodes_10_4_Proccessors_Parallel_4Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(4).schedule(new TaskGraph("src/test/resources/Nodes_10_Random.dot", 4));
		assertEquals(50, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_9_4_Proccessors_Parallel_4Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(4).schedule(new TaskGraph("src/test/resources/Nodes_9_SeriesParallel.dot", 4));
		assertEquals(55, solution.getTotalTime());
	} 
	
	@Test
	public void test_Nodes_8_4_Proccessors_Parallel_4Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(4).schedule(new TaskGraph("src/test/resources/Nodes_8_Random.dot", 4));
		assertEquals(581, solution.getTotalTime());
	}
	
	@Test
	public void test_Nodes_7_4_Proccessors_Parallel_4Core() throws IOException {
		Schedule solution = new ParallelBranchAndBound(4).schedule(new TaskGraph("src/test/resources/Nodes_7_OutTree.dot", 4));
		assertEquals(22, solution.getTotalTime());
	} 
	
	
}
