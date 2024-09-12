/**
 *  
 *  Interface not changed from the 2nd solution 
 */
package components;

public interface Node {
	public void collectPackage(Package p);
	public void deliverPackage(Package p);
	public void work();
	public String getName();
}
