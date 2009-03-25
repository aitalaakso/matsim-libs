/* **********************************import java.util.List;

import org.matsim.interfaces.basic.v01.Id;
                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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

package org.matsim.core.basic.network;

import java.util.List;

import org.matsim.api.basic.v01.Id;
/**
 * 
 * @author dgrether
 *
 */
public interface BasicLanesToLinkAssignment {

	public List<BasicLane> getLanes();

	/**
	 * @param lane
	 */
	public void addLane(BasicLane lane);

	public Id getLinkId();

}