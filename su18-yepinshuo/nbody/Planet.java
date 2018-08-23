/**
 * Create a class called Planet, with 6 class attributes.
 */

public class Planet {

	/** Construct the 6 attributes. */
	double xxPos;
	double yyPos;
	double xxVel;
	double yyVel;
	double mass;
	String imgFileName;

	private static final double G = 6.67e-11;

	/** Constructor the first method. */
	public Planet(double xP, double yP, double xV, double yV, double m, String img) {
		xxPos = xP;
		yyPos = yP;
		xxVel = xV;
		yyVel = yV;
		mass = m;
		imgFileName = img;
	}

	/** Constructor with the second method. */
	public Planet(Planet p) {
		xxPos = p.xxPos;
		yyPos = p.yyPos;
		xxVel = p.xxVel;
		yyVel = p.yyVel;
		mass = p.mass;
		imgFileName = p.imgFileName;
	}

	/** calculate the distance between the two planets. */
	public double calcDistance(Planet p) {
		double xDifference = this.xxPos - p.xxPos;
		double yDifference = this.yyPos - p.yyPos;
		return Math.pow(xDifference * xDifference + yDifference * yDifference, 0.5);
	}

	/** Calculate the gravity force of the two planets. */
	public double calcForceExertedBy(Planet p) {
		double distance = calcDistance(p);
		return G * this.mass * p.mass / (distance * distance);
	}

	public double calcForceExertedByX(Planet p) {
		double xDifference = p.xxPos - this.xxPos;
		return calcForceExertedBy(p) * xDifference / calcDistance(p);
	}

	public double calcForceExertedByY(Planet p) {
		double yDifference = p.yyPos - this.yyPos;
		return calcForceExertedBy(p) * yDifference / calcDistance(p);
	}

	public double calcNetForceExertedByX(Planet[] p) {
		double sum = 0;
		for (int i = 0; i <= p.length - 1; i++){
			if (!(this.equals(p[i]))) {
				sum += this.calcForceExertedByX(p[i]);
			}
		}
		return sum;
	}

	public double calcNetForceExertedByY(Planet[] p) {
		double sum = 0;
		for (int i = 0; i <= p.length - 1; i++){
			if (!(this.equals(p[i]))) {
				sum += this.calcForceExertedByY(p[i]);
			}
		}
		return sum;
	}

	private boolean equals(Planet p) {
		return this.xxPos == p.xxPos && this.yyPos == p.yyPos;
	}

    /** Trying to update the position of the planet given the time and acceleration. */
    public void update(double dt, double fx, double fy) {
        double ax = fx / this.mass;
        double ay = fy / this.mass;
        this.xxVel += dt * ax;
        this.yyVel += dt * ay;
        this.xxPos += dt * this.xxVel;
        this.yyPos += dt * this.yyVel;
    }

    public void draw() {
        StdDraw.enableDoubleBuffering();
        StdDraw.picture(this.xxPos, this.yyPos, "./images/" + this.imgFileName);
        StdDraw.show();
    }
}