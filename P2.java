import java.io.*;
import java.util.Scanner;
import java.util.LinkedList;

/*  
    /============================\
    |  COMP2240 Assignment 2     | 
    |  Name : Ni Zeng            | 
    |  Student Number : c3238805 |
    \============================/   */
//Introduction of class:P2
//Problem 2 : Monitor Colour and Monochrome Printing
//This java program use Monitor to simulate a Printer which have 3 printer heads.
//This is the main class of Problem 2 which will read the data (jobs' information) from the input txt file. and store in a local LinkedList
//And create two thread : Thread t1 and Thread t2. 
//Thread t1 call searchJob() method in P2_printerJob
//Thread t2 call StartPrint() method in P2_printerJob
// There are 3 printer head can be use concurrently, Thread t1 and Thread t2 are running concurrently and control by using Monitor.

public class P2 {

	public static void main(String[] args) throws IOException, InterruptedException{
		// printerJob Object of a class that has both searchJob() and StartPrint() methods		
        P2_printerJob printer = new P2_printerJob();

        LinkedList<P2_Job> alljobs = new LinkedList<>(); // queue for all the jobs

        File file = new File(args[0]);
        Scanner scan = new Scanner(file);
        String inputStream = "";
        try{
            while(scan.hasNextLine()){
                inputStream = scan.nextLine();
                if(!inputStream.contains("M") && !inputStream.contains("C")){
                    printer.TotalJob = Integer.parseInt(inputStream);   // first line of the input.txt indicate number of jobs
                    
                }
                else{
                    String [] StreamSplit = inputStream.split(" ");
                    // input each job into alljobs
                    alljobs.add(new P2_Job(StreamSplit[0].charAt(0) ,Integer.parseInt(StreamSplit[0].substring(1)) ,Integer.parseInt(StreamSplit[1])));
                    // each job refer as a thread. Store each thread into a ready queue                    
                }

            }
            // add alljobs into printer's alljob LinkedList
            printer.addAllJob(alljobs); // now the printer have all jobs infor for waiting jobs

        }catch( Exception e){
            System.out.println("error occured"+e.getMessage());
        }

        scan.close();

        //==========================================================================================       

		// Create searchJob thread
        // this t1 thread mainly use for monitoring the jobs and select the jobs for printer
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run()
			{
				try {
					printer.searchJob();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		// Create StartPrint thread
        // this t2 thread mainly use for run the selected jobs  
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run()
			{
				try {

					printer.StartPrint();
					
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		// Start both threads
        
        t1.start();
        t2.start();

		t1.join();
        t2.join();
	}

	

    



}

