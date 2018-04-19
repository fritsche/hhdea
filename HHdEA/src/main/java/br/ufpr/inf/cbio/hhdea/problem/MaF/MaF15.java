package br.ufpr.inf.cbio.hhdea.problem.MaF;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * Class representing problem MaF15
 */
public class MaF15 extends AbstractDoubleProblem {

    /**
     * Creates a default MaF15 problem (60 variables and 3 objectives)
     *
     * @param solutionType The solution type must "Real".
     */
    public static int nk15;
    public static int sublen15[], len15[];

    public MaF15(String solutionType) throws ClassNotFoundException {
        this(solutionType, 60, 3);
    } // MaF15   

    /**
     * Creates a MaF15 problem instance
     *
     * @param numberOfVariables Number of variables
     * @param numberOfObjectives Number of objective functions
     * @param solutionType The solution type must "Real" or "BinaryReal".
     */
    public MaF15(String solutionType,
            Integer numberOfVariables,
            Integer numberOfObjectives) {
        numberOfVariables_ = numberOfVariables;
        numberOfObjectives_ = numberOfObjectives;
        numberOfConstraints_ = 0;
        problemName_ = "MaF15";

//evaluate sublen15,len15
        nk15 = 2;
        double[] c = new double[numberOfObjectives_];
        c[0] = 3.8 * 0.1 * (1 - 0.1);
        double sumc = 0;
        sumc += c[0];
        for (int i = 1; i < numberOfObjectives_; i++) {
            c[i] = 3.8 * c[i - 1] * (1 - c[i - 1]);
            sumc += c[i];
        }

        int[] sublen = new int[numberOfObjectives_];
        int[] len = new int[numberOfObjectives_ + 1];
        len[0] = 0;
        for (int i = 0; i < numberOfObjectives_; i++) {
            sublen[i] = (int) Math.ceil(Math.round(c[i] / sumc * numberOfVariables_) / (double) nk15);
            len[i + 1] = len[i] + (nk15 * sublen[i]);
        }
        sublen15 = sublen;
        len15 = len;
//re-update numberOfObjectives_,numberOfVariables_
        numberOfVariables_ = numberOfObjectives_ - 1 + len[numberOfObjectives_];

        lowerLimit_ = new double[numberOfVariables_];
        upperLimit_ = new double[numberOfVariables_];
        for (int var = 0; var < numberOfVariables_ - 1; var++) {
            lowerLimit_[var] = 0.0;
            upperLimit_[var] = 1.0;
        } //for
        for (int var = numberOfVariables_ - 1; var < numberOfVariables_; var++) {
            lowerLimit_[var] = 0.0;
            upperLimit_[var] = 10.0;
        } //for

        if (solutionType.compareTo("Real") == 0) {
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
//MaF15 , inverted LSMOP8
    public void evaluate(Solution solution) throws JMException {

        Variable[] gen = solution.getDecisionVariables();
        double[] x = new double[numberOfVariables_];
        double[] f = new double[numberOfObjectives_];

        for (int i = 0; i < numberOfVariables_; i++) {
            x[i] = gen[i].getValue();
        }

//	change x
        for (int i = numberOfObjectives_ - 1; i < numberOfVariables_; i++) {
            x[i] = (1 + Math.cos((i + 1) / (double) numberOfVariables_ * Math.PI / 2)) * x[i] - 10 * x[0];
        }
//	evaluate eta,g
        double[] g = new double[numberOfObjectives_];
        double sub1;
        for (int i = 0; i < numberOfObjectives_; i = i + 2) {
            double[] tx = new double[sublen15[i]];
            sub1 = 0;
            for (int j = 0; j < nk15; j++) {
                System.arraycopy(x, len15[i] + numberOfObjectives_ - 1 + j * sublen15[i], tx, 0, sublen15[i]);
                sub1 += Griewank(tx);
            }
            g[i] = sub1 / (nk15 * sublen15[i]);
        }

        for (int i = 1; i < numberOfObjectives_; i = i + 2) {
            double[] tx = new double[sublen15[i]];
            sub1 = 0;
            for (int j = 0; j < nk15; j++) {
                System.arraycopy(x, len15[i] + numberOfObjectives_ - 1 + j * sublen15[i], tx, 0, sublen15[i]);
                sub1 += Sphere(tx);
            }
            g[i] = sub1 / (nk15 * sublen15[i]);
        }

//	evaluate fm,fm-1,...,2,f1
        double subf1 = 1;
        f[numberOfObjectives_ - 1] = (1 - Math.sin(Math.PI * x[0] / 2)) * (1 + g[numberOfObjectives_ - 1]);
        for (int i = numberOfObjectives_ - 2; i > 0; i--) {
            subf1 *= Math.cos(Math.PI * x[numberOfObjectives_ - i - 2] / 2);
            f[i] = (1 - subf1 * Math.sin(Math.PI * x[numberOfObjectives_ - i - 1] / 2)) * (1 + g[i] + g[i + 1]);
        }
        f[0] = (1 - subf1 * Math.cos(Math.PI * x[numberOfObjectives_ - 2] / 2)) * (1 + g[0] + g[1]);

        for (int i = 0; i < numberOfObjectives_; i++) {
            solution.setObjective(i, f[i]);
        }

    }

    public static double Griewank(double[] x) {
        double eta = 0, sub1 = 0, sub2 = 1;
        for (int i = 0; i < x.length; i++) {
            sub1 += (Math.pow(x[i], 2) / 4000);
            sub2 *= (Math.cos(x[i] / Math.sqrt(i + 1)));
        }
        eta = sub1 - sub2 + 1;
        return eta;
    }

    public static double Sphere(double[] x) {
        double eta = 0;
        for (int i = 0; i < x.length; i++) {
            eta += Math.pow(x[i], 2);
        }
        return eta;
    }
}
