package org.firstinspires.ftc.teamcode.common.commandbase.command.subsystemcommands.subsystem;

import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;

import org.firstinspires.ftc.teamcode.common.hardware.Robot;
import org.firstinspires.ftc.teamcode.common.subsystem.IntakeSubsystem;

public class ClawCommand extends SequentialCommandGroup {
    public ClawCommand(Robot robot, IntakeSubsystem.ClawState state) {
        super(
                new InstantCommand(() -> robot.intake.update(state))
        );
    }
}
