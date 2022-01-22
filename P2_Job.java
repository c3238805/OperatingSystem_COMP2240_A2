/*  
    /============================\
    |  COMP2240 Assignment 2     | 
    |  Name : Ni Zeng            | 
    |  Student Number : c3238805 |
    \============================/   */
//Introduction of class:P2_Job
//this class mainly for storing the job's information 
// variable : jobtype , jobID, numPage, startTime, head
// and all the getter and setter for thoes variable

public class P2_Job {
    
        char jobtype ;
        int jobID ;
        int numPage = 0;
        int startTime = 0;
        int head;   //  variable that store printer's head
        public P2_Job(char jobtype,int jobID , int numPage){
            this.jobID = jobID;     // job's id
            this.numPage = numPage ;    // number of page to print
            this.jobtype = jobtype;     // printing type
            this.head = 0; // inital head to 0 
        }

        public void setHead(int headNum){
            head = headNum;
        }
        public int getHead(){
            return head;
        }
        public char getJobType(){
            return this.jobtype;
        }
        public int getID(){
            return this.jobID;
        }
        public int getPage(){
            return this.numPage;
        }
        public void setStartTime(int current){
            startTime = current;
        }
        public int getStartTime(){
            return startTime;
        }
    
}
