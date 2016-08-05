

Graph graph;
public Estimator(){	
	

}



pubic int estimate(Schedule schedule) {


int idleTime = Math.max(idleEstimator(schedule.getIdleTime()), bottomLevelEstimator(schedule.getMostRecent()));
return idleTime;

}

private int idleEstimator(int idleTime) {
	return (idleTime+graph.getTotalTaskTime())/graph.getNumProcessors();
	
}

private int bottomeLevelEstimator(int node) {
	//most recent node
	return graph.getBottomLevel(node) + graph.getCurrentTime();
	
	
}
