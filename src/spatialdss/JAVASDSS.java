package spatialdss;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class JAVASDSS {
    public static void main(String[] args) throws IOException, GRBException {
        // Read input file
        List<String[]> lines = new ArrayList<>();
        float[][] dMatrix = new float[22][94938];
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\faroo\\Downloads\\distances\\distances_fast.csv"))) {
            String line;
            int flag = 0;
            while ((line = br.readLine()) != null) {
                if (flag != 0) {
                    String[] splitted = line.split(",");
                    int i = Integer.parseInt(splitted[0]);
                    int j = Integer.parseInt(splitted[1]);
                    dMatrix[i - 1][j - 1] = Float.parseFloat(splitted[5]);
                } else {
                    flag++;
                }
            }
        }
        
        // Initialize environment and model
        GRBEnv env = new GRBEnv("logfile.log");
        GRBModel model = new GRBModel(env);
        model.set(GRB.StringAttr.ModelName, "location_allocation");

        // Define variables and parameters
        GRBVar[][] y = new GRBVar[22][94938];
        int[] cap = new int[22];
        double dmax = 10000;

        for (int i = 0; i < 22; i++)
            cap[i] = 5000;

        // Introduce variables
        for (int i = 0; i < 22; i++) {
            for (int j = 0; j < 94938; j++) {
                y[i][j] = model.addVar(0, 1, 1, GRB.INTEGER, "y_" + i + "_" + j);
            }
        }
        model.update();
        // Add constraints
        for (int j = 0; j < 94938; j++) {
            GRBLinExpr myExpr1 = new GRBLinExpr();
            for (int i = 0; i < 22; i++) {
                myExpr1.addTerm(1.0, y[i][j]);
            }
            model.addConstr(myExpr1, GRB.EQUAL, 1.0, "");
        }

        for (int i = 0; i < 22; i++) {
            GRBLinExpr myExpr2 = new GRBLinExpr();
            for (int j = 0; j < 94938; j++) {
                myExpr2.addTerm(1.0, y[i][j]);
            }
            model.addConstr(myExpr2, GRB.LESS_EQUAL, cap[i], "");
        }

        for (int i = 0; i < 22; i++) {
            for (int j = 0; j < 94938; j++) {
                GRBLinExpr myExpr3 = new GRBLinExpr();
                myExpr3.addTerm(dMatrix[i][j], y[i][j]);
                model.addConstr(myExpr3, GRB.LESS_EQUAL, dmax, "");
            }
        }
        model.update();
        long start = System.currentTimeMillis();
        // Set objective function
        GRBLinExpr myExpr4 = new GRBLinExpr();
        for (int i = 0; i < 22; i++) {
            for (int j = 0; j < 94938; j++) {
                myExpr4.addTerm(dMatrix[i][j], y[i][j]);
            }
        }
        model.setObjective(myExpr4, GRB.MINIMIZE);

        model.optimize();

        // Print solution
        
        model.optimize();
        long end = System.currentTimeMillis();

        double timeInSeconds = (end - start) / 1000.0;
        System.out.println("Running time: " + timeInSeconds);

        StringBuilder str = new StringBuilder();
        StringBuilder str2 = new StringBuilder();
        StringBuilder str3 = new StringBuilder();

        for (int i = 0; i < 22; i++) {
            str.append("Fire Station ").append(i).append(", Buildings:");
            str2.append("Fire Station ").append(i).append(", number of Buildings:");
            int n = 0;
            for (int j = 0; j < 94938; j++) {
                double val = model.getVarByName("y_" + i + "_" + j).get(GRB.DoubleAttr.X);
                if (val == 1.0) {
                    str.append(" ").append(j);
                    str3.append((i + 1)).append(", ").append((j + 1)).append("\n");
                    n++;
                }
            }
            str.append("\n");
            str2.append(" ").append(n).append("\n");
        }

        // Write assigned buildings to file
        try (BufferedWriter result3 = new BufferedWriter(new FileWriter("Result_x_5000_10000.csv"))) {
            result3.write(str3.toString());
        }
    }
}
