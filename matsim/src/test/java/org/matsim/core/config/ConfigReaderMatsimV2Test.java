package org.matsim.core.config;

import org.junit.Assert;
import org.junit.Test;
import org.matsim.core.config.groups.ControllerConfigGroup;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Mostly tests for the alias functionality
 *
 * @author mrieser
 */
public class ConfigReaderMatsimV2Test {

	@Test
	public void testModuleNameAlias() {
		Config config = ConfigUtils.createConfig();
		ConfigReaderMatsimV2 r2 = new ConfigReaderMatsimV2(config);

		String xml = """
				<?xml version="1.0" ?>
				<!DOCTYPE config SYSTEM "http://www.matsim.org/files/dtd/config_v2.dtd">
				<config>
					<module name="theController">
						<param name="lastIteration" value="27"/>
					</module>
				</config>
				""";
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

		r2.getConfigAliases().addAlias("theController", ControllerConfigGroup.GROUP_NAME);
		r2.readStream(bais);

		Assert.assertEquals(27, config.controller().getLastIteration());
	}

	@Test
	public void testModuleNameAlias_noOldModules() {
		Config config = ConfigUtils.createConfig();
		ConfigReaderMatsimV2 r2 = new ConfigReaderMatsimV2(config);

		String xml = """
				<?xml version="1.0" ?>
				<!DOCTYPE config SYSTEM "http://www.matsim.org/files/dtd/config_v2.dtd">
				<config>
					<module name="controler">
						<param name="lastIteration" value="27"/>
					</module>
				</config>
				""";
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

		r2.readStream(bais);

		Assert.assertEquals(27, config.controller().getLastIteration());
		Assert.assertNull(config.getModules().get("controler"));
	}

	@Test
	public void testParamNameAlias() {
		Config config = ConfigUtils.createConfig();
		ConfigReaderMatsimV2 r2 = new ConfigReaderMatsimV2(config);

		String xml = """
				<?xml version="1.0" ?>
				<!DOCTYPE config SYSTEM "http://www.matsim.org/files/dtd/config_v2.dtd">
				<config>
					<module name="controler">
						<param name="theLastIteration" value="23"/>
					</module>
				</config>
				""";
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

		r2.getConfigAliases().addAlias("theLastIteration", "lastIteration");
		r2.readStream(bais);

		Assert.assertEquals(23, config.controller().getLastIteration());
	}

	@Test
	public void testModuleAndParamNameAlias() {
		Config config = ConfigUtils.createConfig();
		ConfigReaderMatsimV2 r2 = new ConfigReaderMatsimV2(config);

		String xml = """
				<?xml version="1.0" ?>
				<!DOCTYPE config SYSTEM "http://www.matsim.org/files/dtd/config_v2.dtd">
				<config>
					<module name="theController">
						<param name="theLastIteration" value="23"/>
					</module>
				</config>
				""";
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

		r2.getConfigAliases().addAlias("theController", ControllerConfigGroup.GROUP_NAME);
		r2.getConfigAliases().addAlias("theLastIteration", "lastIteration");
		r2.readStream(bais);

		Assert.assertEquals(23, config.controller().getLastIteration());
	}

	/**
	 * Test that a parameter can be renamed inside a renamed module.
	 */
	@Test
	public void testConditionalParamNameAliasWithModuleRenaming() {
		Config config = ConfigUtils.createConfig();
		ConfigReaderMatsimV2 r2 = new ConfigReaderMatsimV2(config);

		String xml = """
				<?xml version="1.0" ?>
				<!DOCTYPE config SYSTEM "http://www.matsim.org/files/dtd/config_v2.dtd">
				<config>
					<module name="the_network">
						<param name="input" value="my_network.xml.gz" />
					</module>
					<module name="the_plans">
						<param name="input" value="my_plans.xml.gz" />
					</module>
				</config>
				""";
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

		r2.getConfigAliases().addAlias("the_network", "network");
		r2.getConfigAliases().addAlias("the_plans", "plans");
		// for the path, the new name needs to be used:
		r2.getConfigAliases().addAlias("input", "inputNetworkFile", "network");
		r2.getConfigAliases().addAlias("input", "inputPlansFile", "plans");
		r2.readStream(bais);

		Assert.assertEquals("my_network.xml.gz", config.network().getInputFile());
		Assert.assertEquals("my_plans.xml.gz", config.plans().getInputFile());
	}

	/**
	 * Test that 2 aliases for the same parameter name resolve to
	 * different parameter names depending on in which module they are in.
	 */
	@Test
	public void testConditionalParamNameAlias() {
		Config config = ConfigUtils.createConfig();
		ConfigReaderMatsimV2 r2 = new ConfigReaderMatsimV2(config);

		String xml = """
				<?xml version="1.0" ?>
				<!DOCTYPE config SYSTEM "http://www.matsim.org/files/dtd/config_v2.dtd">
				<config>
					<module name="network">
						<param name="input" value="my_network.xml.gz" />
					</module>
					<module name="plans">
						<param name="input" value="my_plans.xml.gz" />
					</module>
				</config>
				""";
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

		r2.getConfigAliases().addAlias("input", "inputNetworkFile", "network");
		r2.getConfigAliases().addAlias("input", "inputPlansFile", "plans");
		r2.readStream(bais);

		Assert.assertEquals("my_network.xml.gz", config.network().getInputFile());
		Assert.assertEquals("my_plans.xml.gz", config.plans().getInputFile());
	}

	/**
	 * Test that an alias only matches if its path also matches.
	 */
	@Test
	public void testConditionalParamNameAlias2() {
		Config config = ConfigUtils.createConfig();
		ConfigReaderMatsimV2 r2 = new ConfigReaderMatsimV2(config);

		String xml = """
				<?xml version="1.0" ?>
				<!DOCTYPE config SYSTEM "http://www.matsim.org/files/dtd/config_v2.dtd">
				<config>
					<module name="network">
						<param name="inputNetworkFile" value="my_network.xml.gz" />
					</module>
					<module name="plans">
						<param name="inputNetworkFile" value="my_plans.xml.gz" />
					</module>
				</config>
				""";
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

		r2.getConfigAliases().addAlias("inputNetworkFile", "inputPlansFile", "plans");
		r2.readStream(bais);

		Assert.assertEquals("my_network.xml.gz", config.network().getInputFile());
		Assert.assertEquals("my_plans.xml.gz", config.plans().getInputFile());
	}

	/**
	 * Test that an alias only matches if its path also matches.
	 */
	@Test
	public void testConditionalParamNameAlias3() {
		Config config = ConfigUtils.createConfig();
		ConfigReaderMatsimV2 r2 = new ConfigReaderMatsimV2(config);

		String xml = """
				<?xml version="1.0" ?>
				<!DOCTYPE config SYSTEM "http://www.matsim.org/files/dtd/config_v2.dtd">
				<config>
					<module name="plans">
						<param name="inputPlansFile" value="my_plans.xml.gz" />
					</module>
				</config>
				""";
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

		r2.getConfigAliases().addAlias("inputPlansFile", "input", "inexistant");
		r2.readStream(bais); // if the alias were matched, it should produce an exception, as "input" is not known

		Assert.assertEquals("my_plans.xml.gz", config.plans().getInputFile());
	}

	/**
	 * Test that an alias only matches if its path also matches.
	 */
	@Test
	public void testConditionalParamNameAlias4() {
		Config config = ConfigUtils.createConfig();
		ConfigReaderMatsimV2 r2 = new ConfigReaderMatsimV2(config);

		String xml = """
				<?xml version="1.0" ?>
				<!DOCTYPE config SYSTEM "http://www.matsim.org/files/dtd/config_v2.dtd">
				<config>
					<module name="plans">
						<param name="inputPlansFile" value="my_plans.xml.gz" />
					</module>
				</config>
				""";
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

		r2.getConfigAliases().addAlias("inputPlansFile", "input", "plans", "inexistant");
		r2.readStream(bais); // if the alias were matched, it should produce an exception, as "input" is not known

		Assert.assertEquals("my_plans.xml.gz", config.plans().getInputFile());

	}

	/**
	 * Test that an alias also matches in nested parameter sets.
	 */
	@Test
	public void testAliasWithParamSets() {
		Config config = ConfigUtils.createConfig();
		ConfigReaderMatsimV2 r2 = new ConfigReaderMatsimV2(config);

		String xml = """
				<?xml version="1.0" ?>
				<!DOCTYPE config SYSTEM "http://www.matsim.org/files/dtd/config_v2.dtd">
				<config>
					<module name="planCalcScore">
						<param name="learningRate" value="1.0" />
						<param name="brainExpBeta" value="2.0" />

						<parameterset type="scoringParameters">
							<param name="lateArrival" value="-18" />
							<param name="earlyDeparture" value="-0" />
							<param name="performing" value="+6" />
							<param name="waiting" value="-0" />

							<parameterset type="modeParams">
								<param name="theMode" value="car"/>
								<param name="marginalUtilityOfTraveling_util_hr" value="-5.6" />
							</parameterset>
							<parameterset type="modeParams">
								<param name="theMode" value="unicycle"/>
								<param name="marginalUtilityOfTraveling_util_hr" value="-8.7" />
							</parameterset>
						</parameterset>
					</module>
				</config>
				""";
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

		r2.getConfigAliases().clearAliases();
		r2.getConfigAliases().addAlias("planCalcScore", "scoring");
		r2.getConfigAliases().addAlias("theLateArrival", "lateArrival", "scoring", "scoringParameters");
		r2.getConfigAliases().addAlias("theMode", "mode", "scoring", "scoringParameters", "modeParams");
		r2.readStream(bais);

		Assert.assertEquals(-5.6, config.scoring().getModes().get("car").getMarginalUtilityOfTraveling(), 1e-7);
		Assert.assertEquals(-8.7, config.scoring().getModes().get("unicycle").getMarginalUtilityOfTraveling(), 1e-7);
	}
}
