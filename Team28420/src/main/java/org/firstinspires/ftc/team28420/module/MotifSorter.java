package org.firstinspires.ftc.team28420.module;

import org.firstinspires.ftc.team28420.processors.BallDetection;

import java.util.HashMap;

public class MotifSorter {
    private String curMotif = "";
    private String targetMotif = null;
    private HashMap<String, Integer> sortSeqMap = null;

    public MotifSorter() {
        initSortSeq();
        resetMotif();
    }

    // SETTERS
    public void resetMotif() {
        curMotif = "";
    }
    public void appendBallToMotif(BallDetection.BallColor color) {
        curMotif += (color == BallDetection.BallColor.PURPLE) ? 'P' : 'G';
    }

    /**
     * Appending ball color as char
     * @param color 'g' or 'p' value
     */
    public void appendBallToMotif(char color) {
        if(color == 'g' || color == 'p') curMotif += color;
    }

    /**
     * Deletes last ball in chamber
     */
    public void dropLastBall() {
        curMotif = curMotif.substring(0, curMotif.length() - 1);
    }

    /**
     * Sets current motif manually
     * @param motif
     */
    public void setCurMotif(String motif) {
        curMotif = motif;
    }

    /**
     * Sets target motif
     * @param targetMotif
     */
    public void setTargetMotif(String targetMotif) {
        this.targetMotif = targetMotif;
    }

    // GETTERS
    public int getMoveSlots() {
        if(targetMotif == null) return 0;
        int currentIndex = sortSeqMap.getOrDefault(curMotif, 0);
        int targetIndex = sortSeqMap.getOrDefault(targetMotif, 0);

        int moveSlots = (targetIndex - currentIndex + 3) % 3;

        return moveSlots;
    }
    public String getCurMotif() {
        return curMotif;
    }
    public boolean isCorrectMotif() {
        return curMotif.equals(targetMotif);
    }
    public boolean isMotifFull() {
        return curMotif.length() == 3;
    }

    private void initSortSeq() {
        sortSeqMap = new HashMap<String, Integer>();
        sortSeqMap.put("PPG", 0);
        sortSeqMap.put("GPP", 1);
        sortSeqMap.put("PGP", 2);
    }

    public boolean isValid() {
        long g = curMotif.chars().filter(ch -> ch == 'G').count();
        long p = curMotif.chars().filter(ch -> ch == 'P').count();
        return (g == 1 && p == 2);
    }

}
