package KeyValue;

import java.io.Serializable;

public interface KeyValueInterface<K, V>  extends Serializable
{
	//getters
	public K getKey();
	public V getValue();
	
	//setters
	public void setValue(V value);
}
