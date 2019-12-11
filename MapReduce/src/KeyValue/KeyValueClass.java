package KeyValue;

public class KeyValueClass<K, V> implements KeyValueInterface<K, V>
{
	private K key;
	private V value;
	
	public KeyValueClass(K newKey, V newValue) {
		setKey(newKey);
		setValue(newValue);
	}
	
	//getters
	public K getKey() {
		return key;
	}
	public V getValue() {
		return value;
	}
	
	//setters
	private void setKey(K key) {
		this.key = key;
	}
	public void setValue(V value) {
		this.value = value;
	}
	
	
	@Override
	public String toString()
	{
		String keyString = key==null?"null":getKey().toString();
		String valString = value==null?"null":getValue().toString();
		String retVal = "(" + keyString + "," + valString+")";
		return retVal;
	}
}
