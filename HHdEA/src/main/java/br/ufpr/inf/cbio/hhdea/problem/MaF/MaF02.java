package jmetal.problems.MaF;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/**
 * Class representing problem MaF02
 */
public class MaF02 extends Problem {

    /**
     * Creates a default MaF02 problem (7 variables and 3 objectives)
     *
     * @param solutionType The solution type must "Real" or "BinaryReal".
     */
    public static int const2;

    public MaF02(String solutionType) throws ClassNotFoundException {
        this(solutionType, 12, 3);
    } // MaF02   

    /**
     * Creates a MaF02 problem instance
     *
     * @param numberOfVariables Number of variables
     * @param numberOfObjectives Number of objective functions
     * @param solutionType The solution type must "Real" or "BinaryReal".
     */
    public MaF02(String solutionType,
            Integer numberOfVariables,
            Integer numberOfObjectives) {
        numberOfVariables_ = numberOfVariables;
        numberOfObjectives_ = numberOfObjectives;
        const2 = (int) Math.floor((numberOfVariables_ - numberOfObjectives_ + 1) / (double) numberOfObjectives_);
        numberOfConstraints_ = 0;
        problemName_ = "MaF02";

        lowerLimit_ = new double[numberOfVariables_];
        upperLimit_ = new double[numberOfVariables_];
        for (int var = 0; var < numberOfVariables; var++) {
            lowerLimit_[var] = 0.0;
            upperLimit_[var] = 1.0;
        } //for
        if (solutionType.compareTo("BinaryReal") == 0) {
            solutionType_ = new BinaryRealSolutionType(this);
        } else if (solutionType.compareTo("Real") == 0) {
            solutionType_ = new RealSolutionType(this);
        } else {
            System.out.println("Error: solution type " + solutionType + " invalid");
            System.exit(-1);
        }
    }

    /**
     * Evaluates a solution
     *
     * @param solution The solution to evaluate
     * @throws JMException
     */
//MaF02 , DTLZ2BZ
    public void evaluate(Solution solution) throws JMException {

        Variable[] gen = solution.getDecisionVariables();
        double[] x = new double[numberOfVariables_];
        double[] f = new double[numberOfObjectives_];

        for (int i = 0; i < numberOfVariables_; i++) {
            x[i] = gen[i].getValue();
        }

        double[] g = new double[numberOfObjectives_];
        double[] thet = new double[numberOfObjectives_ - 1];
        int lb, ub;
//	evaluate g,thet
        for (int i = 0; i < numberOfObjectives_ - 1; i++) {
            g[i] = 0;
            lb = numberOfObjectives_ + i * const2;
            ub = numberOfObjectives_ + (i + 1) * const2 - 1;
            for (int j = lb - 1; j < ub; j++) {
                g[i] += Math.pow(x[j] / 2 - 0.25, 2);
            }
            thet[i] = Math.PI / 2 * (x[i] / 2 + 0.25);
        }
        lb = numberOfObjectives_ + (numberOfObjectives_ - 1) * const2;
        ub = numberOfVariables_;
        for (int j = lb - 1; j < ub; j++) {
            g[numberOfObjectives_ - 1] += Math.pow(x[j] / 2 - 0.25, 2);
        }
//	evaluate fm,fm-1,...,2,f1
        f[numberOfObjectives_ - 1] = Math.sin(thet[0]) * (1 + g[numberOfObjectives_ - 1]);
        double subf1 = 1, subf2, subf3;
//	fi=cos(thet1)cos(thet2)...cos(thet[m-i])*sin(thet(m-i+1))*(1+g[i]),fi=subf1*subf2*subf3
        for (int i = numberOfObjectives_ - 2; i > 0; i--) {
            subf1 *= Math.cos(thet[numberOfObjectives_ - i - 2]);
            f[i] = subf1 * Math.sin(thet[numberOfObjectives_ - i - 1]) * (1 + g[i]);
        }
        f[0] = subf1 * Math.cos(thet[numberOfObjectives_ - 2]) * (1 + g[0]);

        for (int i = 0; i < numberOfObjectives_; i++) {
            solution.setObjective(i, f[i]);
        }
    }

}
