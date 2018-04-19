package jmetal.problems.MaF;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
* Class representing problem MaF06 
*/
public class MaF06 extends Problem {   
/** 
* Creates a default MaF06 problem (7 variables and 3 objectives)
* @param solutionType The solution type must "Real" or "BinaryReal". 
*/
public MaF06(String solutionType) throws ClassNotFoundException {
this(solutionType, 12, 3);
} // MaF06   

/** 
* Creates a MaF06 problem instance
* @param numberOfVariables Number of variables
* @param numberOfObjectives Number of objective functions
* @param solutionType The solution type must "Real" or "BinaryReal". 
*/
public MaF06(String solutionType, 
           Integer numberOfVariables, 
		         Integer numberOfObjectives) {
numberOfVariables_  = numberOfVariables;
numberOfObjectives_ = numberOfObjectives;
numberOfConstraints_= 0;
problemName_        = "MaF06";
    
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
//MaF06 , DTLZ5(I,M)
public void evaluate(Solution solution) throws JMException {
	
	Variable[] gen  = solution.getDecisionVariables();   
	double [] x = new double[numberOfVariables_];
	double [] f = new double[numberOfObjectives_];
	    
	for (int i = 0; i < numberOfVariables_; i++)
	  x[i] = gen[i].getValue();
	
	double[] thet=new double[numberOfObjectives_-1];
	int lb,ub,ri=0;
	double g=0,sub1,sub2;
//	evaluate g,thet
	for(int i=numberOfObjectives_-1;i<numberOfVariables_;i++)
		g+=Math.pow(x[i]-0.5, 2);
	sub1=100*g+1;sub2=1+g;
	for(int i=0;i<1;i++)
		thet[i]=Math.PI*x[i]/2;
	for(int i=1;i<numberOfObjectives_-1;i++)
		thet[i]=Math.PI*(1+2*g*x[i])/(4*sub2);
//	evaluate fm,fm-1,...,2,f1
	f[numberOfObjectives_-1]=Math.sin(thet[0])*sub1;
	double subf1=1,subf2,subf3;
//	fi=cos(thet1)cos(thet2)...cos(thet[m-i])*sin(thet(m-i+1))*(1+g[i]),fi=subf1*subf2*subf3
	for(int i=numberOfObjectives_-2;i>0;i--)
	{	
		subf1*=Math.cos(thet[numberOfObjectives_-i-2]);
		f[i]=subf1*Math.sin(thet[numberOfObjectives_-i-1])*sub1;
	}
	f[0]=subf1*Math.cos(thet[numberOfObjectives_-2])*sub1;
	
	for (int i = 0; i < numberOfObjectives_; i++)
		  solution.setObjective(i,f[i]);	
	
}  

}

