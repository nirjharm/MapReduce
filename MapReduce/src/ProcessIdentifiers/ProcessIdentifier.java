package ProcessIdentifiers;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProcessIdentifier extends Remote{
	public int getIdentifier() throws RemoteException;	
	public void doneMapping() throws RemoteException;
	public void doneReducing() throws RemoteException;
	public void doneRegistering() throws RemoteException;
	public boolean canStartReduce() throws RemoteException;
	public boolean isDoneCompilingResult() throws RemoteException;
	public boolean canStartMap() throws RemoteException;
	public int getNumberOfProcesses() throws RemoteException;
	
	public final String PROCESSIDENTIFIERNAME = "ProcessIdentifier";
}
