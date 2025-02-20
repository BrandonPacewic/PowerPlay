package org.firstinspires.ftc.teamcode.common.commandbase.auto;

import static java.lang.Math.PI;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.common.drive.drive.Drivetrain;
import org.firstinspires.ftc.teamcode.common.drive.drive.swerve.SwerveDrivetrain;
import org.firstinspires.ftc.teamcode.common.drive.geometry.Pose;
import org.firstinspires.ftc.teamcode.common.drive.localizer.Localizer;
import org.firstinspires.ftc.teamcode.common.hardware.Globals;

@Config
public class HoldPositionCommand extends CommandBase {
    public static double ALLOWED_TRANSLATIONAL_ERROR = 0.5;
    public static double PUSHED_THRESHOLD_TRANSLATIONAL = 1.5;
    public static double PUSHED_THRESHOLD_ROTATIONAL = Math.toRadians(2);
    public static double ALLOWED_HEADING_ERROR = Math.toRadians(1.5);

    public static double xP = 0.037;
    public static double xD = 0.06;
    public static double xI = 0.1;

    public static double yP = 0.037;
    public static double yD = 0.06;
    public static double yI = 0.1;

    public static double hP = 0.39;
    public static double hD = 0.1;
    public static double hI = 0.15;


    public static PIDFController xController = new PIDFController(xP, xI, xD, 0);
    public static PIDFController yController = new PIDFController(yP, yI, yD, 0);
    public static PIDFController hController = new PIDFController(hP, hI, hD, 0);


    public static double max_power = 1;

    Drivetrain drivetrain;
    Localizer localizer;
    Pose targetPose;
    ElapsedTime deadTimer;

    private final double ms;
    private ElapsedTime delayTimer;

    private final double v;

    public static boolean retargeting = false;

    public HoldPositionCommand(Drivetrain drivetrain, Localizer localizer, Pose targetPose, double ms, double voltage) {
        this.drivetrain = drivetrain;
        this.localizer = localizer;
        this.targetPose = targetPose;
        this.ms = ms;
        this.v = voltage;
    }

    @Override
    public void execute() {
        if (deadTimer == null) {
            deadTimer = new ElapsedTime();
        }
        Pose error = targetPose.subtract(localizer.getPos());
        retargeting = !retargeting && ((Math.hypot(error.x, error.y) > PUSHED_THRESHOLD_TRANSLATIONAL) || (Math.abs(error.heading) > PUSHED_THRESHOLD_ROTATIONAL));
        boolean reached = ((Math.hypot(error.x, error.y) < ALLOWED_TRANSLATIONAL_ERROR) && (Math.abs(error.heading) < ALLOWED_HEADING_ERROR));
        if (retargeting && reached) {
            retargeting = false;
        }

        if(Math.abs(xController.getPositionError()) < ALLOWED_TRANSLATIONAL_ERROR){
            xController.reset();
        }
        if(Math.abs(yController.getPositionError()) < ALLOWED_TRANSLATIONAL_ERROR){
            yController.reset();
        }
        if(Math.abs(hController.getPositionError()) < ALLOWED_HEADING_ERROR){
            hController.reset();
        }

        if(retargeting) {
            SwerveDrivetrain.minPow = 0.09;
            Pose powers = goToPosition(localizer.getPos(), targetPose);
            drivetrain.set(powers);
            Globals.yummypose = powers;
        }

        if (reached){
            SwerveDrivetrain drivetrain = (SwerveDrivetrain)this.drivetrain;
            SwerveDrivetrain.minPow = 0.07;
            drivetrain.frontLeftModule.setTargetRotation(PI / 4);
            drivetrain.frontRightModule.setTargetRotation(-PI / 4);
            drivetrain.backRightModule.setTargetRotation(PI / 4);
            drivetrain.backLeftModule.setTargetRotation(-PI / 4);
        }

        System.out.println("reached: " + reached);
        System.out.println("retargeting: " + retargeting);
    }

    @Override
    public boolean isFinished() {
        return delayTimer != null && delayTimer.milliseconds() > ms;
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.set(new Pose());
    }

    private static Pose relDistanceToTarget(Pose robot, Pose target) {
        return target.subtract(robot);
    }

    public Pose goToPosition(Pose robotPose, Pose targetPose) {
        Pose deltaPose = relDistanceToTarget(robotPose, targetPose);
        Pose powers = new Pose(
                xController.calculate(0, deltaPose.x),
                yController.calculate(0, deltaPose.y),
                hController.calculate(0, deltaPose.heading)
        );
        double x_rotated = powers.x * Math.cos(robotPose.heading) - powers.y * Math.sin(robotPose.heading);
        double y_rotated = powers.x * Math.sin(robotPose.heading) + powers.y * Math.cos(robotPose.heading);
        double x_power = -x_rotated < -max_power ? -max_power :
                Math.min(-x_rotated, max_power);
        double y_power = -y_rotated < -max_power ? -max_power :
                Math.min(-y_rotated, max_power);
        double heading_power = powers.heading;

        heading_power = Math.max(Math.min(0.5, heading_power), -0.5);

        return new Pose(-y_power / v * 12.5, x_power / v * 12.5, -heading_power / v * 12.5);
    }
}
