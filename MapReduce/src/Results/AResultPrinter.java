package Results;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import KeyValue.KeyValueInterface;
import ProcessIdentifiers.ProcessIdentifier;
import Registry.RMIRegistry;

public class AResultPrinter implements Result {
	private Registry rmiRegistry;
	private BlockingQueue<KeyValueInterface<String, Integer>> finalQueue;
	ProcessIdentifier processIdentifier;
	
	
	
	public AResultPrinter(String rHost, int rPort, int newAlphabetSize) {
		// TODO Auto-generated constructor stub
		try
		{
			System.setProperty( "java.rmi.server.hostname", rHost );
			rmiRegistry = LocateRegistry.getRegistry(rHost, rPort);
		
			Result proxy = (Result) UnicastRemoteObject.exportObject(this, 9001);
			UnicastRemoteObject.exportObject(proxy, 0);
			rmiRegistry.rebind(RESULT, proxy);
			System.err.println("RMI server registration successful");
			processIdentifier = (ProcessIdentifier) rmiRegistry.lookup(ProcessIdentifier.PROCESSIDENTIFIERNAME);
			System.err.println("Got ProcessIdentifier");
		}
		catch(RemoteException e){
			System.err.println("RMI server registration failed");
			System.err.println(e);
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finalQueue = new ArrayBlockingQueue<KeyValueInterface<String,Integer>>(newAlphabetSize);
		
	}
	public int putIntoBuffer(BlockingQueue<KeyValueInterface<String, Integer>> queue)
	{
		//System.err.println("Length of Add " + queue.size());
	
		
		finalQueue.addAll(queue);
		return 0;
	}
	public void print()
	{
		System.err.println("Printing");
		
		try {
			
			
			
			while(!processIdentifier.canStartMap()) ;
			//Map started now record timer
			long startTime = System.nanoTime();
			
			while(!processIdentifier.isDoneCompilingResult()) ;
			//done end timer
			long endTime = System.nanoTime();
			System.err.println("Total time taken in S  ::  " + ((double)(endTime-startTime))/(double)(1000000000.0));
			
			for(KeyValueInterface<String, Integer> item: finalQueue)
			{
				System.err.println("Key ::" + item.getKey() + " \t  Value ::" + item.getValue());
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.err.println("Process Identifier Fetch failed");
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args)
	{
		String rHost = RMIRegistry.rHost;
		int rPort = RMIRegistry.rPort;
		int newAlphabetSize = RMIRegistry.alphabetSize;
		Result result = new AResultPrinter(rHost, rPort, newAlphabetSize);
		try {
			result.print();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
