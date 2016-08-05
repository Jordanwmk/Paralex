

Graph graph;
public Estimator(){
	

}



estimate(Schedule schedule) {


int idleTime 
	Math.max(idleEstimator(schedule.getIdleTime()), bottomLevelEstimator(schedule.getMostRecent()))


}

idleEstimator(int idleTime) {
	return (idleTime+graph.getTotalTaskTime())/graph.getNumProcessors();
	
}

bottomeLevelEstimator(int node) {
	//most recent node
	return graph.getBottomLevel(node) + graph.getCurrentTime();
	
	
}