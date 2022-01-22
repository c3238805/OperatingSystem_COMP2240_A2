/*  
    /============================\
    |  COMP2240 Assignment 2     | 
    |  Name : Ni Zeng            | 
    |  Student Number : c3238805 |
    \============================/   */
//Introduction of class:P1_War
// this class contain the war's infromation:  
// status(loaded or unloaded), war's id , war's from(where war is coming from) and all the getter and setter 
// this class also contain a method to switch the war's status and from (if war have reached its destination )                                         

public class P1_War {

        private boolean Loaded;     // variable for loaded or unloaded status
        private String id = "";     // variable for war's id
        private String from;        // variable for where war is coming from
        public P1_War(boolean Loaded , int i , String from){
            this.Loaded = Loaded;
            this.setid(i);
            this.from = from;
        }
        public void setid(int i){
            id = "WAR-" + i;    
        }
        public String getid(){
            return id;
        }
        public void reachDestination(){
            Loaded = !Loaded ;  // switch loaded or unloaded status
            // switch war's from direction
            if(from=="N"){
                from = "S";
            }
            else if(from=="S"){
                from = "N";
            }
            else if(from=="E"){
                from = "W";
            }
            else{
                from = "E";
            }
        }
        public boolean getStatus(){
            return Loaded;      // return boolean 
        }
        public String getrowards(){
            String towards = "";    // towards opposite direction of from
            if(from=="N"){
                towards = "Going towards Dock 2";
            }
            else if(from=="S"){
                towards = "Going towards Storage 2";
            }
            else if(from=="E"){
                towards = "Going towards Storage 1";
            }
            else{
                towards = "Going towards Dock 1";
            }
            return towards;
        }
        public String getWarInfo(){
            String statusString = "";
            if(Loaded){
                statusString = " (Loaded): ";
            }
            else{
                statusString = " (Unloaded): ";
            }
            return getid() + statusString ; 
        }
        public String getFrom(){
            return this.from;       // return war's from location
        }
    
}
