package Fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import KeyValue.KeyValueClass;
import KeyValue.KeyValueInterface;
import Partitioner.APartitioner;
import Partitioner.Partitioner;
import ProcessIdentifiers.ProcessIdentifier;
import Registry.RMIRegistry;
import Results.Result;
import sun.security.util.Length;

public class SingleProcessWorker {

	

	private Map<String, Integer> partialMap;
	
	
	private int alphabetSize;
	
	
	
	public SingleProcessWorker( int newAlphabetSize) {
		
		alphabetSize = newAlphabetSize;
		partialMap = new HashMap<String, Integer>();
		
	}



	public void map(String filename) {
		long startTime = System.nanoTime();
		
		File file = new File(filename);

		long filesize = file.length(); // got size
		long chunkSize = filesize;
		long start = 0;
		String inputToken = "";

		try {
			InputStream is = new FileInputStream(file);
			is.skip(start);
			System.err.println("At position" + start);
			long initDistortion = 0;
			
			// start read'
			long bytesRead = 0;
			char curByte = 'a';// garbage value
			boolean readData = false;
			System.err.println("Starting Read");
			while ((bytesRead + start + initDistortion + 1 < filesize) && (bytesRead < chunkSize - initDistortion || curByte != ' ')) {
				curByte = (char) is.read();
				

				if(readData && curByte == ' ')
				{
					
					int value = 1; // iterating 1 at a time
					if (!partialMap.containsKey(inputToken)) {
						partialMap.put(inputToken, value);
					} else {
						partialMap.put(inputToken, partialMap.get(inputToken) + value);
					}
					
					readData = false;
					bytesRead++;
					inputToken = "";
					continue;
				}
				
				if(curByte != ' ')
				{
					readData = true;
					inputToken += curByte;
				}
				bytesRead++;
				
				
				
				//System.err.println("Read 1 byte = " + curByte);
			}
		} catch (Exception e) {
			System.err.println("Read/seek Error " + e);
		}

		
		long endTime = System.nanoTime();
		
		System.err.println("Total time taken in S  ::  " + ((double)(endTime-startTime))/(double)(1000000000.0));

		for (Map.Entry<String, Integer> entry : partialMap.entrySet()) {

			String key = entry.getKey();
			Integer value = entry.getValue();
			KeyValueInterface<String, Integer> kvPair = new KeyValueClass<String, Integer>(key, value);

			System.err.println("Key ::" + key + " \t  Value ::" + value);
		}
	}



	public static void main(String[] args) 
	{
		int newAlphabetSize = RMIRegistry.alphabetSize;
		String filename = args[0];
		SingleProcessWorker fragment = new SingleProcessWorker(newAlphabetSize);
		fragment.map(filename);
		
	}

}
