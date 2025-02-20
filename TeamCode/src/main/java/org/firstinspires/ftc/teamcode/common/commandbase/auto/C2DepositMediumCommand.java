package org.firstinspires.ftc.teamcode.common.commandbase.auto;

import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.command.WaitUntilCommand;

import org.firstinspires.ftc.teamcode.common.commandbase.newbot.LatchCommand;
import org.firstinspires.ftc.teamcode.common.commandbase.newbot.LiftCommand;
import org.firstinspires.ftc.teamcode.common.commandbase.newbot.PivotCommand;
import org.firstinspires.ftc.teamcode.common.commandbase.newbot.TurretCommand;
import org.firstinspires.ftc.teamcode.common.commandbase.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.common.commandbase.subsystem.LiftSubsystem;

public class C2DepositMediumCommand extends SequentialCommandGroup {
    public C2DepositMediumCommand(LiftSubsystem lift, IntakeSubsystem intake, long delay) {
        super(
                new LiftCommand(lift, LiftSubsystem.LiftState.MID),
                new WaitCommand(75),
                new PivotCommand(intake, IntakeSubsystem.PivotState.FLAT_AUTO),
                new TurretCommand(intake, IntakeSubsystem.TurretState.OUTWARDS),
                new LatchCommand(lift, LiftSubsystem.LatchState.LATCHED),
                new WaitUntilCommand(lift::isWithinTolerance),
                new WaitCommand(delay),
                new InstantCommand(() -> lift.update(LiftSubsystem.LatchState.UNLATCHED)),
                new WaitCommand(20),
                new InstantCommand(() -> lift.update(LiftSubsystem.LiftState.RETRACTED))
        );
    }
}
