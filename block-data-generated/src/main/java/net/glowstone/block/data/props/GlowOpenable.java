package net.glowstone.block.data.props;

import net.glowstone.block.data.states.StatefulBlockData;
import net.glowstone.block.data.states.reports.BooleanStateReport;
import net.glowstone.redstone_transformer.annotations.AssociatedWithProp;
import org.bukkit.block.data.Openable;

@AssociatedWithProp(
    propName = GlowOpenable.Constants.PROP_NAME,
    reportType = BooleanStateReport.class,
    interfaceName = "Openable"
)
public interface GlowOpenable extends StatefulBlockData, Openable {
    class Constants {
        public static final String PROP_NAME = "open";
        public static final Class<Boolean> STATE_TYPE = Boolean.class;
    }

    @Override
    default boolean isOpen() {
        return getValue(Constants.PROP_NAME, Constants.STATE_TYPE);
    }

    @Override
    default void setOpen(boolean attached) {
        setValue(Constants.PROP_NAME, Constants.STATE_TYPE, attached);
    }
}
