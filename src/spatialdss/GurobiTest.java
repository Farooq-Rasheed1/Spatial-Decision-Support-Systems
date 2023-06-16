package spatialdss;

String path = System.getProperty("java.library.path");
System.out.println("Java path: " + path);

import gurobi.*;

public class GurobiTest {
    public static void main(String[] args) {
        try {
            // Create a new Gurobi environment
            GRBEnv env = new GRBEnv();

            // Create a new Gurobi model
            GRBModel model = new GRBModel(env);

            // Create variables
            GRBVar x = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x");
            GRBVar y = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "y");

            // Set objective function
            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1.0, x);
            expr.addTerm(2.0, y);
            model.setObjective(expr, GRB.MAXIMIZE);

            // Add constraint
            expr = new GRBLinExpr();
            expr.addTerm(1.0, x);
            expr.addTerm(1.0, y);
            model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "c0");

            // Optimize the model
            model.optimize();

            // Print the solution
            System.out.println("Objective value: " + model.get(GRB.DoubleAttr.ObjVal));
            System.out.println("x = " + x.get(GRB.DoubleAttr.X));
            System.out.println("y = " + y.get(GRB.DoubleAttr.X));

            // Dispose of the model and environment
            model.dispose();
            env.dispose();
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }
}
