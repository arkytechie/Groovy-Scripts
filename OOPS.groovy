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


//Inheritance can be defined as the process where one class acquires the properties (methods and fields) of another. 
//With the use of inheritance the information is made manageable in a hierarchical order.

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


//Inner classes are defined within another classes. 
//The enclosing class can use the inner class as usual. 
//On the other side, a inner class can access members of its enclosing class, even if they are private. 
//Classes other than the enclosing class are not allowed to access inner classes.

class Example { 
   static void main(String[] args) { 
      Outer outobj = new Outer(); 
      outobj.name = "Joe"; 
      outobj.callInnerMethod() 
   } 
} 

class Outer { 
   String name;
	
   def callInnerMethod() { 
      new Inner().methodA() 
   } 
	
   class Inner {
      def methodA() { 
         println(name); 
      } 
   } 
	
}   


/* Abstract classes represent generic concepts, thus, they cannot be instantiated, 
being created to be subclassed. Their members include fields/properties and abstract or concrete methods. 
Abstract methods do not have implementation, and must be implemented by concrete subclasses. 
Abstract classes must be declared with abstract keyword. 
Abstract methods must also be declared with abstract keyword.
In the following example, note that the Person class is now made into an abstract class and 
cannot be instantiated. Also note that there is an abstract method called DisplayMarks in the 
abstract class which has no implementation details. In the student class it is mandatory to add the 
implementation details.
*/
class Example { 
   static void main(String[] args) { 
      Student st = new Student(); 
      st.StudentID = 1;
		
      st.Marks1 = 10; 
      st.name="Joe"; 
		
      println(st.name); 
      println(st.DisplayMarks()); 
   } 
} 

abstract class Person { 
   public String name; 
   public Person() { } 
   abstract void DisplayMarks();
}
 
class Student extends Person { 
   int StudentID 
   int Marks1; 
	
   public Student() { 
      super(); 
   } 
	
   void DisplayMarks() { 
      println(Marks1); 
   }  
}

/* 
An interface defines a contract that a class needs to conform to. 
An interface only defines a list of methods that need to be implemented, but does not define the methods 
implementation. An interface needs to be declared using the interface keyword. 
An interface only defines method signatures. Methods of an interface are always public. 
It is an error to use protected or private methods in interfaces.
*/

class Example {
   static void main(String[] args) {
      Student st = new Student();
      st.StudentID = 1;
      st.Marks1 = 10;
      println(st.DisplayMarks());
   } 
} 

interface Marks { 
   void DisplayMarks(); 
} 

class Student implements Marks {
   int StudentID
   int Marks1;
	
   void DisplayMarks() {
      println(Marks1);
   }
}




