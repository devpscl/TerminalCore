package net.pascal.terminal.util;

import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.component.TComponent;

public interface RenderVectorHandler {

    TVector getSize(TComponent component, TVector originalPosition, TVector originalSize, TDisplayDrawer drawer, TVector displaySize);

    TVector getPosition(TComponent component, TVector originalPosition, TVector originalSize, TDisplayDrawer drawer, TVector displaySize);

}
