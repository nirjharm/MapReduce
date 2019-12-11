package Mapper;


import KeyValue.KeyValueInterface;

public interface Mapper <K, V> {
	public KeyValueInterface<K, V> map(String inputString);
}