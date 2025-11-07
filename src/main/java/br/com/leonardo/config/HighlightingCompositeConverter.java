package br.com.leonardo.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class HighlightingCompositeConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {

    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        Level level = event.getLevel();
        return switch (level.toInt()) {
            case Level.ERROR_INT -> ANSIConstants.BOLD + ANSIConstants.RED_FG;
            case Level.WARN_INT, Level.DEBUG_INT -> ANSIConstants.BOLD + ANSIConstants.YELLOW_FG;
            case Level.INFO_INT -> ANSIConstants.GREEN_FG;
            case Level.TRACE_INT -> ANSIConstants.CYAN_FG;
            default -> ANSIConstants.DEFAULT_FG;
        };
    }

}