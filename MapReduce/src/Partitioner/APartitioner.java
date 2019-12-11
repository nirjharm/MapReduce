package Partitioner;

public class APartitioner implements Partitioner<String>{
	
	
	public int getPartition(String key, int numberOfPartitions)
	{
		Character firstChar = key.charAt(0);
		if(!Character.isLetter(firstChar))
		{
			return 0;
		}
		else
		{
			Character lowerFirstChar = Character.toLowerCase(firstChar);
			final float space = 26;
			float multiplier = space/(float)numberOfPartitions;
			return (int)((lowerFirstChar-'a')/multiplier)+1;//start from 1
		}
	}

}
