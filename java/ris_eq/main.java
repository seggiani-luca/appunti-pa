class Gestore {
	boolean[] assigned;
	int numAssigned;

	public Gestore(int n) {
		assigned = new boolean[n];
	}

	public synchronized int richiesta() throws InterruptedException {
		while(numAssigned == assigned.length) {
			wait();
		}
	}

	public synchronized void rilascia() throws InterruptedException {

	}
}
