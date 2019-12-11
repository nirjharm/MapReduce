package Partitioner;

public interface Partitioner <K> {
	public int getPartition(K key, int numberOfPartitions);
}
