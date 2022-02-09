package net.plantabyte.cartopen.test;
import net.plantabyte.drptrace.*;
public class Main {
	public static void main(String[] args){
		try {
			System.out.println(Main.class);
			print("Test 1");
			Prototypes.test1();

			print("Test 2");
			Prototypes.test2();

			print("Test 3");
			Prototypes.test3();
		} catch (Exception ex){
			System.out.flush();
			ex.printStackTrace(System.err);
			System.err.flush();
			System.exit(1);
		}
		System.exit(0);
	}

	public static void print(Object... args){
		// python-style print function
		int i = 0;
		for(var o : args){
			if(i++ != 0){
				System.out.print(' ');
			}
			System.out.print(String.valueOf(o));
		}
		System.out.println();
	}
}