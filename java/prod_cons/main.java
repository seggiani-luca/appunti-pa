class Buffer {
	final int buf[];
	final int size;
	int beg;
	int end;

	Buffer(int size) {
		this.size = size;
		buf = new int[size];
	}

	boolean isFull() {
		return (end + 1) % size == beg; 
	}

	boolean isEmpty() {
		return end == beg;
	}

	synchronized void insert(int e) {
		while(isFull()) {
			System.out.println("insert() bloccato da buffer pieno");
			try {
				wait();
			} catch(InterruptedException ex) {}
		}

		System.out.println("insert() di " + e);
		buf[end] = e;
		end = (end + 1) % size;
		notifyAll();
	}

	synchronized int extract() {
		while(isEmpty()) {
			System.out.println("extract() bloccato da buffer vuoto");
			try {
				wait();
			} catch(InterruptedException ex) {}
		}

		int e = buf[beg];
		beg = (beg + 1) % size;
		System.out.println("extract() di " + e);
		notifyAll();

		return e;
	}
}

class Producer extends Thread {
	static int c = 0;
	
	Buffer b;
	String name;

	Producer(String name, Buffer b) {
		this.name = name;
		this.b = b;
	}

	public void run() {	
		while(true) {
			// aspetta un tempo casuale
			try {
				sleep((long) (Math.random() * 1000));
			} catch(InterruptedException ex) {}
		
			// inserisci
			System.out.println(name + " ha prodotto " + c);
			synchronized(Producer.class) {
				b.insert(c++);
			}
		}
	}
}

class Consumer extends Thread {
	Buffer b;
	String name;

	Consumer(String name, Buffer b) {
		this.name = name;
		this.b = b;
	}

	public void run() {
		while(true) {
			// aspetta un tempo casuale
			try {
				sleep((long) (Math.random() * 1000));
			} catch(InterruptedException ex) {}

			// estrai
			int c = b.extract();
			System.out.println(name + " ha consumato " + c);
		}
	}
}

class Main {
	static void main() {
		// crea buffer
		Buffer buf = new Buffer(10);

		// popola produttori
    for(int i = 0; i < 25; i++) {
      new Producer("Produttore " + i, buf).start(); 
		} 

		// popola consumatori
    for(int i = 0; i < 15; i++) {
      new Consumer("Consumatore " + i, buf).start(); 
		}
	
		// simula per un po'
		try {
			Thread.sleep(5000);
		} catch(InterruptedException ex) {}

		// termina
		System.exit(0);
	}
}
