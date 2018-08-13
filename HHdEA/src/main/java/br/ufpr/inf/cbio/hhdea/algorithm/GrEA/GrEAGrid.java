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

import br.ufpr.inf.cbio.hhdea.algorithm.GrEA.solutionattribute.GCDAttribute;
import br.ufpr.inf.cbio.hhdea.algorithm.GrEA.solutionattribute.GCPDAttribute;
import br.ufpr.inf.cbio.hhdea.algorithm.GrEA.solutionattribute.GRAttribute;
import br.ufpr.inf.cbio.hhdea.algorithm.GrEA.solutionattribute.GridObjectiveAttribute;
import br.ufpr.inf.cbio.hhdea.algorithm.GrEA.solutionattribute.PDAttribute;
import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author Gian Fritsche <gian.fritsche@gmail.com>
 * @param <S>
 */
public class GrEAGrid<S extends Solution> {

    private List<S> pop;
    private int grid_div;
    private double[] grid_min;
    private double[] grid_max;
    private double[] grid_distance;

    private GridObjectiveAttribute gridObjective = new GridObjectiveAttribute();
    private GRAttribute gra = new GRAttribute();
    private GCPDAttribute gcpda = new GCPDAttribute();
    private GCDAttribute gcda = new GCDAttribute();
    private PDAttribute pda = new PDAttribute();

    public GrEAGrid(List<S> pop, int grid_div) {
        this.pop = pop;
        this.grid_div = grid_div;

        int i, j;
        int nobj = pop.get(0).getNumberOfObjectives();
        int popsize = pop.size();

        double[] ind_max = new double[nobj]; // record the maximum value in each
        // objective
        double[] ind_min = new double[nobj]; // record the minimun value in each
        // objective

        grid_distance = new double[nobj];
        grid_max = new double[nobj];
        grid_min = new double[nobj];

        for (j = 0; j < nobj; j++) {
            ind_min[j] = Double.MAX_VALUE;
            ind_max[j] = Double.MIN_VALUE;
            for (i = 0; i < popsize; i++) {
                if (pop.get(i).getObjective(j) < ind_min[j]) {
                    ind_min[j] = pop.get(i).getObjective(j);
                }
                if (pop.get(i).getObjective(j) > ind_max[j]) {
                    ind_max[j] = pop.get(i).getObjective(j);
                }
            }
            grid_distance[j] = (ind_max[j] - ind_min[j]) * (grid_div + 1)
                    / (grid_div * grid_div); // hyperbox width in each objective
            grid_min[j] = ind_min[j]
                    - (grid_distance[j] * grid_div - (ind_max[j] - ind_min[j]))
                    / 2; // grid lower boundary in each objective
            grid_max[j] = ind_max[j]
                    + (grid_distance[j] * grid_div - (ind_max[j] - ind_min[j]))
                    / 2; // grid upper boundary in each objective
        }
    }

    public void assign_GR_GCPD() {
        int i, j;
        int flag;
        double value;

        int popsize = pop.size();
        int nobj = pop.get(0).getNumberOfObjectives();

        for (i = 0; i < popsize; i++) {
            flag = 0;
            value = 0;
            for (j = 0; j < nobj; j++) {
                int val = (int) Math.floor((pop.get(i).getObjective(j) - grid_min[j]) / grid_distance[j]);
                gridObjective.setAttribute(pop.get(i), j, val);
                flag += val;
                value += Math.pow((pop.get(i).getObjective(j) - (grid_min[j]
                        + gridObjective.getAttribute(pop.get(i), j) * grid_distance[j])) / grid_distance[j], 2.0);
            }
            gra.setAttribute(pop.get(i), flag);
            gcpda.setAttribute(pop.get(i), Math.sqrt(value));
        }
    }

    public void assign_GCD() {
        int popsize = pop.size();
        int nobj = pop.get(0).getNumberOfObjectives();

        int i, j, k;
        int flag;

        for (i = 0; i < popsize; i++) {
            gcda.setAttribute(pop.get(i), 0);
        }

        for (i = 0; i < popsize; i++) {
            for (j = i + 1; j < popsize; j++) {
                flag = 0;
                for (k = 0; k < nobj; k++) {
                    flag += Math.abs(gridObjective.getAttribute(pop.get(i), k) - gridObjective.getAttribute(pop.get(j), k));
                }
                if (flag < nobj) {
                    int vi = ((int) gcda.getAttribute(pop.get(i))) + (nobj - flag);
                    gcda.setAttribute(pop.get(i), vi);
                    int vj = ((int) gcda.getAttribute(pop.get(j))) + (nobj - flag);
                    gcda.setAttribute(pop.get(j), vj);
                }
            }
        }
    }

    public void initialization() {
        assign_GR_GCPD();
        for (int i = 0; i < pop.size(); i++) {
            gcda.setAttribute(pop.get(i), 0);
        }
    }

    public void gcdCalculation(List<S> front, Solution q) {
        int nobj = q.getNumberOfObjectives();
        for (int i = 0; i < front.size(); i++) {
            Solution p = front.get(i);
            int gd = getGridDifference(p, q);

            if (gd < nobj) {
                int val = ((int) gcda.getAttribute(p)) + (nobj - gd);
                gcda.setAttribute(p, val);
            }
        }
    }

    public void grAdjustment(List<S> front, Solution q) {
        int nobj = q.getNumberOfObjectives();
        GridDominanceComparator gridComparator = new GridDominanceComparator();

        for (int i = 0; i < front.size(); i++) {
            pda.setAttribute(front.get(i), 0);
        }

        for (int i = 0; i < front.size(); i++) {
            Solution p = front.get(i);
            int gd = getGridDifference(p, q);
            int flag = gridComparator.compare(p, q);

            if (gd == 0) {
                int val = ((int) gra.getAttribute(p)) + (nobj + 2);
                gra.setAttribute(p, val);
            } else if (flag == 1) {
                int val = ((int) gra.getAttribute(p)) + nobj;
                gra.setAttribute(p, val);
            } else if (gd < nobj) {
                if (((int) pda.getAttribute(p)) < nobj - gd) {
                    pda.setAttribute(p, nobj - gd);
                }
                for (int j = 0; j < front.size(); j++) {
                    Solution r = front.get(j);
                    int mark1 = gridComparator.compare(r, p);
                    int mark2 = gridComparator.compare(r, q);
                    int dif = getGridDifference(r, q);

                    if (mark1 == 1 && mark2 != 1 && dif != 0) {
                        if (((int) pda.getAttribute(r)) < ((int) pda.getAttribute(p))) {
                            pda.setAttribute(r, ((int) pda.getAttribute(p)));
                        }
                    }
                }
            }
        }

        for (int i = 0; i < front.size(); i++) {
            Solution p = front.get(i);
            int gd = getGridDifference(p, q);
            int flag = gridComparator.compare(p, q);
            if (gd != 0 && flag != 1) {
                int val = ((int) gra.getAttribute(p)) + ((int) pda.getAttribute(p));
                gra.setAttribute(p, val);
            }
        }
    }

    public int findoutBest() {
        int index = 0;

        int popsize = pop.size();

        for (int i = 1; i < popsize; i++) {
            Solution s1 = pop.get(i);
            Solution s2 = pop.get(index);

            if (((int) gra.getAttribute(s1)) < ((int) gra.getAttribute(s2))) {
                index = i;
            } else if (((int) gra.getAttribute(s1)) == ((int) gra.getAttribute(s2))) {
                if (((int) gcda.getAttribute(s1)) < ((int) gcda.getAttribute(s2))) {
                    index = i;
                } else if (((int) gcda.getAttribute(s1)) == ((int) gcda.getAttribute(s2))) {
                    if (((double) gcpda.getAttribute(s1)) < ((double) gcpda.getAttribute(s2))) {
                        index = i;
                    }
                }
            }
        }
        return index;
    }

    int getGridDifference(Solution s1, Solution s2) {
        int nobj = s1.getNumberOfObjectives();
        int sum = 0;
        for (int j = 0; j < nobj; j++) {
            int v1 = gridObjective.getAttribute(s1, j);
            int v2 = gridObjective.getAttribute(s2, j);
            sum += Math.abs(v1 - v2);
        }
        return sum;
    }

}
