package GS;

import java.util.ArrayList;
import java.util.List;

public class Utils {
	private int measurementCounter = 0;
	private Float velocity = 0.0f;
	private List<Integer> sensorRevolutions = new ArrayList<Integer>();
	
	public List<Integer> collectData(StringBuffer revolutions){
		Integer revNumber = Integer.parseInt(revolutions.substring(revolutions.indexOf(":") + 1, revolutions.length()).trim());

		if(sensorRevolutions.size() == 5){
			sensorRevolutions.remove(measurementCounter);
		}
		
		sensorRevolutions.add(measurementCounter, revNumber);

		measurementCounter++;
		if (measurementCounter == 5){
			measurementCounter = 0;
		}
		System.out.println("Rev list: " + sensorRevolutions.toString());
		return sensorRevolutions;
	}
	
	public Float countVelocity(List<Integer> revolutionsList){
		Float sum = 0.0f;
		if (revolutionsList.size() == 5) {
			for (Integer rev : revolutionsList) {
				sum = sum + (rev * 2.1f * 3.6f);
			}
			velocity = sum / 5;
		}
		
		System.out.println("V1: " + velocity + "km/h");
		return velocity;
	}
	
	public void ResetData(){
		measurementCounter = 0;
		velocity = 0.0f;
		sensorRevolutions = new ArrayList<Integer>();
	}

}
