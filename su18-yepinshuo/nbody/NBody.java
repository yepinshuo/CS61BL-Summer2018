/**
 * Create a class called NBody, with no class constructor.
 */

public class NBody {
    /** Construct the read radius method. */
    public static double readRadius(String filename) {
        In in = new In(filename);
        int NumOfPlanets = in.readInt();
        double radius = in.readDouble();
        return radius;
    }

    public static Planet[] readPlanets(String filename) {
        In in = new In(filename);
        int NumOfPlanets = in.readInt();
        double radius = in.readDouble();

        Planet[] ListOfPlanets = new Planet[NumOfPlanets];

        for (int i = 0; i <= NumOfPlanets - 1; i++) {
            double xP = in.readDouble();
            double yP = in.readDouble();
            double xV = in.readDouble();
            double yV = in.readDouble();
            double m = in.readDouble();
            String img = in.readString();

            ListOfPlanets[i] = new Planet(xP, yP, xV, yV, m, img);
        }

        return ListOfPlanets;
    }

    public static void main(String[] args) {
        /** Create all the input. */
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        String filename = args[2];
        double radius = readRadius(filename);
        Planet[] list = readPlanets(filename);

        /** Draw the background of the universe. */
        StdDraw.enableDoubleBuffering();
        StdDraw.setScale(-radius, radius);
        StdDraw.picture(0, 0, "./images/starfield.jpg");
        StdDraw.show();

        /** Draw the Planets. */
        for (int i = 0; i <= list.length - 1; i++) {
            list[i].draw();
        }

        /** Creating the animation. */
        StdDraw.enableDoubleBuffering();
        double time = 0;
        for (time = 0; time <= T; time += dt) {
            double[] xForces = new double[list.length];
            double[] yForces = new double[list.length];

            for (int i = 0; i <= list.length - 1; i++){
                xForces[i] = list[i].calcNetForceExertedByX(list);
                yForces[i] = list[i].calcNetForceExertedByY(list);
            }

            for (int i = 0; i <= list.length - 1; i++) {
                list[i].update(dt , xForces[i], yForces[i]);
            }

            StdDraw.enableDoubleBuffering();
            StdDraw.setScale(-radius, radius);
            StdDraw.picture(0, 0, "./images/starfield.jpg");
            StdDraw.show();

            for (int i = 0; i <= list.length - 1; i++) {
                list[i].draw();
            }

            StdDraw.pause(10);
        }

        StdOut.printf("%d\n", list.length);
        StdOut.printf("%.2e\n", radius);
        for (int i = 0; i < list.length; i += 1) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                    list[i].xxPos, list[i].yyPos, list[i].xxVel,
                    list[i].yyVel, list[i].mass, list[i].imgFileName);
        }
    }
}
