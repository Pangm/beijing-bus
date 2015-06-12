package cn.pangm.beijingbus.common;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by pangm on 15-6-8.
 */
public class Bus {

    // All the usual stuff
    PVector location;
    PVector velocity;
    PVector acceleration;
    PApplet ctx;
    float r;
    float maxforce;    // Maximum steering force
    float maxspeed;    // Maximum speed
    int curCnt;        // the number of people

    // Constructor initialize all values
    public Bus(PApplet ctx, PVector l, float ms, float mf) {
        this.ctx = ctx;
        location = l.get();
        r = 4.0f;
        maxspeed = ms;
        maxforce = mf;
        acceleration = new PVector(0, 0);
        velocity = new PVector(maxspeed, 0);
        curCnt = (int) ctx.random(0, 50);
    }

    // Main "run" function
    public void run() {
        update();
        display();
    }


    // This function implements Craig Reynolds' path following algorithm
    // http://www.red3d.com/cwr/steer/PathFollow.html
    public void follow(Path p) {

        // Predict location 50 (arbitrary choice) frames ahead
        // This could be based on speed
        PVector predict = velocity.get();
        predict.normalize();
        predict.mult(50);
        PVector predictLoc = PVector.add(location, predict);

        // Now we must find the normal to the path from the predicted location
        // We look at the normal for each line segment and pick out the closest one

        PVector normal = null;
        PVector target = null;
        float worldRecord = 1000000;  // Start with a very high record distance that can easily be beaten

        // Loop through all points of the path
        for (int i = 0; i < p.points.size()-1; i++) {

            // Look at a line segment
            PVector a = p.points.get(i);
            PVector b = p.points.get(i+1);

            // Get the normal point to that line
            PVector normalPoint = getNormalPoint(predictLoc, a, b);
            // This only works because we know our path goes from left to right
            // We could have a more sophisticated test to tell if the point is in the line segment or not
            if (normalPoint.x < a.x || normalPoint.x > b.x) {
                // This is something of a hacky solution, but if it's not within the line segment
                // consider the normal to just be the end of the line segment (point b)
                normalPoint = b.get();
            }

            // How far away are we from the path?
            float distance = PVector.dist(predictLoc, normalPoint);
            // Did we beat the record and find the closest line segment?
            if (distance < worldRecord) {
                worldRecord = distance;
                // If so the target we want to steer towards is the normal
                normal = normalPoint;

                // Look at the direction of the line segment so we can seek a little bit ahead of the normal
                PVector dir = PVector.sub(b, a);
                dir.normalize();
                // This is an oversimplification
                // Should be based on distance to path & velocity
                dir.mult(10);
                target = normalPoint.get();
                target.add(dir);
            }
        }

        // Only if the distance is greater than the path's radius do we bother to steer
        if (worldRecord > p.radius) {
            seek(target);
        }


        // Draw the debugging stuff
        if (true) {
            // Draw predicted future location
            ctx.strokeWeight(1);
            ctx.stroke(0);
            ctx.fill(0);
            ctx.line(location.x, location.y, predictLoc.x, predictLoc.y);
            ctx.ellipse(predictLoc.x, predictLoc.y, 4, 4);

            // Draw normal location
            ctx.stroke(0);
            ctx.fill(0);
            ctx.ellipse(normal.x, normal.y, 4, 4);
            // Draw actual target (red if steering towards it)
            ctx.line(predictLoc.x, predictLoc.y, normal.x, normal.y);
            if (worldRecord > p.radius) ctx.fill(255, 0, 0);
            ctx.noStroke();
            ctx.ellipse(target.x, target.y, 8, 8);
        }
    }


    // A function to get the normal point from a point (p) to a line segment (a-b)
    // This function could be optimized to make fewer new Vector objects
    PVector getNormalPoint(PVector p, PVector a, PVector b) {
        // Vector from a to p
        PVector ap = PVector.sub(p, a);
        // Vector from a to b
        PVector ab = PVector.sub(b, a);
        ab.normalize(); // Normalize the line
        // Project vector "diff" onto line by using the dot product
        ab.mult(ap.dot(ab));
        PVector normalPoint = PVector.add(a, ab);
        return normalPoint;
    }


    // Method to update location
    void update() {
        // Update velocity
        velocity.add(acceleration);
        // Limit speed
        velocity.limit(maxspeed);
        location.add(velocity);
        // Reset accelertion to 0 each cycle
        acceleration.mult(0);
    }

    void applyForce(PVector force) {
        // We could add mass here if we want A = F / M
        acceleration.add(force);
    }


    // A method that calculates and applies a steering force towards a target
    // STEER = DESIRED MINUS VELOCITY
    void seek(PVector target) {
        PVector desired = PVector.sub(target, location);  // A vector pointing from the location to the target

        // If the magnitude of desired equals 0, skip out of here
        // (We could optimize this to check if x and y are 0 to avoid mag() square root
        if (desired.mag() == 0) return;

        // Normalize desired and scale to maximum speed
        desired.normalize();
        desired.mult(maxspeed);
        // Steering = Desired minus Velocity
        PVector steer = PVector.sub(desired, velocity);
        steer.limit(maxforce);  // Limit to maximum steering force

        applyForce(steer);
    }

    public void display() {
        // Draw a triangle rotated in the direction of velocity
        float theta = velocity.heading2D() + ctx.radians(90);
        ctx.fill(240, 240, 240);
        ctx.stroke(240, 240, 240);
        ctx.pushMatrix();
        ctx.translate(location.x, location.y);
        ctx.rotate(theta);
        ctx.beginShape(PApplet.TRIANGLES);
        ctx.vertex(0, -r * 2);
        ctx.vertex(-r, r * 2);
        ctx.vertex(r, r * 2);
        ctx.endShape();
        ctx.popMatrix();
    }

    // Wraparound
    public void borders(Path p) {
        if (location.x > p.getEnd().x + r) {
            location.x = p.getStart().x - r;
            location.y = p.getStart().y + (location.y-p.getEnd().y);
        }
    }
}

