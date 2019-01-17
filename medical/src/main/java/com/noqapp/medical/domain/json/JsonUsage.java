package com.noqapp.medical.domain.json;

/**
 * //TODO this is not yet implemented and so is JsonUsageList
 * Original 		        Freq    15 - Freq	        Sorted	Rank            Sorted - Original
 * 1	    Fever	        1	    14	                9	    1	Viral       9-2
 * 2	    Viral	        6	    9	                9	    2	Vomit       9-5
 * 3	    Cold	        3	    12	                12	    3	Cold        12-3
 * 4	    Pain	        3	    12	                12	    4	Pain        12-4
 * 5	    Vomit	        6	    9	                13	    5	Stool
 * 6	    Eyes	        1	    14	                14	    6	Eyes
 * 7	    Stool	        2	    13	                14	    7	Fever
 *
 * 	Number of Patients	15 = totalInstance
 * 	For more accuracy, compare sorted with original rank(previous rank) i.e. Sorted - Original when Sorted has same value.
 *
 * hitender
 * 2018-11-27 06:50
 */
public class JsonUsage {

    private String name;
    private int rank;
    private int frequency;
    private int totalInstance;

    public String getName() {
        return name;
    }

    public JsonUsage setName(String name) {
        this.name = name;
        return this;
    }

    public int getRank() {
        return rank;
    }

    public JsonUsage setRank(int rank) {
        this.rank = rank;
        return this;
    }

    public int getFrequency() {
        return frequency;
    }

    public JsonUsage setFrequency(int frequency) {
        this.frequency = frequency;
        return this;
    }

    public int getTotalInstance() {
        return totalInstance;
    }

    public JsonUsage setTotalInstance(int totalInstance) {
        this.totalInstance = totalInstance;
        return this;
    }

    /** Sort on this and then re-rank. Lowest number is higher and vice versa. */
    int computeFrequentUsage() {
        return totalInstance - frequency;
    }
}
