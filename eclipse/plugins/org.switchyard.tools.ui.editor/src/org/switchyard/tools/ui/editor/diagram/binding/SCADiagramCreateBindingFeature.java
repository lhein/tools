/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 * @author bfitzpat
 ******************************************************************************/
package org.switchyard.tools.ui.editor.diagram.binding;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.soa.sca.sca1_1.model.sca.Reference;
import org.eclipse.soa.sca.sca1_1.model.sca.Service;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.switchyard.tools.models.switchyard1_0.soap.SOAPBindingType;
import org.switchyard.tools.models.switchyard1_0.soap.SOAPPackage;
import org.switchyard.tools.models.switchyard1_0.switchyard.SwitchYardBindingType;
import org.switchyard.tools.ui.editor.ImageProvider;
import org.switchyard.tools.ui.editor.diagram.binding.wizards.SCADiagramAddBindingWizard;

/**
 * @author bfitzpat
 * 
 */
public class SCADiagramCreateBindingFeature extends AbstractCreateFeature {

    private boolean _hasDoneChanges;

    /**
     * @param fp feature provider
     */
    public SCADiagramCreateBindingFeature(IFeatureProvider fp) {
        super(fp, "Binding", "Create binding");
    }

    @Override
    public boolean hasDoneChanges() {
        return _hasDoneChanges;
    }

    @Override
    public boolean canCreate(ICreateContext context) {
        ContainerShape targetContainer = context.getTargetContainer();
        // check if user wants to add to a service or reference
        if (targetContainer instanceof Service) {
            return true;
        }
        if (getBusinessObjectForPictogramElement(targetContainer) instanceof Service) {
            return true;
        }
        if (targetContainer instanceof Reference) {
            return true;
        }
        if (getBusinessObjectForPictogramElement(targetContainer) instanceof Reference) {
            return true;
        }
        return false;
    }

    @Override
    public Object[] create(ICreateContext context) {

        SwitchYardBindingType newBinding = null;
        SCADiagramAddBindingWizard wizard = new SCADiagramAddBindingWizard();
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        WizardDialog wizDialog = new WizardDialog(shell, wizard);
        int rtn_code = wizDialog.open();
        if (rtn_code == Window.OK) {
            newBinding = wizard.getBinding();
        } else {
            _hasDoneChanges = false;
            return EMPTY;
        }

        ContainerShape targetContainer = context.getTargetContainer();
        Object targetBO = getBusinessObjectForPictogramElement(targetContainer);
        if (newBinding != null) {
            if (targetBO instanceof Service) {
                if (newBinding instanceof SOAPBindingType) {
                    ((Service) targetBO).getBindingGroup().add(SOAPPackage.eINSTANCE.getDocumentRoot_BindingSoap(),
                            newBinding);
                }
            } else if (targetBO instanceof Reference) {
                if (newBinding instanceof SOAPBindingType) {
                    ((Reference) targetBO).getBindingGroup().add(SOAPPackage.eINSTANCE.getDocumentRoot_BindingSoap(),
                            newBinding);
                }
            }

        }

        // do the add
        addGraphicalRepresentation(context, newBinding);

        // activate direct editing after object creation
        getFeatureProvider().getDirectEditingInfo().setActive(true);

        // make sure we look like we actually did something.
        _hasDoneChanges = true;

        // return newly created business object(s)
        return new Object[] {newBinding };
    }

    @Override
    public String getCreateImageId() {
        return ImageProvider.IMG_16_CHAIN;
    }

}