/* *********************************************************************** *
 * project: org.matsim.*
 * DefaultTravelCostCalculatorFactoryImpl
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.contrib.noise;

import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.router.costcalculators.RandomizingTimeDistanceTravelDisutilityFactory;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;

import com.google.inject.Inject;


/**
 * @author ikaddoura
 *
 */
public final class NoiseTollTimeDistanceTravelDisutilityFactory implements TravelDisutilityFactory {

	private TravelDisutilityFactory travelDisutilityFactoryDelegate;
	
	@Inject private NoiseContext noiseContext;
	@Inject private PlansCalcRouteConfigGroup plansCalcRouteConfigGroup;

	public NoiseTollTimeDistanceTravelDisutilityFactory( TravelDisutilityFactory travelDisutilityFactoryDelegate ) {
		this.travelDisutilityFactoryDelegate = travelDisutilityFactoryDelegate;
	}

	@Override
	public final TravelDisutility createTravelDisutility(TravelTime timeCalculator) {

		if ( travelDisutilityFactoryDelegate instanceof RandomizingTimeDistanceTravelDisutilityFactory ){
			((RandomizingTimeDistanceTravelDisutilityFactory) travelDisutilityFactoryDelegate).setSigma( plansCalcRouteConfigGroup.getRoutingRandomness() );
		}
		
		return new NoiseTollTimeDistanceTravelDisutility(
				travelDisutilityFactoryDelegate.createTravelDisutility(timeCalculator ),
				new NoiseTollCalculator(noiseContext), this.noiseContext.getScenario().getConfig().planCalcScore().getMarginalUtilityOfMoney(),
				plansCalcRouteConfigGroup.getRoutingRandomness()!=0.
			);
	}
	
}
