import static org.junit.Assert.*;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;
import org.junit.Test;


public class GraphTester {

	@Test
	public void test_Nodes_11_2_Proccessors(){
		Graph.cleanInstance();
		String[] args = {"src/tests/Nodes_11_OutTree.dot", "2"};
		assertEquals(350, Main.main(args));
	} 
	
	@Test
	public void test_Nodes_10_2_Proccessors(){
		Graph.cleanInstance();
		String[] args = {"src/tests/Nodes_10_Random.dot", "2"};
		assertEquals(50, Main.main(args));
	} 
	
	@Test
	public void test_Nodes_9_2_Proccessors(){
		Graph.cleanInstance();
		String[] args = {"src/tests/Nodes_9_SeriesParallel.dot", "2"};
		assertEquals(55, Main.main(args));
	} 
	
	@Test
	public void test_Nodes_8_2_Proccessors(){
		Graph.cleanInstance();
		String[] args = {"src/tests/Nodes_8_Random.dot", "2"};
		assertEquals(581, Main.main(args));
	} 
	
	@Test
	public void test_Nodes_7_2_Proccessors(){
		Graph.cleanInstance();
		String[] args = {"src/tests/Nodes_7_OutTree.dot", "2"};
		assertEquals(28, Main.main(args));
	} 
	
	//===================================================================================
	//===================================================================================
	//====================				  4 Processors 		     ========================
	//===================================================================================
	//===================================================================================
	
	@Test
	public void test_Nodes_10_4_Proccessors(){
		Graph.cleanInstance();
		String[] args = {"src/tests/Nodes_10_Random.dot", "4"};
		assertEquals(50, Main.main(args));
	} 
	
	@Test
	public void test_Nodes_11_4_Proccessors(){
		Graph.cleanInstance();
		String[] args = {"src/tests/Nodes_11_OutTree.dot", "4"};
		assertEquals(227, Main.main(args));
	} 
	
	@Test
	public void test_Nodes_9_4_Proccessors(){
		Graph.cleanInstance();
		String[] args = {"src/tests/Nodes_9_SeriesParallel.dot", "4"};
		assertEquals(55, Main.main(args));
	} 
	
	@Test
	public void test_Nodes_8_4_Proccessors(){
		Graph.cleanInstance();
		String[] args = {"src/tests/Nodes_8_Random.dot", "4"};
		assertEquals(581, Main.main(args));
	} 
	
	@Test
	public void test_Nodes_7_4_Proccessors(){
		Graph.cleanInstance();
		String[] args = {"src/tests/Nodes_7_OutTree.dot", "4"};
		assertEquals(22, Main.main(args));
	} 
	
}
