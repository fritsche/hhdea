package jmetal.problems.MaF;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
* Class representing problem MaF08 
*/
public class MaF08 extends Problem {   
/** 
* Creates a default MaF08 problem (2 variables and 10 objectives)
* @param solutionType The solution type must "Real".
*/
	public static double const8[][]; 
public MaF08(String solutionType) throws ClassNotFoundException {
this(solutionType, 2, 10);
} // MaF08   

/** 
* Creates a MaF08 problem instance
* @param numberOfVariables Number of variables
* @param numberOfObjectives Number of objective functions
* @param solutionType The solution type must "Real". 
*/
public MaF08(String solutionType, 
           Integer numberOfVariables, 
		         Integer numberOfObjectives) {
numberOfVariables_  = numberOfVariables;
numberOfObjectives_ = numberOfObjectives;
numberOfVariables_=2;
numberOfConstraints_= 0;
problemName_        = "MaF08";
//other constants during the whole process once M&D are defined
double r=1;
const8=polygonpoints(numberOfObjectives_,r); 

lowerLimit_ = new double[numberOfVariables_];
upperLimit_ = new double[numberOfVariables_];        
for (int var = 0; var < numberOfVariables_; var++){
  lowerLimit_[var] = -10000;
  upperLimit_[var] = 10000;
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
//MaF08 , multi-point distance minimization problem
public void evaluate(Solution solution) throws JMException {
	
	Variable[] gen  = solution.getDecisionVariables();   
	double [] x = new double[numberOfVariables_];
	double [] f = new double[numberOfObjectives_];
	    
	for (int i = 0; i < numberOfVariables_; i++)
	  x[i] = gen[i].getValue();
	
//	evaluate f
	for(int i=0;i<numberOfObjectives_;i++)
		f[i]=Math.sqrt(Math.pow(const8[i][0]-x[0], 2)+Math.pow(const8[i][1]-x[1], 2));
	
	for (int i = 0; i < numberOfObjectives_; i++)
		  solution.setObjective(i,f[i]);	
	
}

public static double[][] polygonpoints(int m,double r)
{
	double[] startp=new double[2];
	startp[0]=0;startp[1]=1;
	double[][] p1=new double[m][2];
	double[][] p=new double[m][2];
	p1[0]=startp;
//	vertexes with the number of edges(m),start vertex(startp),radius(r)
	for (int i = 1; i < m; i++) 
		p1[i] = nextPoint(2*Math.PI/m*i,startp,r);
	for(int i=0;i<m;i++)
		p[i]=p1[m-i-1];
	return p;
}
public static double[] nextPoint(double arc,double[] startp,double r) 
{// arc is radians£¬evaluation the next vertex with arc and r
    double[] p=new double[2];
    p[0] = startp[0] - r * Math.sin(arc);
    p[1] = r * Math.cos(arc);
    return p;
}

}

