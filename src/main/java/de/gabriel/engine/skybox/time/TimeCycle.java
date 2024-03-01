package de.gabriel.engine.skybox.time;

/**
 * Abstrakte Klasse für die Implementation von verschiedenen
 * Zeitzyklen.
 */
public interface TimeCycle {

    /**
     * Gibt die aktuelle Zeit zurück, die als Blend Faktor
     * für die Blendung zwischen der Tag und Nacht Skybox
     * verwendet wird. Daher ist der Faktor auf einen
     * Wert von 0 bis 1 limitiert.
     */
    public float getTime();

}
