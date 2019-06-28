package org.matsim.vehicles;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.core.utils.io.MatsimXmlParser;
import org.matsim.utils.objectattributes.attributable.AttributesXmlReaderDelegate;
import org.xml.sax.Attributes;

import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

class VehicleReaderV2 extends MatsimXmlParser{
	private static final Logger log = Logger.getLogger( VehicleReaderV2.class ) ;

	private final Vehicles vehicles;
	private final VehiclesFactory builder;
	private VehicleType currentVehType = null;
	private VehicleCapacity currentCapacity = null;
	private FreightCapacity currentFreightCapacity = null;
	private EngineInformation currentEngineInformation = null;
//	private EngineInformation.FuelType currentFuelType = null;
//	private double fixedCostsPerDay = Double.NaN;
//	private double costsPerMeter = Double.NaN;
//	private double costsPerSecond = Double.NaN;

	private final AttributesXmlReaderDelegate attributesReader = new AttributesXmlReaderDelegate();
	private org.matsim.utils.objectattributes.attributable.Attributes currAttributes =
		  new org.matsim.utils.objectattributes.attributable.Attributes();

	public VehicleReaderV2( final Vehicles vehicles ){
		log.info("Using " + this.getClass().getName());
		this.vehicles = vehicles;
		this.builder = this.vehicles.getFactory();
	}

	@Override
	public void endTag( final String name, final String content, final Stack<String> context ){
		switch( name ){
			case VehicleSchemaV2Names.DESCRIPTION:
				if( content.trim().length() > 0 ){
					this.currentVehType.setDescription( content.trim() );
				}
				break;
			case VehicleSchemaV2Names.ENGINEINFORMATION:
				this.currentVehType.setEngineInformation( this.currentEngineInformation );
				this.currentEngineInformation = null;
				break;
			case VehicleSchemaV2Names.FREIGHTCAPACITY:
				this.currentCapacity.setFreightCapacity( this.currentFreightCapacity );
				this.currentFreightCapacity = null;
				break;
			case VehicleSchemaV2Names.CAPACITY:
				this.currentVehType.setCapacity( this.currentCapacity );
				this.currentCapacity = null;
				break;
			case VehicleSchemaV2Names.VEHICLETYPE:
				this.vehicles.addVehicleType( this.currentVehType );
				this.currentVehType = null;
				break;
			case VehicleSchemaV2Names.ATTRIBUTES:
				this.currAttributes = null;
				break;
			case VehicleSchemaV2Names.ATTRIBUTE:
				this.attributesReader.endTag( name, content, context );
				break;
			default:
				throw new IllegalStateException( "Unexpected value: " + name );
		}
	}


	@Override
	public void startTag( final String name, final Attributes atts, final Stack<String> context ){
		switch( name ){
			case VehicleSchemaV2Names.VEHICLETYPE:
				this.currentVehType = this.builder.createVehicleType( Id.create( atts.getValue( VehicleSchemaV2Names.ID ), VehicleType.class ) );
				break;
			case VehicleSchemaV2Names.LENGTH:
				this.currentVehType.setLength( Double.parseDouble( atts.getValue( VehicleSchemaV2Names.METER ) ) );
				break;
			case VehicleSchemaV2Names.WIDTH:
				this.currentVehType.setWidth( Double.parseDouble( atts.getValue( VehicleSchemaV2Names.METER ) ) );
				break;
			case VehicleSchemaV2Names.MAXIMUMVELOCITY:
				double val = Double.parseDouble( atts.getValue( VehicleSchemaV2Names.METERPERSECOND ) );
				if( val == 1.0 ) log.warn(
					  "The vehicle type's maximum velocity is set to 1.0 meter per second, is this really intended? vehicletype = " + this.currentVehType.getId().toString() );
				this.currentVehType.setMaximumVelocity( val );
				break;
			case VehicleSchemaV2Names.ENGINEINFORMATION:
				this.currentEngineInformation = new EngineInformationImpl();
				break;
			case VehicleSchemaV2Names.CAPACITY:
				this.currentCapacity = this.builder.createVehicleCapacity();
				break;
			case VehicleSchemaV2Names.SEATS:
				this.currentCapacity.setSeats( Integer.valueOf( atts.getValue( VehicleSchemaV2Names.PERSONS ) ) );
				break;
			case VehicleSchemaV2Names.STANDINGROOM:
				this.currentCapacity.setStandingRoom( Integer.valueOf( atts.getValue( VehicleSchemaV2Names.PERSONS ) ) );
				break;
			case VehicleSchemaV2Names.FREIGHTCAPACITY:
				this.currentFreightCapacity = this.builder.createFreigthCapacity();
				break;
			case VehicleSchemaV2Names.VOLUME:
				this.currentFreightCapacity.setVolume( Double.parseDouble( atts.getValue( VehicleSchemaV2Names.CUBICMETERS ) ) );
				break;
			case VehicleSchemaV2Names.WEIGHT:
				this.currentFreightCapacity.setWeight( Double.parseDouble( atts.getValue( VehicleSchemaV2Names.TONS ) ) );
				break;
			case VehicleSchemaV2Names.COSTINFORMATION:
				double fixedCostsPerDay = Double.parseDouble( atts.getValue( VehicleSchemaV2Names.FIXEDCOSTSPERDAY ) );
				double costsPerMeter = Double.parseDouble( atts.getValue( VehicleSchemaV2Names.COSTSPERMETER ) );
				double costsPerSecond = Double.parseDouble( atts.getValue( VehicleSchemaV2Names.COSTSPERSECOND ) );
				CostInformation currentCostInformation = this.builder.createCostInformation( fixedCostsPerDay, costsPerMeter, costsPerSecond );
				this.currentVehType.setCostInformation( currentCostInformation );
				break;
			case VehicleSchemaV2Names.VEHICLE:
				Id<VehicleType> typeId = Id.create( atts.getValue( VehicleSchemaV2Names.TYPE ), VehicleType.class );
				VehicleType type = this.vehicles.getVehicleTypes().get( typeId );
				if( type == null ){
					log.error( "VehicleType " + typeId + " does not exist." );
				}
				String idString = atts.getValue( VehicleSchemaV2Names.ID );
				Id<Vehicle> id = Id.create( idString, Vehicle.class );
				Vehicle v = this.builder.createVehicle( id, type );
				this.vehicles.addVehicle( v );
				break;
			case VehicleSchemaV2Names.PASSENGERCAREQUIVALENTS:
				this.currentVehType.setPcuEquivalents( Double.parseDouble( atts.getValue( VehicleSchemaV2Names.PCE ) ) );
				break;
			case VehicleSchemaV2Names.FLOWEFFICIENCYFACTOR:
				this.currentVehType.setFlowEfficiencyFactor( Double.parseDouble( atts.getValue( VehicleSchemaV2Names.FACTOR ) ) );
				break;
			case VehicleSchemaV2Names.ATTRIBUTES:
				log.warn( "attributes encountered; context.peek()=" + context.peek() );
				if( context.peek().equals( VehicleSchemaV2Names.VEHICLETYPE ) ){
					currAttributes = this.currentVehType.getAttributes();
					attributesReader.startTag( name, atts, context, currAttributes );
				} else if( context.peek().equals( VehicleSchemaV2Names.ENGINEINFORMATION ) ){
					currAttributes = this.currentEngineInformation.getAttributes();
					attributesReader.startTag( name, atts, context, currAttributes );
				} else{
					throw new RuntimeException( "encountered attributes for context in which they are not registered; context=" + context );
				}
				break;
			case VehicleSchemaV2Names.ATTRIBUTE:
				attributesReader.startTag( name, atts, context, currAttributes );
				break;
			case VehicleSchemaV2Names.NETWORKMODE:
				this.currentVehType.setNetworkMode( atts.getValue( VehicleSchemaV2Names.NETWORKMODE ) );
				break;
			case VehicleSchemaV2Names.DESCRIPTION:
				this.currentVehType.setDescription( atts.getValue( VehicleSchemaV2Names.DESCRIPTION ) );
				break;
			default:
				throw new RuntimeException( "encountered unknown tag=" + name + " in context=" + context );
		}

	}

}
