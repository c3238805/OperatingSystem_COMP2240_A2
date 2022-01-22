import java.util.*;
import java.util.LinkedList;

/*  
    /============================\
    |  COMP2240 Assignment 2     | 
    |  Name : Ni Zeng            | 
    |  Student Number : c3238805 |
    \============================/   */
//Introduction of class:P2_printerJob
//this class have two main method: searchJob() and StartPrint()
//searchJob() is to find an suitable job according to the job's mode (either Colour or Monochrome ) and avaliable printer Head (only 3 printer heads).
//               After the desire job is found, add into a Joblist and ready to be print.
//               This method use Monitor wait() when there are no head avaliable or job required switch printing mode. 
//StartPrint() is to get the jobs from the Joblist (which is ready to be print) and calculate the time (number of Page) to print and update the current time
//             then remove it from the Joblist
//             This mehod use Monitor wait() when the Joblist is empty or there are head still avaliable for more jobs(when switch mode is not required).


public class P2_printerJob {
    // This class has a list, searchJob (adds jobs to Joblist
	// and StartPrint (removes job when finish printting).

     int current = 0;     // keep track of the current job Start Time                            
     int TotalJob = 0;    // total jobs
     String currentType = ""; // initial to ""
     int Mcounter = 0;      // count number of Monochrome printing
     int Ccounter = 0;      // count number of colour printing

		// Create a list shared by searchJob and StartPrint
		LinkedList<P2_Job> Joblist = new LinkedList<>();    // queue for each printing round
        LinkedList<P2_Job> waitingJob = new LinkedList<>(); // queue to stored all waiting jobs
		int capacity = 3;   // printer capacity maximum 3 jobs at the time
        int [] printerHead = new int [3];   // printer have 3 head
        boolean switchMode = false;

        public P2_printerJob(){
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

        public void addAllJob(LinkedList<P2_Job> waitingJob){
            this.waitingJob = waitingJob;
        }
        
		// Function called by searchJob thread
		public void searchJob() throws InterruptedException
		{

            while (!waitingJob.isEmpty()) {	
                synchronized (this)
                {                             
                    // searchJob thread waits() while list is full 
                    while (Joblist.size() == capacity){
                        wait(); // if printer is currently printing 3 jobs, the other job has to wait.
                    }
                    
                        if(Joblist.isEmpty()){  // when there is no mode been choosen
                            switchMode = false;
                            // set its mode in share class (currentType)
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
                            // sort the JobList, smallest page get to finishe first
                            // sort Joblist by printing time (sonnest finsh job go first)
                            Collections.sort(Joblist, new sortByTime());

                        }
                        else{   // if there is already have job in the jobList(printe mode is set), then select next job

                            // check if printer mode needs to be change
                            if(Character.toString(waitingJob.peek().getJobType()).matches(currentType) && ((waitingJob.peek().getID()== Mcounter+1) || (waitingJob.peek().getID()== Ccounter+1))){
                                switchMode = false;                            
                                // add into print Joblist
                                waitingJob.peek().setStartTime(current);   // set current start time to the job
                                // search printer head and assign to current job
                                waitingJob.peek().setHead(findHead());

                                // printout
                                System.out.println("("+waitingJob.peek().getStartTime()+") "+waitingJob.peek().getJobType() + Integer.toString(waitingJob.peek().getID()) + " uses head "+waitingJob.peek().getHead()+ "  (Time: "+waitingJob.peek().getPage()+")");

                                // to insert the jobs in the list
                                Joblist.add(waitingJob.remove());
                                // sort the JobList, smallest page get to finishe first
                                // sort Joblist by printing time (sonnest finsh job go first)
                                Collections.sort(Joblist, new sortByTime());

                                if(currentType.matches("M")){
                                    Mcounter++;     // add monochrome id counter
                                }
                                else if (currentType.matches("C")){
                                    Ccounter++;     // add colour id counter
                                }
                                
                            }
                            else {
                                switchMode = true;
                                wait();
                            }    // if require switch mode, it has to wait till Joblist.isEmpty()

                        }
                        notifyAll();    
                }
            }
        }

		// Function called by StartPrint thread
		public void StartPrint() throws InterruptedException
		{
            
			while (true) {
				synchronized (this)
				{
                    if(waitingJob.isEmpty() && Joblist.isEmpty()){
                        System.out.println("("+current+") " + "DONE");
                        System.exit(0); // when all the waiting jobs are printed

                    }
                   
                    while (Joblist.size() == 0 ) {                                                
                        wait(); // wait while nothing to print for StartPrint thread                                                                  
                    }

                    if(!waitingJob.isEmpty() ){
                        while((Joblist.size() > 0 && Joblist.size()<3 && Character.toString(waitingJob.peek().getJobType()).matches(currentType))){
                            wait();
                        }
                    }
                
                    //update current time
                    current = Joblist.peek().getStartTime() + Joblist.peek().getPage();    // update current time
                    
                    // rest printer head when job is been printed.
                    resetHead(Joblist.peek().getHead());
           
                    // remove from joblist and print
                    Joblist.removeFirst();                   
                    
                    // Wake up all thread that is currently on wait(), 
                    notifyAll();
                }
			}
		}

        public class sortByTime implements Comparator<P2_Job>{

            @Override
            public int compare(P2_Job o1, P2_Job o2) {
                
                return (o1.getPage()+ o1.getStartTime()) - (o2.getPage()+o2.getStartTime());
            }
            
        }
        
	
}
