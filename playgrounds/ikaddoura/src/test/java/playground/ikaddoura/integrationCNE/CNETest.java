/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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

/**
 * 
 */
package playground.ikaddoura.integrationCNE;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.core.controler.Controler;
import org.matsim.testcases.MatsimTestUtils;

import playground.ikaddoura.analysis.linkDemand.LinkDemandEventHandler;

/**
 * @author ikaddoura
 *
 */
public class CNETest {
	
	@Rule
	public MatsimTestUtils testUtils = new MatsimTestUtils();

	/**
	 * Test for isolated congestion pricing, isolated noise pricing and simultaneous congestion and noise pricing.
	 *
	 */
	@Test
	public final void test1(){

		String configFile = testUtils.getPackageInputDirectory() + "CNETest/test1/config.xml";
		
		// baseCase
		CNEIntegration cneIntegration1 = new CNEIntegration(configFile, testUtils.getOutputDirectory() + "bc");
		Controler controler1 = cneIntegration1.prepareControler();		
		LinkDemandEventHandler handler1 = new LinkDemandEventHandler(controler1.getScenario().getNetwork());
		controler1.getEvents().addHandler(handler1);
		controler1.getConfig().controler().setCreateGraphs(false);
		controler1.run();

		// congestion pricing
		CNEIntegration cneIntegration2 = new CNEIntegration(configFile, testUtils.getOutputDirectory() + "c");
		cneIntegration2.setCongestionPricing(true);
		Controler controler2 = cneIntegration2.prepareControler();
		LinkDemandEventHandler handler2 = new LinkDemandEventHandler(controler2.getScenario().getNetwork());
		controler2.getEvents().addHandler(handler2);
		controler2.getConfig().controler().setCreateGraphs(false);
		controler2.run();

		// noise pricing
		CNEIntegration cneIntegration3 = new CNEIntegration(configFile, testUtils.getOutputDirectory() + "n");
		cneIntegration3.setNoisePricing(true);
		Controler controler3 = cneIntegration3.prepareControler();
		LinkDemandEventHandler handler3 = new LinkDemandEventHandler(controler3.getScenario().getNetwork());
		controler3.getEvents().addHandler(handler3);
		controler3.getConfig().controler().setCreateGraphs(false);
		controler3.run();
						
		// congestion + noise pricing
		CNEIntegration cneIntegration4 = new CNEIntegration(configFile, testUtils.getOutputDirectory() + "cn");
		cneIntegration4.setCongestionPricing(true);
		cneIntegration4.setNoisePricing(true);
		Controler controler4 = cneIntegration4.prepareControler();
		LinkDemandEventHandler handler4 = new LinkDemandEventHandler(controler4.getScenario().getNetwork());
		controler4.getEvents().addHandler(handler4);
		controler4.getConfig().controler().setCreateGraphs(false);
		controler4.run();
		
		System.out.println("----------------------------------");
		System.out.println("Base case:");
		printResults(handler1);
		
		System.out.println("----------------------------------");
		System.out.println("Congestion pricing:");
		printResults(handler2);
		
		System.out.println("----------------------------------");
		System.out.println("Noise pricing:");
		printResults(handler3);
		
		System.out.println("----------------------------------");
		System.out.println("Congestion + noise pricing:");
		printResults(handler4);
		
		// no zero demand on bottleneck link
		Assert.assertEquals(true,
				getBottleneckDemand(handler1) != 0 &&
				getBottleneckDemand(handler2) != 0 &&
				getBottleneckDemand(handler3) != 0 &&
				getBottleneckDemand(handler4) != 0);
				
		// the demand on the bottleneck link should go down in case of congestion pricing (c)
		Assert.assertEquals(true, getBottleneckDemand(handler2) < getBottleneckDemand(handler1));		
		
		// the demand on the noise sensitive route should go up in case of congestion pricing (c)
		Assert.assertEquals(true, getNoiseSensitiveRouteDemand(handler2) > getNoiseSensitiveRouteDemand(handler1));
		
		// the demand on the noise sensitive route should go down in case of noise pricing (n)
		Assert.assertEquals(true, getNoiseSensitiveRouteDemand(handler3) < getNoiseSensitiveRouteDemand(handler1));
		
		// the demand on the long and uncongested route should go up in case of noise pricing (n)
		Assert.assertEquals(true, getLongUncongestedDemand(handler3) > getLongUncongestedDemand(handler1));	

		// the demand on the bottleneck link should go down in case of congestion + noise pricing (cn)
		Assert.assertEquals(true, getBottleneckDemand(handler4) < getBottleneckDemand(handler1));
		
		// the demand on the noise sensitive route should go down in case of congestion + noise pricing (cn)
		Assert.assertEquals(true, getNoiseSensitiveRouteDemand(handler4) < getNoiseSensitiveRouteDemand(handler1));
	
		// the demand on the long and uncongested route should go up in case of congestion and noise pricing (cn)
		Assert.assertEquals(true, getLongUncongestedDemand(handler4) > getLongUncongestedDemand(handler1));	
		
		// the demand on the bottleneck link should go down in case of congestion and noise pricing (cn) compared to noise pricing (n)
		Assert.assertEquals(true, getBottleneckDemand(handler4) < getBottleneckDemand(handler3));	
	}
	
	@Ignore
	@Test
	public final void test2(){
		String configFile = testUtils.getPackageInputDirectory() + "CNETest/test2/config.xml";
		
		// baseCase
		CNEIntegration cneIntegration1 = new CNEIntegration(configFile, testUtils.getOutputDirectory() + "bc");
		Controler controler1 = cneIntegration1.prepareControler();		
		LinkDemandEventHandler handler1 = new LinkDemandEventHandler(controler1.getScenario().getNetwork());
		controler1.getEvents().addHandler(handler1);
		controler1.getConfig().controler().setCreateGraphs(false);
		controler1.run();

		// air pollution pricing
		CNEIntegration cneIntegration2 = new CNEIntegration(configFile, testUtils.getOutputDirectory() + "e");
		cneIntegration2.setAirPollutionPricing(true);
		Controler controler2 = cneIntegration2.prepareControler();
		LinkDemandEventHandler handler2 = new LinkDemandEventHandler(controler2.getScenario().getNetwork());
		controler2.getEvents().addHandler(handler2);
		controler2.getConfig().controler().setCreateGraphs(false);
		controler2.run();

		// noise pricing
		CNEIntegration cneIntegration3 = new CNEIntegration(configFile, testUtils.getOutputDirectory() + "n");
		cneIntegration3.setNoisePricing(true);
		Controler controler3 = cneIntegration3.prepareControler();
		LinkDemandEventHandler handler3 = new LinkDemandEventHandler(controler3.getScenario().getNetwork());
		controler3.getEvents().addHandler(handler3);
		controler3.getConfig().controler().setCreateGraphs(false);
		controler3.run();
						
		// air pollution + noise pricing
		CNEIntegration cneIntegration4 = new CNEIntegration(configFile, testUtils.getOutputDirectory() + "cn");
		cneIntegration4.setAirPollutionPricing(true);
		cneIntegration4.setNoisePricing(true);
		Controler controler4 = cneIntegration4.prepareControler();
		LinkDemandEventHandler handler4 = new LinkDemandEventHandler(controler4.getScenario().getNetwork());
		controler4.getEvents().addHandler(handler4);
		controler4.getConfig().controler().setCreateGraphs(false);
		controler4.run();
		
		System.out.println("----------------------------------");
		System.out.println("Base case:");
		printResults(handler1);
		
		System.out.println("----------------------------------");
		System.out.println("Air pollution pricing:");
		printResults(handler2);
		
		System.out.println("----------------------------------");
		System.out.println("Noise pricing:");
		printResults(handler3);
		
		System.out.println("----------------------------------");
		System.out.println("Air pollution + noise pricing:");
		printResults(handler4);
		
		// no zero demand on bottleneck link
		Assert.assertEquals(true,
				getBottleneckDemand(handler1) != 0 &&
				getBottleneckDemand(handler2) != 0 &&
				getBottleneckDemand(handler3) != 0 &&
				getBottleneckDemand(handler4) != 0);
				
		// the demand on the bottleneck link should go down in case of congestion pricing (c)
		Assert.assertEquals(true, getBottleneckDemand(handler2) < getBottleneckDemand(handler1));		
		
		// the demand on the noise sensitive route should go up in case of congestion pricing (c)
		Assert.assertEquals(true, getNoiseSensitiveRouteDemand(handler2) > getNoiseSensitiveRouteDemand(handler1));
		
		// the demand on the noise sensitive route should go down in case of noise pricing (n)
		Assert.assertEquals(true, getNoiseSensitiveRouteDemand(handler3) < getNoiseSensitiveRouteDemand(handler1));
		
		// the demand on the long and uncongested route should go up in case of noise pricing (n)
		Assert.assertEquals(true, getLongUncongestedDemand(handler3) > getLongUncongestedDemand(handler1));	

		// the demand on the bottleneck link should go down in case of congestion + noise pricing (cn)
		Assert.assertEquals(true, getBottleneckDemand(handler4) < getBottleneckDemand(handler1));
		
		// the demand on the noise sensitive route should go down in case of congestion + noise pricing (cn)
		Assert.assertEquals(true, getNoiseSensitiveRouteDemand(handler4) < getNoiseSensitiveRouteDemand(handler1));
	
		// the demand on the long and uncongested route should go up in case of congestion and noise pricing (cn)
		Assert.assertEquals(true, getLongUncongestedDemand(handler4) > getLongUncongestedDemand(handler1));	
		
		// the demand on the bottleneck link should go down in case of congestion and noise pricing (cn) compared to noise pricing (n)
		Assert.assertEquals(true, getBottleneckDemand(handler4) < getBottleneckDemand(handler3));	
		
	}
	
	private void printResults(LinkDemandEventHandler handler) {
		System.out.println("long but uncongested, low noise cost: " + getLongUncongestedDemand(handler));
		System.out.println("bottleneck, low noise cost: " + getBottleneckDemand(handler));
		System.out.println("high noise cost: " + (getNoiseSensitiveRouteDemand(handler)));
	}
	
	private int getNoiseSensitiveRouteDemand(LinkDemandEventHandler handler) {
		int noiseSensitiveRouteDemand = 0;
		if (handler.getLinkId2demand().containsKey(Id.createLinkId("link_7_8"))) {
			noiseSensitiveRouteDemand = handler.getLinkId2demand().get(Id.createLinkId("link_7_8"));
		}
		return noiseSensitiveRouteDemand;
	}

	private int getBottleneckDemand(LinkDemandEventHandler handler) {
		int bottleneckRouteDemand = 0;
		if (handler.getLinkId2demand().containsKey(Id.createLinkId("link_4_5"))) {
			bottleneckRouteDemand = handler.getLinkId2demand().get(Id.createLinkId("link_4_5"));
		}
		return bottleneckRouteDemand;
	}

	private int getLongUncongestedDemand(LinkDemandEventHandler handler) {
		int longUncongestedRouteDemand = 0;
		if (handler.getLinkId2demand().containsKey(Id.createLinkId("link_1_2"))) {
			longUncongestedRouteDemand = handler.getLinkId2demand().get(Id.createLinkId("link_1_2"));
		}
		return longUncongestedRouteDemand;
	}

//	private void printResults(LinkDemandEventHandler handler) {
//		System.out.println("high speed + low N costs + high E costs: " + demand_highSpeed_lowN_highE(handler));
//		System.out.println("low speed + low N costs + low E costs: " + demand_lowSpeed_lowN_lowE(handler));
//		System.out.println("medium speed + high N costs + low E costs: " + demand_mediumSpeed_highN_lowE(handler));
//	}
//
//	private int demand_mediumSpeed_highN_lowE(LinkDemandEventHandler handler) {
//		int demand = 0;
//		if (handler.getLinkId2demand().containsKey(Id.createLinkId("link_7_8"))) {
//			demand = handler.getLinkId2demand().get(Id.createLinkId("link_7_8"));
//		}
//		return demand;
//	}
//
//	private int demand_lowSpeed_lowN_lowE(LinkDemandEventHandler handler) {
//		int demand = 0;
//		if (handler.getLinkId2demand().containsKey(Id.createLinkId("link_3_6"))) {
//			demand = handler.getLinkId2demand().get(Id.createLinkId("link_3_6"));
//		}
//		return demand;
//	}
//
//	private int demand_highSpeed_lowN_highE(LinkDemandEventHandler handler) {
//		int demand = 0;
//		if (handler.getLinkId2demand().containsKey(Id.createLinkId("link_1_2"))) {
//			demand = handler.getLinkId2demand().get(Id.createLinkId("link_1_2"));
//		}
//		return demand;
//	}
		
}
