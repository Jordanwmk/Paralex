public interface Algorithm  {
	public boolean isDone();
    public Schedule schedule(TaskGraph taskGraph);
    public Schedule getCurrentBest();
}
