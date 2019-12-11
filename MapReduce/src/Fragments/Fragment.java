package Fragments;

import java.rmi.Remote;
import java.rmi.RemoteException;

import KeyValue.KeyValueInterface;

public interface Fragment extends Remote{
	public void addToFinalPartialQueue(KeyValueInterface<String, Integer> kvPair) throws RemoteException;
	public void map(String inputString) throws RemoteException;
}
