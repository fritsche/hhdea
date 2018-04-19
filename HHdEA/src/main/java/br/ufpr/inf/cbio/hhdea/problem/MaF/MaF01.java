package br.ufpr.inf.cbio.hhdea.problem.MaF;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * Class representing problem MaF01
 */
public class MaF01 extends AbstractDoubleProblem {

    private final String name;

    /**
     * Creates a default MaF01 problem (7 variables and 3 objectives)
     *
     * @param solutionType The solution type must "Real" or "BinaryReal".
     */
    public MaF01(String solutionType) {
        this(solutionType, 12, 3);
    } // MaF01   

    /**
     * Creates a MaF01 problem instance
     *
     * @param numberOfVariables Number of variables
     * @param numberOfObjectives Number of objective functions
     * @param solutionType The solution type must "Real" or "BinaryReal".
     */
    public MaF01(String solutionType,
            Integer numberOfVariables,
            Integer numberOfObjectives) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setNumberOfConstraints(0);
        this.name = "MaF01";

        List<Double> lower = new ArrayList<>(getNumberOfVariables()), upper = new ArrayList<>(getNumberOfVariables());

        for (int var = 0; var < numberOfVariables; var++) {
            lower.add(0.0);
            upper.add(1.0);
        } //for

        setLowerLimit(lower);
        setUpperLimit(upper);

    }

    /**
     * Evaluates a solution
     *
     * @param solution The solution to evaluate
     */
//MaF01 , modified inverted DTLZ1
    @Override
    public void evaluate(DoubleSolution solution) {

        int numberOfVariables_ = solution.getNumberOfVariables();
        int numberOfObjectives_ = solution.getNumberOfObjectives();

        double[] x = new double[numberOfVariables_];
        double[] f = new double[numberOfObjectives_];

        for (int i = 0; i < numberOfVariables_; i++) {
            x[i] = solution.getVariableValue(i);
        }

        double g = 0, subf1 = 1, subf3;
//		evaluate g(xm)
        for (int j = numberOfObjectives_ - 1; j < numberOfVariables_; j++) {
            g += (Math.pow(x[j] - 0.5, 2));
        }
        subf3 = 1 + g;
//		evaluate objectives
//		fi=(1+g)(1-(x1x2...x[m-i])(1-x[m-i+1])),fi=subf3*(1-subf1*subf2)
//		evaluate fm,f2~m-1,f1,
        f[numberOfObjectives_ - 1] = x[0] * subf3;
        for (int i = numberOfObjectives_ - 2; i > 0; i--) {
            subf1 *= x[numberOfObjectives_ - i - 2];
            f[i] = subf3 * (1 - subf1 * (1 - x[numberOfObjectives_ - i - 1]));
        }
        f[0] = (1 - subf1 * x[numberOfObjectives_ - 2]) * subf3;

        for (int i = 0; i < numberOfObjectives_; i++) {
            solution.setObjective(i, f[i]);
        }
    }

}
