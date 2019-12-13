#include<iostream>
#include<fstream>
#include<stdlib.h>
#include<stdio.h>
#include<string.h>

using namespace std;





char * to_string(int a)
{
	int i = 0;
	char * x = new char[32];
	while(a> 0)	
	{
		char t = (a %25)+97;
		a /=10;
		x[i] = t; 
		i++;	
	}
	x[i] = '\0';
	return x; //number is reversed but we dont realy a=care about values here
}


int main()
{
	int size;
	cout << "enter size in KB :";
	cin >>size;
	cout << "adress with name and extension (like /a/b/c.txt)?";
	char *name = new char();
	cin >>name;
	int asize;
	cout <<"alphabetsize? ";
	cin >>asize;

	ofstream file;
	ifstream lang;
	
	file.open(name);
	lang.open("alpha.txt");	


	char ** memo = new char*[asize+1];
	for(int i = 0;i < asize+1;i++)
	{
		memo[i] = new char[32];
		lang >> memo[i];	
	}

	for(int i = 0, j = 0; i<size;)
	{
		if(j >= 1000)
		{
			i++; //+1 KB
			j -= 1000; //reset bytes
		}
		char * str;
		int number = 1+(rand()%asize);
		
		str = memo[number];
		
		file <<str <<	" ";		
		j += strlen(str)+1;
	}

}
