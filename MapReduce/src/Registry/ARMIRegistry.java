package Registry;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class ARMIRegistry implements RMIRegistry{
	
	
	
	public ARMIRegistry()
	{
		
		try {
			System.setProperty( "java.rmi.server.hostname", rHost );
			
			LocateRegistry.createRegistry(rPort);
			
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	public static void main(String[] args)
	{
		//rPort = Integer.parseInt(args[0]);
		//rHost = args[1];
		
		RMIRegistry r = new ARMIRegistry();
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
	}
}
