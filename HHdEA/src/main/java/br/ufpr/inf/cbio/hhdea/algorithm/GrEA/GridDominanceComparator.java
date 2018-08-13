/*
 * Copyright (C) 2018 Gian Fritsche <gian.fritsche@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.ufpr.inf.cbio.hhdea.algorithm.GrEA;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ConstraintViolationComparator;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;

/**
 *
 * @author Gian Fritsche <gian.fritsche@gmail.com>
 */
public class GridDominanceComparator implements Comparator {

    ConstraintViolationComparator violationConstraintComparator_;

    public GridDominanceComparator() {
        violationConstraintComparator_ = new OverallConstraintViolationComparator();
    }

    public GridDominanceComparator(ConstraintViolationComparator comparator) {
        violationConstraintComparator_ = comparator;
    }

    public int compare(Object object1, Object object2) {
        if (object1 == null) {
            return 1;
        } else if (object2 == null) {
            return -1;
        }

        Solution solution1 = (Solution) object1;
        Solution solution2 = (Solution) object2;

        int dominate1; // dominate1 indicates if some objective of solution1
        // dominates the same objective in solution2. dominate2
        int dominate2; // is the complementary of dominate1.

        dominate1 = 0;
        dominate2 = 0;

        int flag = 0;
        
        // Test to determine whether at least a solution violates some constraint
        int result = violationConstraintComparator_.compare(solution1, solution2);

        if (result != 0) {
            return result;
        }

        double value1, value2;
        for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
            value1 = solution1.getGridCorrd(i);
            value2 = solution2.getGridCorrd(i);
            if (value1 < value2) {
                flag = -1;
            } else if (value1 > value2) {
                flag = 1;
            }// if

            if (flag == -1) {
                dominate1 = 1;
            }

            if (flag == 1) {
                dominate2 = 1;
            }

        }//for

        if (dominate1 == dominate2) {
            return 0;  		//No one dominate the other
        }
        if (dominate1 == 1) {
            return -1;		// Solution1 grid-dominate Solution2
        }
        return 1;			// Solution2 grid-dominate Solution1

    } // compare

}
