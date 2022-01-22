import java.util.*;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/*  
    /============================\
    |  COMP2240 Assignment 2     | 
    |  Name : Ni Zeng            | 
    |  Student Number : c3238805 |
    \============================/   */
//Introduction of class:P3_printerJob
//this class have two main method: searchJob() and StartPrint()
//searchJob() is to find an suitable job according to the job's mode (either Colour or Monochrome ) and avaliable printer Head (only 3 printer heads).
//               There are total 3 avaliable semaphore permit(only 3 printer head),after the desire job is found, add into a Joblist and ready to be print.
//               This method use Semaphoer acquire() before entering current thread's critial section, number of avaliable permit == avaliable print head.
//StartPrint() is to get the jobs from the Joblist (which is ready to be print) and calculate the time (number of Page) to print and update the current time
//             then remove it from the Joblist.
//             This mehod use release() 1 semaphore signal after the job is finished print(and update the current time).

public class P3_printerJob {
    int current = 0;     // keep track of the current job Start Time                            
    int TotalJob = 0;    // total jobs
    String currentType = ""; // initial to ""
    int Mcounter = 0;      // Monochrome print counter
    int Ccounter = 0;       // Colour print counter

	// This class has a list, searchJob (adds jobs to Joblist
	// and StartPrint (removes job when finish printting).
    Semaphore semaphore;
    // Create a list shared by searchJob and StartPrint
    // Size of Joblist is 3.
    LinkedList<P3_Job> Joblist = new LinkedList<>();    // queue for each printing round
    LinkedList<P3_Job> waitingJob = new LinkedList<>(); // queue to stored all waiting jobs
    int capacity = 3;   // printer capacity maximum 3 jobs at the time
    int [] printerHead = new int [3];   // printer have 3 head
    boolean switchMode = false;

    public P3_printerJob(Semaphore semaphore){
        this.semaphore = semaphore;
            
        for(int i= 0 ; i <printerHead.length ; i++ ){
            printerHead[i] = 0; // inital to 0, when  = 0  , printer head is not in use.
        }
    }
    public int findHead(){
        int headNum = 0;
        for(int i = 0; i<printerHead.length ; i++){
            if(printerHead[i] == 0){     // when printerHead = 0, this head is avaliable 
                printerHead[i] = i+1;     // active printerhead (head is active if != 0 )
                headNum = i+1;
                break;
            }
        }
        return headNum;
    }
    public void resetHead(int headNum){
        // after a job is printed, reset the printer head back to 0.
        printerHead[headNum-1] = 0;
    }

    public void addAllJob(LinkedList<P3_Job> waitingJob){
        this.waitingJob = waitingJob;
    }
    
    // Function called by searchJob thread
    public void searchJob() throws InterruptedException
    {
        while (!waitingJob.isEmpty()) {	

            if(Joblist.isEmpty()){  // when there is no mode been choosen
                semaphore.acquire();
                switchMode = false;
                
                currentType = Character.toString(waitingJob.peek().getJobType()); 

                // set current start time to the job
                waitingJob.peek().setStartTime(current);   // set current start time to the job
                // search printer head and assign to current job
                waitingJob.peek().setHead(findHead());

                // printout
                System.out.println("("+waitingJob.peek().getStartTime()+") "+waitingJob.peek().getJobType() + Integer.toString(waitingJob.peek().getID()) + " uses head "+waitingJob.peek().getHead()+ "  (Time: "+waitingJob.peek().getPage()+")");

                if(currentType.matches("M")){
                    Mcounter++;     // add monochrome id counter
                }
                else if (currentType.matches("C")){
                    Ccounter++;     // add colour id counter
                }
               
                // to insert the jobs in the list
                Joblist.add(waitingJob.remove());
                
            }
            else{   // if there is already have job in the jobList(printe mode is set), then select next job
                // check if printer mode needs to be change
                if(Character.toString(waitingJob.peek().getJobType()).matches(currentType) && ((waitingJob.peek().getID()== Mcounter+1) || (waitingJob.peek().getID()== Ccounter+1))){
                    semaphore.acquire();
                    switchMode = false;                            
                    // add into print Joblist
                    waitingJob.peek().setStartTime(current);   // set current start time to the job
                    // search printer head and assign to current job
                    waitingJob.peek().setHead(findHead());

                    // printout
                    System.out.println("("+waitingJob.peek().getStartTime()+") "+waitingJob.peek().getJobType() + Integer.toString(waitingJob.peek().getID()) + " uses head "+waitingJob.peek().getHead()+ "  (Time: "+waitingJob.peek().getPage()+")");

                    // to insert the jobs in the list
                    Joblist.add(waitingJob.remove());

                    if(currentType.matches("M")){
                        Mcounter++;     // add monochrome id counter
                    }
                    else if (currentType.matches("C")){
                        Ccounter++;     // add colour id counter
                    }                   
                }
                else {
                    switchMode = true;

                }    // if require switch mode, it has to wait till Joblist.isEmpty()
            }
        }
    }

    // Function called by StartPrint thread
    public void StartPrint() throws InterruptedException
    {            
        while(true){

            // if next job require to switch mode, then print all jobs currently in the Joblist
            if(!waitingJob.isEmpty() && !Joblist.isEmpty() ){
                if(switchMode == true){
                    // sort the JobList, smallest page get to finishe first
                    // sort Joblist by printing time (sonnest finsh job go first)
                    if(Joblist.size()>1){
                        Collections.sort(Joblist, new sortByTime());
                    }
                    int permit = semaphore.availablePermits();  // current avaliable permit stored into a variable 
                    // finish all jobs that's currently in the joblist
                    while(!Joblist.isEmpty()){
                        //update current time
                        current = Joblist.peek().getStartTime() + Joblist.peek().getPage();    // update current time
                        
                        // rest printer head when job is been printed.
                        resetHead(Joblist.peek().getHead());
            
                        // remove from joblist and print
                        Joblist.removeFirst(); 
                    }
                    // after completed all current joblist, release all semaphore (3 semaphore )
                    for(int i = 0 ; i< 3-permit ; i ++){
                        semaphore.release();
                    }   
                }else if (switchMode == false  && Joblist.size() == 3){
                    // sort the JobList, smallest page get to finishe first
                    // sort Joblist by printing time (sonnest finsh job go first)
                    Collections.sort(Joblist, new sortByTime());

                    //update current time
                    current = Joblist.peek().getStartTime() + Joblist.peek().getPage();    // update current time
                    
                    // rest printer head when job is been printed.
                    resetHead(Joblist.peek().getHead());
        
                    // remove from joblist and print
                    Joblist.removeFirst(); 
                    semaphore.release();
                }

            }
            
            else if(waitingJob.isEmpty() && !Joblist.isEmpty()){
                    // sort the JobList, smallest page get to finishe first
                    // sort Joblist by printing time (sonnest finsh job go first)
                    if(Joblist.size()>1){
                        Collections.sort(Joblist, new sortByTime());
                    }

                    while(!Joblist.isEmpty()){
                        //update current time
                        if(current < Joblist.peek().getStartTime() + Joblist.peek().getPage()){
                            current = Joblist.peek().getStartTime() + Joblist.peek().getPage();    // update current time
                        }
                        
                        // rest printer head when job is been printed.
                        resetHead(Joblist.peek().getHead());
            
                        // remove from joblist and print
                        Joblist.removeFirst(); 

                        semaphore.release();
                    }
            }else if(waitingJob.isEmpty() && semaphore.availablePermits() == 3){
                System.out.println("("+current+") " + "DONE");
                System.exit(0); // when all the waiting jobs are printed

            }
        
            
        }
    }
    public class sortByTime implements Comparator<P3_Job>{

        @Override
        public int compare(P3_Job o1, P3_Job o2) {
            return (o1.getPage()+ o1.getStartTime()) - (o2.getPage()+o2.getStartTime());
        }
    }
}
