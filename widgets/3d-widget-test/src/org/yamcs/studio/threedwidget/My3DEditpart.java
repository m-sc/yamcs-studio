package org.yamcs.studio.threedwidget;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.simplepv.VTypeHelper;
import org.diirt.vtype.VType;
import org.eclipse.draw2d.IFigure;

public class My3DEditpart extends AbstractPVWidgetEditPart {

    @Override
    protected IFigure doCreateFigure() {
        My3DFigure figure = new My3DFigure(this);
        figure.setMin(getWidgetModel().getMin());
        figure.setMax(getWidgetModel().getMax());
        return figure;
    }

    @Override
    public My3DModel getWidgetModel() {
        return (My3DModel) super.getWidgetModel();
    }

    @Override
    protected void registerPropertyChangeHandlers() {
        setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, (oldValue, newValue, figure) -> {
            if (newValue == null) {
                return false;
            }
            ((My3DFigure) figure).setValue(VTypeHelper.getDouble((VType) newValue));
            return false;
        });

        setPropertyChangeHandler(My3DModel.PROP_MAX, (oldValue, newValue, figure) -> {
            ((My3DFigure) figure).setMax((Double) newValue);
            return false;
        });

        setPropertyChangeHandler(My3DModel.PROP_MIN, (oldValue, newValue, figure) -> {
            ((My3DFigure) figure).setMin((Double) newValue);
            return false;
        });
    }

    @Override
    public Object getValue() {
        return ((My3DFigure) getFigure()).getValue();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Double) {
            ((My3DFigure) getFigure()).setValue((Double) value);
        }
    }
}
