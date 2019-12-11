package Results;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import KeyValue.KeyValueInterface;

public interface Result extends Remote{
	public int putIntoBuffer(BlockingQueue<KeyValueInterface<String, Integer>> queue) throws RemoteException;
	public void print() throws RemoteException;
	public final String RESULT = "RESULT";
}
