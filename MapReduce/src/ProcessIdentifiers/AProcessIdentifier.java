package ProcessIdentifiers;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import Registry.RMIRegistry;

public class AProcessIdentifier implements ProcessIdentifier {

	
	private int number;
	private Registry rmiRegistry;
	private int doneMapping;
	private int doneReducing;
	private int doneRegistering;
	public int NumberOfProcesses = 3;
	
	public int getNumberOfProcesses()
	{
		return NumberOfProcesses;
	}
	
	public AProcessIdentifier(String rHost, int rPort, int nop)
	{
		NumberOfProcesses = nop;
		number = 1;
		doneMapping = 0;
		doneReducing = 0;
		doneRegistering = 0;
		try
		{
			rmiRegistry = LocateRegistry.getRegistry(rHost, rPort);
		
			ProcessIdentifier proxy = (ProcessIdentifier) UnicastRemoteObject.exportObject(this, 9000);
			UnicastRemoteObject.exportObject(proxy, 0);
			rmiRegistry.rebind(PROCESSIDENTIFIERNAME, proxy);
			System.err.println("RMI server registration successful");
		}
		catch(RemoteException e){
			System.err.println("RMI server registration failed");
			System.err.println(e);
		}
	}
	
	public synchronized int getIdentifier()
	{
		
		int temp = number;
		number++;
		return temp;
	}
	
	
	
	public synchronized void doneMapping()
	{
		doneMapping++;
	}
	
	public synchronized void doneReducing()
	{
		doneReducing++;
	}
	public synchronized void doneRegistering()
	{
		doneRegistering++;
	}
	
	public boolean canStartMap()
	{
		if(doneRegistering == NumberOfProcesses) return true;
		return false;
	}
	
	public boolean canStartReduce() {
		if(doneMapping == NumberOfProcesses)return true;
		return false;
	}
	
	public boolean isDoneCompilingResult() {
		System.err.println("done Reducing = " + doneReducing);
		if(doneReducing == NumberOfProcesses)return true;
		return false;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String rHost = RMIRegistry.rHost;
		int rPort = RMIRegistry.rPort;
		int nop = 3;
		if(args.length == 1)
			nop = Integer.parseInt(args[0]);
		ProcessIdentifier processIdentifier = new AProcessIdentifier(rHost, rPort, nop);
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
	}

}
