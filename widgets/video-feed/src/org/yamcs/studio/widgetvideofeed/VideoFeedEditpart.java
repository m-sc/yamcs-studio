package org.yamcs.studio.widgetvideofeed;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.simplepv.VTypeHelper;
import org.diirt.vtype.VType;
import org.eclipse.draw2d.IFigure;

public class VideoFeedEditpart extends AbstractPVWidgetEditPart {

    @Override
    protected IFigure doCreateFigure() {
        VideoFeedFigure figure = new VideoFeedFigure();
        figure.setMin(getWidgetModel().getMin());
        figure.setMax(getWidgetModel().getMax());
        return figure;
    }

    @Override
    public VideoFeedModel getWidgetModel() {
        return (VideoFeedModel) super.getWidgetModel();
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, (oldValue, newValue, figure) -> {
            if (newValue == null) {
                return false;
            }
            ((VideoFeedFigure) figure).setValue(VTypeHelper.getDouble((VType) newValue));
            return false;
        });

        setPropertyChangeHandler(VideoFeedModel.PROP_MAX, (oldValue, newValue, figure) -> {
            ((VideoFeedFigure) figure).setMax((Double) newValue);
            return false;
        });

        setPropertyChangeHandler(VideoFeedModel.PROP_MIN, (oldValue, newValue, figure) -> {
            ((VideoFeedFigure) figure).setMin((Double) newValue);
            return false;
        });
    }

    @Override
    public Object getValue() {
        return ((VideoFeedFigure) getFigure()).getValue();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Double) {
            ((VideoFeedFigure) getFigure()).setValue((Double) value);
        }
    }
}
