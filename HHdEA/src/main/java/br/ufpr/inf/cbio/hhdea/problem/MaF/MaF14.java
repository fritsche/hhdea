package jmetal.problems.MaF;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
* Class representing problem MaF14 
*/
public class MaF14 extends Problem {   
/** 
* Creates a default MaF14 problem (60 variables and 3 objectives)
* @param solutionType The solution type must "Real".
*/
public static int nk14;
public static int sublen14[],len14[];
public MaF14(String solutionType) throws ClassNotFoundException {
this(solutionType, 60, 3);
} // MaF14   

/** 
* Creates a MaF14 problem instance
* @param numberOfVariables Number of variables
* @param numberOfObjectives Number of objective functions
* @param solutionType The solution type must "Real" or "BinaryReal". 
*/
public MaF14(String solutionType, 
           Integer numberOfVariables, 
		         Integer numberOfObjectives) {
numberOfVariables_  = numberOfVariables;
numberOfObjectives_ = numberOfObjectives;
numberOfConstraints_= 0;
problemName_        = "MaF14";
 
//evaluate sublen14,len14
nk14=2;
double[] c=new double[numberOfObjectives_];
c[0]=3.8*0.1*(1-0.1);
double sumc=0;
sumc+=c[0];
for(int i=1;i<numberOfObjectives_;i++)
{
	c[i]=3.8*c[i-1]*(1-c[i-1]);
	sumc+=c[i];
}

int[] sublen=new int[numberOfObjectives_];int[] len=new int[numberOfObjectives_+1];len[0]=0;
for(int i=0;i<numberOfObjectives_;i++)
{
	sublen[i]=(int) Math.ceil(Math.round(c[i]/sumc*numberOfVariables_)/(double)nk14);
	len[i+1]=len[i]+(nk14*sublen[i]);
}
sublen14=sublen;len14=len;
//re-update numberOfObjectives_,numberOfVariables_
numberOfVariables_=numberOfObjectives_-1+len[numberOfObjectives_];

lowerLimit_ = new double[numberOfVariables_];
upperLimit_ = new double[numberOfVariables_];        
for (int var = 0; var < numberOfVariables_-1; var++){
  lowerLimit_[var] = 0.0;
  upperLimit_[var] = 1.0;
} //for
for (int var = numberOfVariables_-1; var < numberOfVariables_; var++){
	  lowerLimit_[var] = 0.0;
	  upperLimit_[var] = 10.0;
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
//MaF14 , LSMOP3
public void evaluate(Solution solution) throws JMException {
	
	Variable[] gen  = solution.getDecisionVariables();   
	double [] x = new double[numberOfVariables_];
	double [] f = new double[numberOfObjectives_];
	    
	for (int i = 0; i < numberOfVariables_; i++)
	  x[i] = gen[i].getValue();
	
//	change x
	for(int i=numberOfObjectives_-1;i<numberOfVariables_;i++)		
		x[i]=(1+(i+1)/(double)numberOfVariables_)*x[i]-10*x[0];
//	evaluate eta,g
	double[] g=new double[numberOfObjectives_];
	double sub1;
	for(int i=0;i<numberOfObjectives_;i=i+2)
	{
		double[] tx=new double[sublen14[i]];
		sub1=0;
		for(int j=0;j<nk14;j++)
		{
			System.arraycopy(x, len14[i]+numberOfObjectives_-1+j*sublen14[i], tx, 0, sublen14[i]);
			sub1+=Rastrigin(tx);
		}
		g[i]=sub1/(nk14*sublen14[i]);
	}
	
	for(int i=1;i<numberOfObjectives_;i=i+2)
	{
		double[] tx=new double[sublen14[i]];
		sub1=0;
		for(int j=0;j<nk14;j++)
		{
			System.arraycopy(x, len14[i]+numberOfObjectives_-1+j*sublen14[i], tx, 0, sublen14[i]);
			sub1+=Rosenbrock(tx);
		}
		g[i]=sub1/(nk14*sublen14[i]);
	}
	
//	evaluate fm,fm-1,...,2,f1
	double subf1=1;
	f[numberOfObjectives_-1]=(1-x[0])*(1+g[numberOfObjectives_-1]);
	for(int i=numberOfObjectives_-2;i>0;i--)
	{
		subf1*=x[numberOfObjectives_-i-2];
		f[i]=subf1*(1-x[numberOfObjectives_-i-1])*(1+g[i]);
	}
	f[0]=subf1*x[numberOfObjectives_-2]*(1+g[0]);
	
	for (int i = 0; i < numberOfObjectives_; i++)
		  solution.setObjective(i,f[i]);	
	
}  


public static double Rastrigin(double[] x)
{
	double eta=0;
	for(int i=0;i<x.length;i++)
		eta+=(Math.pow(x[i], 2)-10*Math.cos(2*Math.PI*x[i])+10);
	return eta;
}
public static double Rosenbrock(double[] x)
{
	double eta=0;
	for(int i=0;i<x.length-1;i++)
		eta+=(100*Math.pow(Math.pow(x[i],2)-x[i+1],2)+Math.pow((x[i]-1),2));
	return eta;
}
}

