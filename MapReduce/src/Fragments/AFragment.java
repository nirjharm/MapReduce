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

public class AFragment implements Fragment {

	private Registry rmiRegistry;
	private String FRAGMENTNAME;
	private int FRAGMENTNUMBER;
	private ProcessIdentifier processIdentifier;

	private Map<String, Integer> partialMap;
	private Map<String, Integer> finalMap;
	private BlockingQueue<KeyValueInterface<String, Integer>> finalPartialQueue;
	
	private int alphabetSize;
	private int numberOfProcesses; // = number of partitions = number of fragments
	
	public void addToFinalPartialQueue(KeyValueInterface<String, Integer> kvPair) {
		finalPartialQueue.add(kvPair);
	}

	public AFragment(String rHost, int rPort, int newAlphabetSize) {
		try {
			// get Registry
			System.setProperty( "java.rmi.server.hostname", rHost );
			rmiRegistry = LocateRegistry.getRegistry(rHost, rPort);

			// get Fragment Name
			processIdentifier = (ProcessIdentifier) rmiRegistry.lookup(ProcessIdentifier.PROCESSIDENTIFIERNAME);
			numberOfProcesses = processIdentifier.getNumberOfProcesses();

			int processNumber = processIdentifier.getIdentifier();
			if (processNumber > numberOfProcesses)
				try {
					throw new Exception("Can't join Map-Reduce Cluster, Would exceed max permitted cluster size");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Exiting Process");
					System.exit(0);
				}
			FRAGMENTNUMBER = processNumber;
			FRAGMENTNAME = Integer.toString(processNumber);

			// register Fragment as FragmentName
			Fragment proxy = (Fragment) UnicastRemoteObject.exportObject(this, 9001+FRAGMENTNUMBER);
			UnicastRemoteObject.exportObject(proxy, 0);
			rmiRegistry.rebind(FRAGMENTNAME, proxy);

			
			
			processIdentifier.doneRegistering();

			System.err.println("RMI server registration successful, Spin Waiting to start");
			while (!processIdentifier.canStartMap());
			
			
		} catch (RemoteException | NotBoundException e) {
			System.err.println("RMI server registration failed with exception");
			System.err.println(e);
		}
		alphabetSize = newAlphabetSize;
		partialMap = new HashMap<String, Integer>();
		finalPartialQueue = new ArrayBlockingQueue<KeyValueInterface<String, Integer>>(alphabetSize*numberOfProcesses);
		finalMap = new HashMap<String, Integer>();
	}

	public int getPartition(String key, int numberOfPartitions) {
		Character firstChar = key.charAt(0);
		if (!Character.isLetter(firstChar)) {
			return 0;
		} else {
			Character lowerFirstChar = Character.toLowerCase(firstChar);
			final float space = 26;
			float multiplier = space / (float) numberOfPartitions;
			return (int) ((lowerFirstChar - 'a') / multiplier) + 1;// start from 1
		}
	}

	public void map(String filename) {
		
		File file = new File(filename);

		long filesize = file.length(); // got size
		long chunkSize = filesize / numberOfProcesses;
		long start = (chunkSize) * (FRAGMENTNUMBER - 1);
		String inputToken = "";

		try {
			InputStream is = new FileInputStream(file);
			is.skip(start);
			System.err.println("At position" + start);
			long initDistortion = 0;
			if (FRAGMENTNUMBER != 1) {
				while (is.read() != (byte) ' ') {
					initDistortion++;
					//System.err.println("Skipped nonwhitespace");
				}
				//is.skip(-1);
			}
			/*
			long startloc = start+initDistortion;
			
			
			long endloc = filesize;
			
			if(chunkSize + start + 1 < filesize)
			{
				is.skip(chunkSize-initDistortion);
				endloc = chunkSize + start + 1;
				
				while (is.read() != (byte) ' ') {
					endloc++;
					if(endloc >= filesize)
					{
						endloc = filesize;
						break;
					}
				}
			}	
			*/
			
		
			
			
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

		/*String[] tokens = inputString.split(" ");

		// Map and initial partial reduction
		for (String token : tokens) {
			int value = 1; // iterating 1 at a time
			if (!partialMap.containsKey(token)) {
				partialMap.put(token, value);
			} else {
				partialMap.put(token, partialMap.get(token) + value);
			}
		}*/
		// send to correct process
		Partitioner<String> partitioner = new APartitioner();

		

		for (Map.Entry<String, Integer> entry : partialMap.entrySet()) {

			String key = entry.getKey();
			Integer value = entry.getValue();
			KeyValueInterface<String, Integer> kvPair = new KeyValueClass<String, Integer>(key, value);

			int processNumber = getPartition(key, numberOfProcesses);

			if (processNumber == FRAGMENTNUMBER) {
				addToFinalPartialQueue(kvPair);
				continue;
			}

			try {

				
				Fragment fragment;

				fragment = (Fragment) rmiRegistry.lookup(Integer.toString(processNumber));
				fragment.addToFinalPartialQueue(kvPair);

			//	System.err.println("Adding " + kvPair.getKey() + " to process Number " + processNumber);

			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				System.err.println("Failed to fetch process Number " + processNumber);
				e.printStackTrace();
			}

		}
		try {
			processIdentifier.doneMapping();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reduce();

	}

	void reduce() {
		try {
			while (!processIdentifier.canStartReduce())
				;
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.err.println("Starting Reduce of len " + finalPartialQueue.size());
		// final reduction
		try {
			while (!finalPartialQueue.isEmpty()) {
				KeyValueInterface<String, Integer> item;

				item = finalPartialQueue.take();

				if (!finalMap.containsKey(item.getKey())) {
					finalMap.put(item.getKey(), item.getValue());
				} else {
					finalMap.put(item.getKey(), finalMap.get(item.getKey()) + item.getValue());
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// send to correct process

		System.err.println("Final size " + finalMap.size());

		BlockingQueue<KeyValueInterface<String, Integer>> list = new ArrayBlockingQueue<KeyValueInterface<String, Integer>>(
				alphabetSize);

		for (Map.Entry<String, Integer> entry : finalMap.entrySet()) {

			String key = entry.getKey();
			Integer value = entry.getValue();
			KeyValueInterface<String, Integer> kvPair = new KeyValueClass<String, Integer>(key, value);

			list.add(kvPair);
		}

		System.err.println("list size " + list.size());

		try {
			Result result = (Result) rmiRegistry.lookup(Result.RESULT);
			int res = result.putIntoBuffer(list);
			System.err.println("Sent to Result with return value = " + res);
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to fetch result");
			e.printStackTrace();
		}

		try {
			processIdentifier.doneReducing();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			rmiRegistry.unbind(FRAGMENTNAME);
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);//exit
	}

	

}
