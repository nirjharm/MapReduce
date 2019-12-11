package Mapper;


import KeyValue.KeyValueClass;
import KeyValue.KeyValueInterface;

public class ATokenCountingMapper implements Mapper<String, Integer> {

	@Override
	public KeyValueInterface<String, Integer> map(String inputString) {
		KeyValueInterface<String, Integer> retVal = new KeyValueClass<String, Integer>(inputString, 1); 
		return retVal;
	}

}
