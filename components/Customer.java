/**
 * 
 *  Class of a customer creates random packages insted of the main office and keeps track of them 
 */
package components;

import java.util.*;

import components.Package;

public class Customer implements Runnable {
	private static int countID=0;
	final private int customerID;
	private Address customerAddress;
	private int maxPack;
	private ArrayList<Package> packages=new ArrayList<Package>();
	private boolean threadSuspend = false;
	
	/**
	 * default Constructor
	 */
	public Customer() {
		maxPack = 5;
		Random r = new Random();
		customerAddress = new Address(r.nextInt(MainOffice.getHub().getBranches().size()), r.nextInt(999999)+100000);
		customerID = countID++;
	}
	
	/**
	 * creates random packages and adds them to the correct locations
	 */
	public void addPackage() {
		System.out.println(this);
		Random r = new Random();
		Package p;
		Branch br;
		Priority priority=Priority.values()[r.nextInt(3)];
		Address sender = this.customerAddress;
		Address dest = new Address(r.nextInt(MainOffice.getHub().getBranches().size()), r.nextInt(999999)+100000);

		switch (r.nextInt(3)){
		case 0:
			p = new SmallPackage(priority,  sender, dest, r.nextBoolean() );
			br = MainOffice.getHub().getBranches().get(sender.zip);
			br.addPackage(p);
			p.setBranch(br); 
			break;
		case 1:
			p = new StandardPackage(priority,  sender, dest, r.nextFloat()+(r.nextInt(9)+1));
			br = MainOffice.getHub().getBranches().get(sender.zip); 
			br.addPackage(p);
			p.setBranch(br); 
			break;
		case 2:
			p=new NonStandardPackage(priority,  sender, dest,  r.nextInt(1000), r.nextInt(500), r.nextInt(400));
			MainOffice.getHub().addPackage(p);
			break;
		default:
			p=null;
			return;
		}
		this.packages.add(p);
		MainOffice.getInstance().getPackages().add(p);
	}
	
	public synchronized void setSuspend() {
	   	threadSuspend = true;
	}

	public synchronized void setResume() {
	   	threadSuspend = false;
	   	notify();
	}
	
	

	@Override
	public String toString() {
		return "Customer [customerID = " + customerID + "]:";
	}

	/**
	 * creates a package every few seconds
	 * NOT DONE
	 */
	public void run() {
		while(true) {				
			while(packages.size() < maxPack) {
				Random r = new Random();
				synchronized (this) {
					addPackage();
				}
				try {
					Thread.sleep((r.nextInt(4)+2)*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				synchronized (this) {
					while(threadSuspend) {
						try {
							wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
			
			
			
			synchronized (this) {
				while(threadSuspend) {
					try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
