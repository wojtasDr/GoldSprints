package GS;

import java.util.ArrayList;
import java.util.List;

public class Utils {
	private int measurementCounter = 0;
	private Float velocity = 0.0f;
	private List<Integer> sensorRevolutions = new ArrayList<Integer>();
	
	public List<Integer> collectData(StringBuffer revolutions){
		System.out.println("Rev: " + revolutions);
		//System.out.println("Rev number:" + revolutions.substring(revolutions.indexOf(":")+1, revolutions.length()).trim());

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
		if (revolutionsList.size() >= 5) {
			for (Integer rev : revolutionsList) {
				velocity = velocity + (rev * 2.1f * 3.6f);
			}
			velocity = velocity / 5;
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
