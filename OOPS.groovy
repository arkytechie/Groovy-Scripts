//creating objects from a class

class Emp {
   int empID;
   String empName;
	
   static void main(String[] args) {
      Emp ep = new Emp();
      ep.empID = 1000;
      ep.empName = "Arkesh";     
   } 
}



//Using getter and setter methods

class Emp {
   private int empID;
   private String empName;
	
   void setEmpID(int pID) {
      empID = pID;
   }
	
   void setEmpName(String pName) {
      empName = pName;
   }
	
   int getEmpID() {
      return this.empID;
   }
	
   String getEmpName() {
      return this.empName;
   }
	
   static void main(String[] args) {
      Emp ep = new Emp();
      ep.setEmpID(1000);
      ep.setEmpName("Arkesh");
		
      println(ep.getEmpID());
      println(ep.getEmpName());
   } 
}



//Showcasing Instance Methods by creating an instance class called total

class Emp {
   int empID;
   String empName;
	
   int money1;
   int money2;
   int money3;
	
   int Total() {
      return money1 + money2 + money3;
   }
	
   static void main(String[] args) {
      Emp ep = new Emp();
      ep.empID = 1000;
      ep.empName="Arkesh";
		
      ep.money1 = 10;
      ep.money2 = 20;
      ep.money3 = 30;
		
      println(ep.Total());
   }
}



//Create multiple objects for class Emp

class Emp {
   int empID;
   String empName;
	
   int money1;
   int money2;
   int money3;
	
   int Total() { 
      return money1+money2+money3;
   } 
	
   static void main(String[] args) {
      Emp ep = new Emp();
      ep.empID = 1000;
      ep.empName = "Arkesh";
		
      ep.money1 = 10;
      ep.money2 = 20;
      ep.money3 = 30;
		
      println(ep.Total()); 
   
      Emp ep1 = new Emp();
      ep.empID = 1000;
      ep.empName = "Arkesh";
		
      ep.money1 = 10;
      ep.money2 = 20;
      ep.money3 = 40;
		
      println(ep.Total());  
        
      Emp ep3 = new Emp();
      ep.empID = 1000;
      ep.empName = "Arkesh";
		
      ep.money1 = 10; 
      ep.money2 = 20;
      ep.money3 = 50;
		
      println(ep.Total());
   } 
}


//Inheritance can be defined as the process where one class acquires the properties (methods and fields) of another. With the use of inheritance the information is made manageable in a hierarchical order.

class Example {
   static void main(String[] args) {
      Student st = new Student();
      st.StudentID = 1;
		
      st.Marks1 = 10;
      st.name = "Joe";
		
      println(st.name);
   }
} 

class Person {
   public String name;
   public Person() {}  
} 

class Student extends Person {
   int StudentID
   int Marks1;
	
   public Student() {
      super();
   } 
}   


