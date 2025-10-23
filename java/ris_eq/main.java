class Handler {
	boolean[] assigned;
	int numAssigned;

	public Handler(int n) {
		assigned = new boolean[n];
	}

	public synchronized int request() throws InterruptedException {
		while(numAssigned == assigned.length) {
			wait();
		}

		int i = 0;
		while(i < assigned.length && assigned[i]) i++;
		assigned[i] = true;

		numAssigned++;
		return i;
	}

	public synchronized void release(int k) throws InterruptedException {
		assigned[k] = false;
		numAssigned--;
		
		// le attese sono tutte equivalenti 
		notify();
	}
}

class User extends Thread {
	String name;
	Handler h;
	int numRequests;

	public User(String name, Handler h, int numRequests) {
		this.name = name;
		this.h = h;
		this.numRequests = numRequests;
	}

	public void run() {
		try {
			for(int i = 0; i < numRequests; i++) {
				// ottieni
				sleep((long) (Math.random() * 1000));
				int k = h.request();
				System.out.println(name + " ha ottenuto " + k);

				// rilascia
				sleep((long) (Math.random() * 1000));
				h.release(k);
				System.out.println(name + " ha rilasciato " + k);
			}
		} catch(InterruptedException e) {
			System.err.println(name + " e' stato interrotto");
		}
	}
}

class Main {
	public static void main(String[] main) {
		Handler h = new Handler(10);

		for(int i = 0; i < 10; i++) {
			(new User("Utilizzatore " + i, h, 100)).start();
		}
	}
}
