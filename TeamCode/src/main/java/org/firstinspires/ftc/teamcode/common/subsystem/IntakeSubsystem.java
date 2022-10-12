package org.firstinspires.ftc.teamcode.common.subsystem;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.common.purepursuit.geometry.profiling.MotionProfile;
import org.firstinspires.ftc.teamcode.common.purepursuit.geometry.profiling.TrapezoidalMotionProfile;

@Config
public class IntakeSubsystem extends SubsystemBase {
    private final MotorEx extension;
    private final Servo barLeft, barRight;
    private final Servo claw, turret;

    private MotionProfile profile;
    private final ElapsedTime timer;
    private final PIDController controller;

    public static double P = 0.0;
    public static double I = 0.0;
    public static double D = 0.0;

    public static double distance = 0.0;
    public static double maxV = 60;
    public static double maxA = 30;

    public static int intake_out_pos = 100;
    public static int intake_in_pos = 0;

    public static double claw_open_pos = 10;
    public static double claw_closed_pos = 5;

    public static double forebar_extended = 0.4;
    public static double forebar_middle = 0.25;
    public static double forebar_retracted = 0.1;

    public static final double FOREBAR_LENGTH = 9.842;

    public IntakeSubsystem(MotorEx extension, Servo barLeft, Servo barRight, Servo claw, Servo turret) {
        this.extension = extension;
        this.barLeft = barLeft;
        this.barRight = barRight;
        this.claw = claw;
        this.turret = turret;

        this.profile = new TrapezoidalMotionProfile(maxV, maxA, distance);
        this.timer = new ElapsedTime();
        this.controller = new PIDController(P, I, D);
    }

    public void loop() {
        double target = profile.update(timer.time())[0];
        double power = controller.calculate(extension.getCurrentPosition(), target);
        extension.set(power);
    }

    public void setMotionProfile(MotionProfile profile) {
        this.profile = profile;
    }

    public void setExtension(int pos) {
        this.distance = pos;
    }

    public void setForebar(double pos) {
        // TODO set forebar
    }

    public void setClaw(double position) {
        claw.setPosition(position);
    }

    public int getExtension() {
        return extension.getCurrentPosition();
    }

    public void extensionOut() {
        extension.setTargetPosition(intake_out_pos);
    }

    public void extensionIn() {
        extension.setTargetPosition(intake_in_pos);
    }

    public void closeClaw() {
        claw.setPosition(claw_closed_pos);
    }

    public void openClaw() {
        claw.setPosition(claw_open_pos);
    }

    public void extendForebar() {
        barLeft.setPosition(forebar_extended);
        barRight.setPosition(forebar_extended);
    }

    public void closeForebar() {
        barLeft.setPosition(forebar_retracted);
        barRight.setPosition(forebar_retracted);
    }

    public void resetTimer() {
        timer.reset();
    }

    public void setPID(double P, double I, double D) {
        controller.setPID(P, I, D);
    }
}
