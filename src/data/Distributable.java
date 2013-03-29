package data;

public class Distributable {
	private String name;
	private byte[] data;
	
	public Distributable(String name, byte[] data) {
		this.name = name;
		this.data = data;
	}
	
	public String getName() {
		return name;
	}
	
	public byte[] getData() {
		return data;
	}
}
