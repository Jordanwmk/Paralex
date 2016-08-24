public interface Algorithm  {
    public Schedule schedule(TaskGraph taskGraph);
    public Schedule getCurrentBest();
}
