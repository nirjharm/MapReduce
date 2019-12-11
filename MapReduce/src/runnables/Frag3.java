package runnables;

import java.rmi.RemoteException;

import Fragments.AFragment;
import Fragments.Fragment;
import Registry.RMIRegistry;

public class Frag3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				String rHost = RMIRegistry.rHost;
				int rPort = RMIRegistry.rPort;
				int newAlphabetSize = RMIRegistry.alphabetSize;
				Fragment fragment = new AFragment(rHost, rPort, newAlphabetSize);
				try {
					fragment.map("file.txt");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

}
