package jmetal.problems.MaF;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
* Class representing problem MaF11 
*/
public class MaF11 extends Problem {   
/** 
* Creates a default MaF11 problem (12 variables and 3 objectives)
* @param solutionType The solution type must "Real" or "BinaryReal".
*/
public static int K11,L11;
public MaF11(String solutionType) throws ClassNotFoundException {
this(solutionType, 12, 3);
} // MaF11   

/** 
* Creates a MaF11 problem instance
* @param numberOfVariables Number of variables
* @param numberOfObjectives Number of objective functions
* @param solutionType The solution type must "Real" or "BinaryReal". 
*/
public MaF11(String solutionType, 
           Integer numberOfVariables, 
		         Integer numberOfObjectives) {
numberOfVariables_  = numberOfVariables;
numberOfObjectives_ = numberOfObjectives;
numberOfConstraints_= 0;
problemName_        = "MaF11";
numberOfVariables_=(int) (Math.ceil((numberOfVariables_-numberOfObjectives_+1)/2.0)*2+numberOfObjectives_-1);
K11=numberOfObjectives_-1;
L11=numberOfVariables_-K11;	
lowerLimit_ = new double[numberOfVariables_];
upperLimit_ = new double[numberOfVariables_];        
for (int var = 0; var < numberOfVariables_; var++){
	lowerLimit_[var] = 0.0;
	upperLimit_[var] = 2.0*(var+1);
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
//MaF11 , WFG2
public void evaluate(Solution solution) throws JMException {
	
	Variable[] gen  = solution.getDecisionVariables();   
	double [] x = new double[numberOfVariables_];
	double [] f = new double[numberOfObjectives_];
	    
	for (int i = 0; i < numberOfVariables_; i++)
	  x[i] = gen[i].getValue();
	
//	evaluate zi,t1i,t2i,t3i,t4i,yi
	double[] z=new double[numberOfVariables_];
	double[] t1=new double[numberOfVariables_];
	double[] t2=new double[(numberOfVariables_+K11)/2];
	double[] t3=new double[numberOfObjectives_];
	double[] y=new double[numberOfObjectives_];
	double sub1=0,sub2=0;
	int lb=0,ub=0;
	for(int i=0;i<K11;i++)
	{
		z[i]=x[i]/(2*i+2);
		t1[i]=z[i];
		t2[i]=t1[i];
	}
	for(int i=K11;i<numberOfVariables_;i++)
	{
		z[i]=x[i]/(2*i+2);
		t1[i]=Math.abs(z[i]-0.35)/(Math.abs(Math.floor(0.35-z[i])+0.35));
	}
	for(int i=K11;i<t2.length;i++)
		t2[i]=(t1[2*i-K11]+t1[2*i-K11+1]+2*Math.abs(t1[2*i-K11]-t1[2*i-K11+1]))/3;
	sub2=K11/(numberOfObjectives_-1);
	for(int i=0;i<numberOfObjectives_-1;i++)
	{
		sub1=0;
		lb=i*K11/(numberOfObjectives_-1)+1;ub=(i+1)*K11/(numberOfObjectives_-1);
		for(int j=lb-1;j<ub;j++)
			sub1+=t2[j];
		t3[i]=sub1/sub2;
	}
	lb=K11+1;ub=(numberOfVariables_+K11)/2;sub1=0;sub2=(numberOfVariables_-K11)/2;
	for(int j=lb-1;j<ub;j++)
		sub1+=t2[j];
	t3[numberOfObjectives_-1]=sub1/sub2;
	for(int i=0;i<numberOfObjectives_-1;i++)
		y[i]=(t3[i]-0.5)*Math.max(1, t3[numberOfObjectives_-1])+0.5;
	y[numberOfObjectives_-1]=t3[numberOfObjectives_-1];
	
//	evaluate fm,fm-1,...,2,f1
	double subf1=1;
	f[numberOfObjectives_-1]=y[numberOfObjectives_-1]+2*numberOfObjectives_*(1-y[0]*Math.pow(Math.cos(5*Math.PI*y[0]),2));
	for(int i=numberOfObjectives_-2;i>0;i--)
	{
		subf1*=(1-Math.cos(Math.PI*y[numberOfObjectives_-i-2]/2));
		f[i]=y[numberOfObjectives_-1]+2*(i+1)*subf1*(1-Math.sin(Math.PI*y[numberOfObjectives_-i-1]/2));
	}
	f[0]=y[numberOfObjectives_-1]+2*subf1*(1-Math.cos(Math.PI*y[numberOfObjectives_-2]/2));
	
	for (int i = 0; i < numberOfObjectives_; i++)
		  solution.setObjective(i,f[i]);	
	
}  

}

