import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.io.*;
import java.util.Scanner;

/*  
    /============================\
    |  COMP2240 Assignment 2     | 
    |  Name : Ni Zeng            | 
    |  Student Number : c3238805 |
    \============================/   */
//Introduction of class:WarControlling
// java program for solving the WAR problem,
// each war will considerate as a single thread.
// when crossing the intersection, will use semaphore on the thread 
// and increase 200 ms delay for 3 times.
// after perticular theard crossed the intesection, release the semaphore and 
// allow next thread to cross the intesection. Temaniate when track 1 and track 2 both
// reached 150 limit.

public class P1{
    
    static class Resource { // Will take Resource as  shared class
        static int Track1,Track2 = 0;       // variable to store number of thread crossed the track1 and track2
        static int CheckPointCount = 0;     // variable for the checkPoint counter 
        static int maxtrackNumber = 150;    // variable for the maximum wars can cross the track 
    }
    public static void main(String[] args) throws Exception {
        // initial semaphore, only allow one war into intersection.
        Semaphore semaphore = new Semaphore(1,true);    
        //==========================================================================================
        // inital warCount to 1, was's id start from 1
        int waridCount = 1;
        Queue<Thread> warThread = new LinkedList<>();   // a queue for the thread.(each war is a thread)
        
        File file = new File(args[0]); 
        Scanner scan = new Scanner(file);
        String inputStream = "";

        // first read the data from input txt file
        try{
            while(scan.hasNextLine()){
                inputStream = scan.nextLine();
                String [] inputStreamSplit = inputStream.split(" ");

                String [] NSplit = inputStreamSplit[0].split("=");
                String [] SSplit = inputStreamSplit[1].split("=");
                String [] ESplit = inputStreamSplit[2].split("=");
                String [] WSplit = inputStreamSplit[3].split("=");
                //input number of wars into each direction, and assign unique id to each war.
                for(int i =0 ; i < Integer.valueOf(NSplit[1]) ; i++){  
                    P1_War wars = new P1_War(true,(i+1),"N");
                    warThread.add(new Thread(new warRunnable(semaphore,wars)));        // WARS on the north are loaded (from storage). create a new thread
                    waridCount++;
                }
                for(int i =0 ; i < Integer.valueOf(SSplit[1]) ; i++){
                    P1_War wars = new P1_War(false,waridCount , "S");
                    warThread.add(new Thread(new warRunnable(semaphore,wars)));        // WARS on the south are unloaded (from dock). create a new thread
                    waridCount++;
                }
                for(int i =0 ; i < Integer.valueOf(ESplit[1]) ; i++){
                    P1_War wars = new P1_War(false,Integer.valueOf(waridCount) , "E");
                    warThread.add(new Thread(new warRunnable(semaphore,wars)));        // WARS on the east are Unloaded (from dock). create a new thread
                    waridCount++;
                }
                for(int i =0 ; i < Integer.valueOf(WSplit[1]) ; i++){
                    P1_War wars = new P1_War(true,Integer.valueOf(waridCount), "W");
                    warThread.add(new Thread(new warRunnable(semaphore,wars)));        // WARS on the west are loaded (from storage). create a new thread
                    waridCount++;
                }

            }
        }catch( Exception e){
            System.out.println("error occured"+e.getMessage());
        }

        scan.close();

        //==========================================================================================       
        for(Thread t : warThread){   // start all the thread in warThread queue                     
            t.start();
            
        }
    }

    // this class is for the warRunnable 
    static class warRunnable implements Runnable {

        private P1_War wars; 
        private Semaphore semaphore;
        private boolean reachLimite;

        public warRunnable(Semaphore semaphore , P1_War wars ){
            this.wars = wars;
            this.semaphore = semaphore;
        }
        @Override
		public void run(){ 

                while(!reachLimite){
                    System.out.println(wars.getWarInfo()+"Wating at the Intersection. " + wars.getrowards());

                    try {

                        semaphore.acquire();        // acquire semaphore on current thread 
                    
                        if(Resource.Track1 >= Resource.maxtrackNumber && (wars.getFrom() == "E" || wars.getFrom() == "W")){
                            reachLimite = true; // when reach limit, boolean value reachLimite is true
                            
                            semaphore.release();    // release semaphore for other thread
                                                    // release intersection semaphore , prevent deadlock 
                            break;            // break out the while loop 
                        }
                        else if(Resource.Track2 >= Resource.maxtrackNumber && (wars.getFrom() == "N" || wars.getFrom() == "S") ){
                            reachLimite = true; // when reach limit, boolean value reachLimite is true
                            
                            semaphore.release();        // release semaphore for other thread
                                                        // release intersection semaphore , prevent deadlock 
                            break;          // break out the while loop 
                        }

                        //get the war (the war which has the permission) croosed the intersection
                        for(int i = 0; i<3 ; i++){      // each check point delay 200 ms, there are 3 checkPoint
                            Resource.CheckPointCount++;
                            System.out.println(wars.getWarInfo()+"Crossing intersection Checkpoint "+Resource.CheckPointCount+".");
                            try {
                                Thread.sleep(200);  // put thread to sleep 200ms
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }  
                        }
                        if(wars.getFrom() == "E" || wars.getFrom() == "W"){ 
                            Resource.Track1++;// update the number of wars that passed track1
                        }
                        else if(wars.getFrom() == "N" || wars.getFrom() == "S"){
                            Resource.Track2++;// update the number of wars that passed track1
                        }
                        System.out.println(wars.getWarInfo()+"Crossed the intersection.");
                        wars.reachDestination();    // after the war reach destination, switch loctaion and loaded status.
                        Resource.CheckPointCount = 0;   //reset Resource.CheckPointCount. 

                        // output Total crossed in Track1 and Track2.
                        System.out.println("Total crossed in Track1: "+Resource.Track1+" Track2: "+Resource.Track2);
                                                
                          
                        if(Resource.Track1 >= Resource.maxtrackNumber && Resource.Track2 >= Resource.maxtrackNumber){  //terminate program if both reach limit
                            System.out.println("Total wars crossed in Track1 and Track2 has reach the limit of "+ Resource.maxtrackNumber + "\n" + "Terminate the program.");
                            System.exit(0);
                        } 
                        else if(Resource.Track1 >= Resource.maxtrackNumber && Resource.Track2 == 0){  //terminate program if both reach limit
                            
                            System.out.println("Total wars crossed in Track1 reached the limit of "+ Resource.maxtrackNumber + "\n" + "Terminate the program.");
                            System.exit(0);
                        }
                        else if(Resource.Track2 >= Resource.maxtrackNumber && Resource.Track1 == 0){  //terminate program if both reach limit
                            
                            System.out.println("Total wars crossed in Track2 reached the limit of "+ Resource.maxtrackNumber + "\n" + "Terminate the program.");
                            System.exit(0);
                        }
                    } catch (InterruptedException e1) {                        
                        e1.printStackTrace();
                    }   
                        
                    semaphore.release();        // release semaphore
                              
                }                                                          
        }

    }


    
}
