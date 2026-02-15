package org.nknsd.teamcode.components.handlers.artifact;


import com.qualcomm.robotcore.util.RobotLog;

import org.nknsd.teamcode.components.handlers.artifact.states.IntakeBallState;
import org.nknsd.teamcode.components.handlers.artifact.states.IntakeStartState;
import org.nknsd.teamcode.components.handlers.artifact.states.LaunchAllStartState;
import org.nknsd.teamcode.components.handlers.artifact.states.LaunchBCStartState;
import org.nknsd.teamcode.components.handlers.artifact.states.ScanStartState;
import org.nknsd.teamcode.components.handlers.color.BallColor;
import org.nknsd.teamcode.components.handlers.launch.LaunchSystem;
import org.nknsd.teamcode.components.utility.StateMachine;


public class ArtifactSystem {
    private MicrowaveScoopHandler microwaveScoopHandler;
    private SlotTracker slotTracker;
    private LaunchSystem launchSystem;
    private boolean isLaunching;
    private boolean isScanning;

    public void setIntakeState(StateMachine.State intakeState) {
        this.intakeState = intakeState;
    }

    public void setIsLaunching(boolean isLaunching) {
        this.isLaunching = isLaunching;
    }


    public void setScanState(StateMachine.State scanState) {
        this.scanState = scanState;
    }

    public void setIsScanning(boolean scanning){
        isScanning = scanning;
        RobotLog.v("changing scanning mode to " + scanning);
    }

    private StateMachine.State intakeState;
    private StateMachine.State launchState;
    private StateMachine.State scanState;

    /**
     * checks if any artifact states are currently running
     *
     * @return boolean showing if none of the artifact states are running
     */
    public boolean isReady() {
        if (intakeState != null && intakeState.isRunning()) {
            return false;
        }
        if (isLaunching) {
            return false;
        }
        if (isScanning) {
            return false;
        }
        return true;
    }


    /**
     * gets the contents of the microwave
     *
     * @return BallColor array of the BallColor in each of the three slots
     */
    public BallColor[] getContents() {
        BallColor[] colors = new BallColor[3];
        for (int i = 0; i < 3; i++) {
            colors[i] = slotTracker.getSlotColor(i);
        }
        return colors;
    }

    /**
     * Scans all three slots to check the color
     */
    public boolean scanAll() {
        if (isReady()) {
            RobotLog.v("Starting scan alllll");
            StateMachine.INSTANCE.startAnonymous(new ScanStartState(this, microwaveScoopHandler, slotTracker, false));
            return true;
        } else {
            return false;
        }
    }

    public boolean scanWithOverride() {
        if (isReady()) {
            StateMachine.INSTANCE.startAnonymous(new ScanStartState(this, microwaveScoopHandler, slotTracker, true));
            return true;
        } else {
            return false;
        }
    }

    /**
     * launches a ball of a given BallColor that is in a slot
     *
     * @param color The BallColor that is supposed to be launched
     * @return if the specified BallColor was found or not
     */
    public boolean launchColor(BallColor color) {
        if (isReady()) {
            for (int i = 0; i < 3; i++) {
                if (slotTracker.getSlotColor(i) == color) {
                    setIsLaunching(true);
                    MicrowavePositions microwavePos = MicrowavePositions.values()[i + 3];
                    StateMachine.INSTANCE.startAnonymous(new LaunchBCStartState(microwaveScoopHandler, slotTracker, this, microwavePos, i));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Launches all three slots of the microwave without care for if a slot is empty
     */
    public boolean launchAll() {
        return launchAll(new int[]{0, 1, 2});
    }

    public boolean launchAll(BallColor[] colorOrder
    ) {
        int[] orderToLaunch = new int[]{4,4,4};
        BallColor[] slotColors = new BallColor[3];
        for (int i = 0; i < 3; i++) {
            slotColors[i] = slotTracker.getSlotColor(i);
        }

//        assigns order of slots based on colors
        for (int i = 0; i < 3; i++) {
            for (int c = 0; c < 3; c++) {
                if (colorOrder[i] == slotColors[c]) {
                    orderToLaunch[i] = c;
                    slotColors[c] = null;
                    break;
                }
            }
        }

//        if a slot could not be matched to the pattern's color, fill with whatever is left
        for (int e = 0; e < 3; e++) {
            if (orderToLaunch[e] == 4) {
                for (int s = 0; s < 3; s++) {
                    if (slotColors[s] != null) {
                        orderToLaunch[e] = s;
                        slotColors[s] = null;
                        break;
                    }
                }
            }
        }

        RobotLog.v("launch order! 0: " + orderToLaunch[0] + ", 1: " + orderToLaunch[1] + ", 2:" + orderToLaunch[2]);
        return launchAll(orderToLaunch);
    }

    public boolean launchAll(int[] slotOrder) {
        if (isReady()) {
            setIsLaunching(true);
            StateMachine.INSTANCE.startAnonymous(new LaunchAllStartState(slotOrder, microwaveScoopHandler, slotTracker, this, launchSystem));
            return true;
        }
        return false;
    }

    /**
     * Intakes balls until it has a BallColor for each slot
     */
    public boolean intakeUntilFull() {
        if (isReady()) {
            IntakeBallState.killIntake = false;
            StateMachine.INSTANCE.startAnonymous(new IntakeStartState(microwaveScoopHandler, slotTracker, this));
            return true;
        }
        return false;
    }

    public boolean stopIntake() {
        if (!isReady()) {
            IntakeBallState.killIntake = true;
            return true;
        }
        return false;
    }


    public void link(MicrowaveScoopHandler microwaveScoopHandler, SlotTracker slotTracker, LaunchSystem launchSystem) {
        this.microwaveScoopHandler = microwaveScoopHandler;
        this.slotTracker = slotTracker;
        this.launchSystem = launchSystem;
    }
}