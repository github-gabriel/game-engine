package de.gabriel.gameEngine.skybox.time;

public class HourlyDayNightCycle implements TimeCycle {

    /**
     * Die Länge eines Tages (in Stunden).
     */
    private final float DAY_LENGTH = 0.01f;

    /**
     * Die Startzeit des Tag-Nacht-Zyklus.
     */
    private final long startTime;

    public HourlyDayNightCycle() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Gibt einen Wert zwischen 0 und 1 als Blendfaktor zurück, der zum
     * Blenden zwischen der Tag und Nacht Skybox verwendet wird. Der Faktor
     * wird über eine Zeit {@link HourlyDayNightCycle#DAY_LENGTH} erhöht, bis
     * der Zyklus von vorne beginnt.
     *
     * @return den Blendfaktor abhängig von der Zeit
     */
    @Override
    public float getTime() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        float elapsedTimeInSeconds = elapsedTime / 1000f;

        float DAY_LENGTH_IN_SECONDS = DAY_LENGTH * 3600;
        float frequency = 1f / DAY_LENGTH_IN_SECONDS; // Adjust as needed
        float angularFrequency = 2 * (float) Math.PI * frequency;

        float blendFactor = 0.5f * (1 + (float) Math.sin(angularFrequency * elapsedTimeInSeconds));

        return blendFactor;
    }

}
