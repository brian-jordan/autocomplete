import java.util.*;

public class BinarySearchDevelopment {
	
	private static String[] data = {
			"banana", "watermelon", "applesauce", "jicama", "marsupial",
			"amphibian"
	};
	
	public static String[] getList(int repeats) {
		ArrayList<String> ret = new ArrayList<>();
		for(String s : data) {
			for(int k=0; k < repeats; k++) {
				ret.add(s);
			}
		}
		Collections.sort(ret);
		return ret.toArray(new String[0]);
	}
	
	public static int firstIndexFast(String[] values, String target,Comparator<String> comp) {
		
		int low = -1;
		int high = values.length-1;
		
		// values (low,high] contains target if target present in values
		
		return -1;
	}
	
	public static int firstIndex(String[] values, String target, Comparator<String> comp) {
		List<String> list = Arrays.asList(values);
		int index = Collections.binarySearch(list, target,comp);
		while (0 <= index && comp.compare(list.get(index),target) == 0) {
			index -= 1;
		}
		return index+1;
	}
	
	public static void main(String[] args) {
		String[] list = getList(10000);
		String target = data[2];
		CountedComparator<String> comp = new CountedComparator<>();
		int index = firstIndex(list, target,comp);
		//int index = firstIndexFast(list,target,comp);
		int comps = comp.getCount();
		int expected = 1 +  (int) Math.ceil(Math.log10(list.length)/Math.log10(2));
		System.out.printf("%s\t%d\t%d\t%d\n",target,index,expected,comps);
	}
}
