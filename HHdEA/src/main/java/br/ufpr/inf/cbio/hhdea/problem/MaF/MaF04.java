package jmetal.problems.MaF;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
* Class representing problem MaF04 
*/
public class MaF04 extends Problem {   
/** 
* Creates a default MaF04 problem (7 variables and 3 objectives)
* @param solutionType The solution type must "Real" or "BinaryReal". 
*/
	public static double const4[];
public MaF04(String solutionType) throws ClassNotFoundException {
this(solutionType, 12, 3);
} // MaF04   

/** 
* Creates a MaF04 problem instance
* @param numberOfVariables Number of variables
* @param numberOfObjectives Number of objective functions
* @param solutionType The solution type must "Real" or "BinaryReal". 
*/
public MaF04(String solutionType, 
           Integer numberOfVariables, 
		         Integer numberOfObjectives) {
numberOfVariables_  = numberOfVariables;
numberOfObjectives_ = numberOfObjectives;
numberOfConstraints_= 0;
problemName_        = "MaF04";

//other constants during the whole process once M&D are defined
double[] c4=new double[numberOfObjectives_];
for(int i=0;i<numberOfObjectives_;i++)
	c4[i]=Math.pow(2,i+1);
const4=c4;

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
//MaF04 , inverted badly scaled DTLZ3
public void evaluate(Solution solution) throws JMException {
	
	Variable[] gen  = solution.getDecisionVariables();   
	double [] x = new double[numberOfVariables_];
	double [] f = new double[numberOfObjectives_];
	    
	for (int i = 0; i < numberOfVariables_; i++)
	  x[i] = gen[i].getValue();
	
	double g=0;
//	evaluate g
	for(int i=numberOfObjectives_-1;i<numberOfVariables_;i++)
		g+=(Math.pow(x[i]-0.5, 2)-Math.cos(20*Math.PI*(x[i]-0.5)));
	g=100*(numberOfVariables_-numberOfObjectives_+1+g);
	double subf1=1,subf3=1+g;
//	evaluate fm,fm-1,...2,f1
	f[numberOfObjectives_-1]=const4[numberOfObjectives_-1]*(1-Math.sin(Math.PI*x[0]/2))*subf3;
//	fi=2^i*(1-subf1*subf2)*(subf3)
	for(int i=numberOfObjectives_-2;i>0;i--)
	{
		subf1*=Math.cos(Math.PI*x[numberOfObjectives_-i-2]/2);
		f[i]=const4[i]*(1-subf1*Math.sin(Math.PI*x[numberOfObjectives_-i-1]/2))*subf3;
	}
	f[0]=const4[0]*(1-subf1*Math.cos(Math.PI*x[numberOfObjectives_-2]/2))*subf3;
	
	for (int i = 0; i < numberOfObjectives_; i++)
		  solution.setObjective(i,f[i]);	
	
}  

}

