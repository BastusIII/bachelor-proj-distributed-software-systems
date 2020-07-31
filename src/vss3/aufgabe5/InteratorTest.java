package vss3.aufgabe5;

public class InteratorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int workers = 12;
		int amountOfCities = 12;
        int initialSizeOfTasks = 1;
		int maxDepth = 1;
		for(; maxDepth<Integer.MAX_VALUE; maxDepth++){
			if(maxDepth>=amountOfCities-3){
				break;
			}
			initialSizeOfTasks *= amountOfCities-maxDepth;
			if(workers <= initialSizeOfTasks){
				break;
			}
		}
     
		System.out.println("Begin");
		TaskIterator test = new TaskIterator(maxDepth, amountOfCities, 0);
		int counter = 0;
		while(test.hasNext()){
			System.out.println("Next:");
			for(Integer j:test.next()){
				System.out.println(j);
			}
			
			//test.next();
			counter++;
			System.out.println();
		}
		System.out.println(initialSizeOfTasks+" : "+counter);

	}

}
