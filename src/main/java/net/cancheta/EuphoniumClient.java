package net.cancheta;

//import net.cancheta.ai.AIController;
import net.fabricmc.api.ClientModInitializer;
import net.cancheta.ai.AIController;
import net.cancheta.ai.AIHelper;
import net.cancheta.test.AIHelperTest;
import net.cancheta.util.ModEventsRegister;

public class EuphoniumClient implements ClientModInitializer {
	AIController controller = new AIController();
	AIHelperTest test = new AIHelperTest();
	@Override
	public void onInitializeClient() {
		ModEventsRegister.registerEvents(controller);
	}
}
