package capstone.tunemaker.dto.vocal;

import java.util.HashMap;
import java.util.Map;

public class PitchConverter {
    private static final Map<String, Double> PITCH_TO_HZ = new HashMap<String, Double>() {{
        put("C3", 130.81);
        put("CSharp3", 138.59);
        put("D3", 146.83);
        put("DSharp3", 155.56);
        put("E3", 164.81);
        put("F3", 174.61);
        put("FSharp3", 185.00);
        put("G3", 196.00);
        put("GSharp3", 207.65);
        put("A3", 220.00);
        put("ASharp3", 233.08);
        put("B3", 246.94);
        put("C4", 261.63);
        put("CSharp4", 277.18);
        put("D4", 293.66);
        put("DSharp4", 311.13);
        put("E4", 329.63);
        put("F4", 349.23);
        put("FSharp4", 369.99);
        put("G4", 392.00);
        put("GSharp4", 415.30);
        put("A4", 440.00);
        put("ASharp4", 466.16);
        put("B4", 493.88);
        put("C5", 523.25);
        put("CSharp5", 554.37);
        put("D5", 587.33);
        put("DSharp5", 622.25);
        put("E5", 659.25);
        put("F5", 698.46);
        put("FSharp5", 739.99);
        put("G5", 783.99);
        put("GSharp5", 830.61);
        put("A5", 880.00);
    }};

    public static Double convertPitchToHz(String target) {
        return PITCH_TO_HZ.get(target);
    }

}
