/*
 *  *********************************************************************** *
 *  * project: org.matsim.*
 *  * *********************************************************************** *
 *  *                                                                         *
 *  * copyright       : (C) 2022 by the members listed in the COPYING,        *
 *  *                   LICENSE and WARRANTY file.                            *
 *  * email           : info at matsim dot org                                *
 *  *                                                                         *
 *  * *********************************************************************** *
 *  *                                                                         *
 *  *   This program is free software; you can redistribute it and/or modify  *
 *  *   it under the terms of the GNU General Public License as published by  *
 *  *   the Free Software Foundation; either version 2 of the License, or     *
 *  *   (at your option) any later version.                                   *
 *  *   See also COPYING, LICENSE and WARRANTY file                           *
 *  *                                                                         *
 *  * ***********************************************************************
 */

package lsp.usecase;

import lsp.LSPSimulationTracker;
import org.matsim.contrib.freight.events.eventhandler.FreightServiceEndEventHandler;
import lsp.shipment.*;
import org.matsim.api.core.v01.Id;
import org.matsim.contrib.freight.carrier.CarrierService;

import org.matsim.contrib.freight.events.FreightServiceEndEvent;
import lsp.LogisticsSolutionElement;
import lsp.LSPCarrierResource;
import lsp.LSPResource;
import org.matsim.core.controler.events.AfterMobsimEvent;
import org.matsim.core.controler.listener.AfterMobsimListener;
import org.matsim.core.events.handler.EventHandler;

import java.util.ArrayList;
import java.util.Collection;

class CollectionServiceEndEventHandler implements AfterMobsimListener, FreightServiceEndEventHandler, LSPSimulationTracker<LSPShipment> {

	private final CarrierService carrierService;
	private final LogisticsSolutionElement solutionElement;
	private final LSPCarrierResource resource;
	private final Collection<EventHandler> eventHandlers = new ArrayList<>();
	private LSPShipment lspShipment;

	public CollectionServiceEndEventHandler(CarrierService carrierService, LSPShipment lspShipment, LogisticsSolutionElement element, LSPCarrierResource resource) {
		this.carrierService = carrierService;
		this.lspShipment = lspShipment;
		this.solutionElement = element;
		this.resource = resource;
	}


	@Override
	public void reset(int iteration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(FreightServiceEndEvent event) {
		if (event.getServiceId() == carrierService.getId() && event.getCarrierId() == resource.getCarrier().getId()) {
			logLoad(event);
			logTransport(event);
		}
	}

	private void logLoad(FreightServiceEndEvent event) {
		ShipmentUtils.LoggedShipmentLoadBuilder builder = ShipmentUtils.LoggedShipmentLoadBuilder.newInstance();
		builder.setStartTime(event.getTime() - event.getServiceDuration());
		builder.setEndTime(event.getTime());
		builder.setLogisticsSolutionElement(solutionElement);
		builder.setResourceId(resource.getId());
		builder.setLinkId(event.getLinkId());
		builder.setCarrierId(event.getCarrierId());
		ShipmentPlanElement loggedShipmentLoad = builder.build();
		String idString = loggedShipmentLoad.getResourceId() + "" + loggedShipmentLoad.getSolutionElement().getId() + "" + loggedShipmentLoad.getElementType();
		Id<ShipmentPlanElement> loadId = Id.create(idString, ShipmentPlanElement.class);
		lspShipment.getLog().addPlanElement(loadId, loggedShipmentLoad);
	}

	private void logTransport(FreightServiceEndEvent event) {
		ShipmentUtils.LoggedShipmentTransportBuilder builder = ShipmentUtils.LoggedShipmentTransportBuilder.newInstance();
		builder.setStartTime(event.getTime());
		builder.setLogisticsSolutionElement(solutionElement);
		builder.setResourceId(resource.getId());
		builder.setFromLinkId(event.getLinkId());
		builder.setCarrierId(event.getCarrierId());
		ShipmentLeg transport = builder.build();
		String idString = transport.getResourceId() + "" + transport.getSolutionElement().getId() + "" + transport.getElementType();
		Id<ShipmentPlanElement> transportId = Id.create(idString, ShipmentPlanElement.class);
		lspShipment.getLog().addPlanElement(transportId, transport);
	}


	public CarrierService getCarrierService() {
		return carrierService;
	}


	public LSPShipment getLspShipment() {
		return lspShipment;
	}


	public LogisticsSolutionElement getElement() {
		return solutionElement;
	}


	public Id<LSPResource> getResourceId() {
		return resource.getId();
	}


	@Override
	public void setEmbeddingContainer(LSPShipment pointer) {
		this.lspShipment = pointer;
	}

	@Override
	public void notifyAfterMobsim(AfterMobsimEvent event) {
	}
}
