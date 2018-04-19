package jmetal.problems.MaF;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
* Class representing problem MaF07 
*/
public class MaF07 extends Problem {   
/** 
* Creates a default MaF07 problem (22 variables and 3 objectives)
* @param solutionType The solution type must "Real" or "BinaryReal". 
*/
public MaF07(String solutionType) throws ClassNotFoundException {
this(solutionType, 22, 3);
} // MaF07   

/** 
* Creates a MaF07 problem instance
* @param numberOfVariables Number of variables
* @param numberOfObjectives Number of objective functions
* @param solutionType The solution type must "Real" or "BinaryReal". 
*/
public MaF07(String solutionType, 
           Integer numberOfVariables, 
		         Integer numberOfObjectives) {
numberOfVariables_  = numberOfVariables;
numberOfObjectives_ = numberOfObjectives;
numberOfConstraints_= 0;
problemName_        = "MaF07";
    
lowerLimit_ = new double[numberOfVariables_];
upperLimit_ = new double[numberOfVariables_];        
for (int var = 0; var < numberOfVariables; var++){
  lowerLimit_[var] = 0.0;
  upperLimit_[var] = 1.0;
} //for

if (solutionType.compareTo("BinaryReal") == 0)
	solutionType_ = new BinaryRealSolutionType(this) ;
else if (solutionType.compareTo("Real") == 0)
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
//MaF07 , DTLZ7
public void evaluate(Solution solution) throws JMException {
	
	Variable[] gen  = solution.getDecisionVariables();   
	double [] x = new double[numberOfVariables_];
	double [] f = new double[numberOfObjectives_];
	    
	for (int i = 0; i < numberOfVariables_; i++)
	  x[i] = gen[i].getValue();
	
//	evaluate g,h
	double g=0,h=0,sub1;
	for(int i=numberOfObjectives_-1;i<numberOfVariables_;i++)
		g+=x[i];
	g=1+9*g/(numberOfVariables_-numberOfObjectives_+1);
	sub1=1+g;
	for(int i=0;i<numberOfObjectives_-1;i++)
		h+=(x[i]*(1+Math.sin(3*Math.PI*x[i]))/sub1);
	h=numberOfObjectives_-h;
//	evaluate f1,...,m-1,m
	for(int i=0;i<numberOfObjectives_;i++)
		f[i]=x[i];
	f[numberOfObjectives_-1]=h*sub1;	
	
	for (int i = 0; i < numberOfObjectives_; i++)
		  solution.setObjective(i,f[i]);	
	
}  

}

