package jmetal.problems.MaF;
import java.util.ArrayList;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
* Class representing problem MaF13 
*/
public class MaF13 extends Problem {   
/** 
* Creates a default MaF13 problem (5 variables and 3 objectives)
* @param solutionType The solution type must "Real".
*/
public MaF13(String solutionType) throws ClassNotFoundException {
this(solutionType, 5, 3);
} // MaF13   

/** 
* Creates a MaF13 problem instance
* @param numberOfVariables Number of variables
* @param numberOfObjectives Number of objective functions
* @param solutionType The solution type must "Real" or "BinaryReal". 
*/
public MaF13(String solutionType, 
           Integer numberOfVariables, 
		         Integer numberOfObjectives) {
numberOfVariables_  = numberOfVariables;
numberOfObjectives_ = numberOfObjectives;
numberOfConstraints_= 0;
problemName_        = "MaF13";
    
lowerLimit_ = new double[numberOfVariables_];
upperLimit_ = new double[numberOfVariables_];        
for (int var = 0; var < numberOfVariables; var++){
  lowerLimit_[var] = 0.0;
  upperLimit_[var] = 1.0;
} //for

if (solutionType.compareTo("Real") == 0)
	solutionType_ = new RealSolutionType(this) ;
else {
	System.out.println("Error: solution type " + solutionType + " invalid") ;
	System.exit(-1) ;
}            
}            

/** 
* Evaluates a solution 
* @param solution The solution to evaluate
* @throws JMException 
*/  
//MaF13 , PF7
public void evaluate(Solution solution) throws JMException {
	
	Variable[] gen  = solution.getDecisionVariables();   
	double [] x = new double[numberOfVariables_];
	double [] f = new double[numberOfObjectives_];
	    
	for (int i = 0; i < numberOfVariables_; i++)
	  x[i] = gen[i].getValue();
	
//	evaluate J,y,sub1,sub2,sub3,sub4
	double[] y=new double[numberOfVariables_];
	for(int i=0;i<numberOfVariables_;i++)
		y[i]=x[i]-2*x[1]*Math.sin(2*Math.PI*x[0]+(i+1)*Math.PI/numberOfVariables_);
	ArrayList<Integer> J1=new ArrayList<Integer>();
	ArrayList<Integer> J2=new ArrayList<Integer>();
	ArrayList<Integer> J3=new ArrayList<Integer>();
	ArrayList<Integer> J4=new ArrayList<Integer>();
	double sub1=0,sub2=0,sub3=0,sub4=0;
	for(int i=4;i<=numberOfVariables_;i=i+3)
	{
		J1.add(i);
		sub1+=Math.pow(y[i-1], 2);
	}
	sub1=2*sub1/J1.size();
	for(int i=5;i<=numberOfVariables_;i=i+3)
	{
		J2.add(i);
		sub2+=Math.pow(y[i-1], 2);
	}
	sub2=2*sub2/J2.size();
	for(int i=3;i<=numberOfVariables_;i=i+3)
	{
		J3.add(i);
		sub3+=Math.pow(y[i-1], 2);
	}
	sub3=2*sub3/J3.size();
	for(int i=4;i<=numberOfVariables_;i++)
	{
		J4.add(i);
		sub4+=Math.pow(y[i-1], 2);
	}
	sub4=2*sub4/J4.size();
//	evaluate f1,f2,f3,f4,...m	
	f[0]=Math.sin(Math.PI*x[0]/2)+sub1;
	f[1]=Math.cos(Math.PI*x[0]/2)*Math.sin(Math.PI*x[1]/2)+sub2;
	f[2]=Math.cos(Math.PI*x[0]/2)*Math.cos(Math.PI*x[1]/2)+sub3;
	for(int i=3;i<numberOfObjectives_;i++)
		f[i]=Math.pow(f[0], 2)+Math.pow(f[1], 10)+Math.pow(f[2], 10)+sub4;
	
	for (int i = 0; i < numberOfObjectives_; i++)
		  solution.setObjective(i,f[i]);	
	
}  

}

