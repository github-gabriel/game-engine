package de.gabriel.gameEngine.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

/**
 * Eigene Klasse zum Highlighten der verschiedenen Log-Level in
 * entsprechenden Farben.
 */
public class HighlightLogLevel extends ForegroundCompositeConverterBase<ILoggingEvent> {

    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        Level level = event.getLevel();
        return switch (level.toInt()) {
            case Level.TRACE_INT -> ANSIConstants.CYAN_FG;
            case Level.DEBUG_INT -> ANSIConstants.BLUE_FG;
            case Level.INFO_INT -> ANSIConstants.GREEN_FG;
            case Level.WARN_INT -> ANSIConstants.YELLOW_FG;
            case Level.ERROR_INT -> ANSIConstants.RED_FG;
            default -> ANSIConstants.DEFAULT_FG;
        };
    }
}